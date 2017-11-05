package com.pyjunkies.kemo.server;

import static com.pyjunkies.kemo.server.https.SslContextFactory.createSslContext;
import static com.pyjunkies.kemo.server.util.CommandLineParams.read;
import static com.pyjunkies.kemo.server.util.HandlerUtils.accessLog;
import static com.pyjunkies.kemo.server.util.HandlerUtils.addCacheHeaders;
import static com.pyjunkies.kemo.server.util.HandlerUtils.addHeaders;
import static io.undertow.servlet.Servlets.servlet;
import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.util.Optional;

import com.pyjunkies.kemo.server.https.SslContextFactory;
import com.pyjunkies.kemo.server.util.HttpsRedirectHandler;
import com.pyjunkies.kemo.server.util.SslConfiguration;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.servlet.api.*;
import org.jboss.logging.Logger;

import com.pyjunkies.kemo.server.logging.LoggerInitiliazer;
import com.pyjunkies.kemo.server.servlet.ChatServlet;
import com.pyjunkies.kemo.server.servlet.EmbeddedChatServlet;
import com.pyjunkies.kemo.server.servlet.ErrorReportingServlet;
import com.pyjunkies.kemo.server.servlet.WelcomeServlet;
import com.pyjunkies.kemo.server.util.CommandLineParams;
import com.pyjunkies.kemo.server.util.HandlerUtils.Constants;
import com.pyjunkies.kemo.server.websocket.MessagingWebSocketEndpoint;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.server.handlers.PathHandler;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import web.WebResourceManager;

import javax.net.ssl.SSLContext;

public class KemoServer {

    static {
        // Perform initial logger configuration
        LoggerInitiliazer.configureJavaLogging("logging.properties");
    }

    public static final Logger log = Logger.getLogger(KemoServer.class);

    public static void main(final String[] args) throws Exception {
        log.infov("Starting Kemo server with command line arguments : {0}", asList(args));
        // Read command line parameters
        CommandLineParams params = read(args);

        // Get bind address
        String bindAddress = params.get("--bind").orElse("127.0.0.1");
        // Get port
        Integer port = params.getInt("--port").orElse(8080);

        // Get production/development mode flag
        Boolean isProductionMode = params.getBool("--prod").orElse(Boolean.FALSE);
        GlobalSettings.prodMode = isProductionMode;

        // Get access log directory path
        Optional<String> accessLogDir = params.get("-a", "--accesslog");
        GlobalSettings.accessLogDir = accessLogDir;

        log.infof("Bind address : '%s'", bindAddress);
        log.infof("Production mode : '%s'", isProductionMode);

        // Read SSL configuration
        SslConfiguration sslConfiguration = new SslConfiguration(params);
        log.infof("SSL configuration : '%s'", sslConfiguration);

        // Prepare server handlers path and Undertow server
        PathHandler path = Handlers.path();

        final ServletContainer container = ServletContainer.Factory.newInstance();


        DeploymentInfo deploymentInfo = new DeploymentInfo()
                .setClassLoader(KemoServer.class.getClassLoader())
                .setContextPath("/")
                .addWelcomePage("index.html")
                .setResourceManager(new WebResourceManager())
                .addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME,
                        new WebSocketDeploymentInfo()
                                .setBuffers(new DefaultByteBufferPool(true, 100))
                                .addEndpoint(MessagingWebSocketEndpoint.class))
                .addServlet(servlet(WelcomeServlet.class)
                        .addMapping("/index.html"))
                .addServlet(servlet(ChatServlet.class)
                        .addMapping("/chat"))
                .addServlet(servlet(EmbeddedChatServlet.class)
                        .addMapping("/embedded"))
                .addServlet(servlet(ErrorReportingServlet.class)
                        .addMapping("/error/report-agent"))
                .addInitialHandlerChainWrapper(addCacheHeaders(".js", ".css", ".ico", ".png", ".jpg", ".gif"))
                .setDeploymentName("kemo.war");


        // Add global security headers
        deploymentInfo.addInitialHandlerChainWrapper(addHeaders((exchange) -> {
            if (isProductionMode) {
                // TODO Security headers are disabled due to possible cause of
                // our connectivity issues
                /*
                 * exchange.getResponseHeaders().add(Constants.ResponseHeader.
				 * CONTENT_SECURITY_POLICY,
				 * "default-src 'self' 'unsafe-eval' https://kemoundertow-krablak.rhcloud.com/; "
				 * +
				 * "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://fonts.gstatic.com; "
				 * +
				 * "font-src 'self' https://fonts.googleapis.com https://fonts.gstatic.com; "
				 * +
				 * "connect-src 'self' ws://kemoundertow-krablak.rhcloud.com wss://kemoundertow-krablak.rhcloud.com:8443;"
				 * );
				 * exchange.getResponseHeaders().add(Constants.ResponseHeader.
				 * HSTS, "max-age=31536000");
				 */
            } else {

                exchange.getResponseHeaders().add(Constants.ResponseHeader.CONTENT_SECURITY_POLICY,
                        "default-src 'self' 'unsafe-eval'; "
                                + "img-src 'self'; "
                                + "script-src 'self' 'unsafe-eval'; "
                                + "frame-src 'self'; "
                                + "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://fonts.gstatic.com; "
                                + "font-src 'self' https://fonts.googleapis.com https://fonts.gstatic.com; "
                                + format("connect-src 'self' ws://%s:%s wss://%s:%s;", bindAddress, port, bindAddress, sslConfiguration.isSslConfigured() ? sslConfiguration.getSslPortOpt().get() : port));
            }
        }));

        // When access log configuration is present add logging
        accessLogDir.ifPresent(logDirPath -> {
            deploymentInfo.addInitialHandlerChainWrapper(accessLog(logDirPath, "/messaging"));
        });

        DeploymentManager manager = container.addDeployment(deploymentInfo);
        manager.deploy();
        path.addPrefixPath("/", manager.start());

        // Prepare server
        Undertow.Builder builder = Undertow.builder()
                // Add handler on http or when SSL is configured add
                .addHttpListener(port, bindAddress, sslConfiguration.isSslConfigured() ? new HttpsRedirectHandler(sslConfiguration.getSslPortOpt().get()) : path)
                .setServerOption(UndertowOptions.ENABLE_HTTP2, Boolean.FALSE);

        // Continue only in case of all required config params
        if (sslConfiguration.isSslConfigured()) {
            SSLContext sslContext = createSslContext(sslConfiguration.getKeyStoreOpt().get(), sslConfiguration.getKeyPassOpt().get());
            builder.addHttpsListener(sslConfiguration.getSslPortOpt().get(), bindAddress, sslContext, path);
        } else {
            log.warnf("SSL is not configured. Following parameters must be set: --sslport,--sslkeystore,--sslpassword");
        }
        // Build server instance
        Undertow server = builder.build();
        server.start();

    }

}

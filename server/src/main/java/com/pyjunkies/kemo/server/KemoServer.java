package com.pyjunkies.kemo.server;

import static com.pyjunkies.kemo.server.util.CommandLineParams.read;
import static com.pyjunkies.kemo.server.util.HandlerUtils.accessLog;
import static com.pyjunkies.kemo.server.util.HandlerUtils.addCacheHeaders;
import static io.undertow.servlet.Servlets.servlet;
import static java.util.Arrays.asList;

import java.util.Optional;

import org.jboss.logging.Logger;

import com.pyjunkies.kemo.server.logging.LoggerInitiliazer;
import com.pyjunkies.kemo.server.servlet.ChatServlet;
import com.pyjunkies.kemo.server.servlet.EmbeddedChatServlet;
import com.pyjunkies.kemo.server.servlet.WelcomeServlet;
import com.pyjunkies.kemo.server.util.CommandLineParams;
import com.pyjunkies.kemo.server.websocket.MessagingWebSocketEndpoint;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import web.WebResourceManager;

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
		String bindAddress = params.get("-b", "--bind").orElse("localhost");

		// Get production/development mode flag
		Boolean isProductionMode = params.getBool("-m", "--mode").orElse(Boolean.FALSE);
		GlobalSettings.prodMode = isProductionMode;

		// Get access log directory path
		Optional<String> accessLogDir = params.get("-a", "--accesslog");

		log.infof("Bind address : '%s'", bindAddress);
		log.infof("Production mode : '%s'", isProductionMode);

		// Prepare server handers path and Undertow server
		PathHandler path = Handlers.path();
		Undertow server = Undertow.builder()
				.addHttpListener(8080, bindAddress)
				// .addHttpsListener(8443, "localhost",
				// createSslContext("/ssl-keystore.jks", "password"))
				.setHandler(path)
				.setServerOption(UndertowOptions.ENABLE_HTTP2, Boolean.TRUE)
				.build();
		server.start();

		final ServletContainer container = ServletContainer.Factory.newInstance();
		DeploymentInfo builder = new DeploymentInfo()
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
				.addInitialHandlerChainWrapper(addCacheHeaders(".js", ".css", ".ico", ".png"))
				.setDeploymentName("kemo.war");

		// When access log configuration is present add logging
		accessLogDir.ifPresent(logDirPath -> {
			builder.addInitialHandlerChainWrapper(accessLog(logDirPath, "/messaging"));
		});

		DeploymentManager manager = container.addDeployment(builder);
		manager.deploy();
		path.addPrefixPath("/", manager.start());

	}

}

package com.pyjunkies.kemo.server;

import static com.pyjunkies.kemo.server.util.HandlerUtils.addCacheHeaders;
import static io.undertow.servlet.Servlets.servlet;
import static java.lang.String.valueOf;

import java.util.Date;

import com.pyjunkies.kemo.server.servlet.EmbeddedChatServlet;
import com.pyjunkies.kemo.server.servlet.LabsServlet;
import com.pyjunkies.kemo.server.servlet.WelcomeServlet;
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

	public static void main(final String[] args) throws Exception {
		// Get IP from args
		String ip = args.length >= 1 ? args[0] : "localhost";
		// Get production mode flag
		Boolean prodMode = args.length == 2 ? Boolean.TRUE.toString().equalsIgnoreCase(args[1]) : Boolean.FALSE;

		// Prepare server handers path and Undertow server
		PathHandler path = Handlers.path();
		Undertow server = Undertow.builder()
				.addHttpListener(8080, ip)
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
						.addInitParam(Constants.Params.MODE_PROD, valueOf(prodMode))
						.addInitParam(Constants.Params.MODE_RES_VERSION, valueOf(new Date().getTime()))
						.addMapping("/index.html"))
				.addServlet(servlet(LabsServlet.class)
						.addInitParam(Constants.Params.MODE_PROD, valueOf(prodMode))
						.addInitParam(Constants.Params.MODE_RES_VERSION, valueOf(new Date().getTime()))
						.addMapping("/labs"))
				.addServlet(servlet(EmbeddedChatServlet.class)
						.addInitParam(Constants.Params.MODE_PROD, valueOf(prodMode))
						.addInitParam(Constants.Params.MODE_RES_VERSION, valueOf(new Date().getTime()))
						.addMapping("/embedded"))
				.addInitialHandlerChainWrapper(addCacheHeaders(".js", ".css", ".ico", ".png"))
				.setDeploymentName("kemo.war");

		DeploymentManager manager = container.addDeployment(builder);
		manager.deploy();
		path.addPrefixPath("/", manager.start());

	}

}

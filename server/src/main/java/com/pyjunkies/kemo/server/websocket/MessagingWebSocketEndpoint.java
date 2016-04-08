package com.pyjunkies.kemo.server.websocket;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/messaging/{" + MessagingWebSocketEndpoint.Constants.PATH_KEY + "}")
public class MessagingWebSocketEndpoint {

	interface Constants {
		String PATH_KEY = "key";
	}

	@OnMessage
	public void message(String message, Session session) {
		String openSessionKey = session.getPathParameters().get(Constants.PATH_KEY);
		for (Session curSession : session.getOpenSessions()) {
			String key = curSession.getPathParameters().get(Constants.PATH_KEY);
			if (key != null && key.equals(openSessionKey)) {
				curSession.getAsyncRemote().sendText(message);
			}
		}
	}

}

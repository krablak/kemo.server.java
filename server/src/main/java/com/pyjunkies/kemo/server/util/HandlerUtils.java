package com.pyjunkies.kemo.server.util;

import static io.undertow.util.HttpString.tryFromString;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

/**
 * Provides helper methods related to request handlers.
 * 
 * @author krablak
 *
 */
public class HandlerUtils {

	/**
	 * Utils related constants.
	 */
	interface Constants {
		interface ResponseHeader {
			HttpString CACHE_CONTROL = tryFromString("Cache-Control");
			HttpString EXPIRES = tryFromString("Expires");
		}
	}

	private HandlerUtils() {
	}

	/**
	 * Adds cache controls into request which has relative path ending with
	 * given strings.
	 * 
	 * @param endsWith
	 *            requests endings to which will be cache controls applied.
	 * @return ready to use cache control adding wrapper.
	 */
	public static HandlerWrapper addCacheHeaders(String... endsWith) {
		return new HandlerWrapper() {
			@Override
			public HttpHandler wrap(final HttpHandler handler) {
				return new HttpHandler() {
					@Override
					public void handleRequest(final HttpServerExchange exchange) throws Exception {
						if (endsWith(exchange.getRelativePath(), endsWith)) {
							handler.handleRequest(exchange);
							exchange.getResponseHeaders().add(Constants.ResponseHeader.CACHE_CONTROL, "max-age=315360000, public");
							exchange.getResponseHeaders().add(Constants.ResponseHeader.EXPIRES, "Thu, 31 Dec 2037 23:55:55 GMT");
						} else {
							handler.handleRequest(exchange);
						}
					}
				};
			}
		};
	}

	/**
	 * Returns <code>true</code> when relative path ends with one of given
	 * endings.
	 * 
	 * @param relativePath
	 *            path to be checked.
	 * @param endsWith
	 *            checked relative paths endings.
	 * @return ends check result.
	 */
	private static boolean endsWith(String relativePath, String... endsWith) {
		boolean endsWithRes = false;
		if (relativePath != null) {
			for (String curEndsWith : endsWith) {
				endsWithRes = relativePath.endsWith(curEndsWith);
				if (endsWithRes) {
					break;
				}
			}
		}
		return endsWithRes;
	}

}

package com.pyjunkies.kemo.server.util;

import static com.pyjunkies.kemo.server.logging.AccessLogWriterUtils.toMessage;
import static io.undertow.util.HttpString.tryFromString;

import java.util.Date;

import com.pyjunkies.kemo.server.logging.AccessLogWriter;

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
						handler.handleRequest(exchange);
						if (endsWith(exchange.getRelativePath(), endsWith)) {
							exchange.getResponseHeaders().add(Constants.ResponseHeader.CACHE_CONTROL, "max-age=315360000, public");
							exchange.getResponseHeaders().add(Constants.ResponseHeader.EXPIRES, "Thu, 31 Dec 2037 23:55:55 GMT");
						}
					}
				};
			}
		};
	}

	public static HandlerWrapper accessLog(String logDirPath, String... excludeStartsWith) {
		// Access log writer
		final AccessLogWriter logWriter = new AccessLogWriter(logDirPath);
		// Prepare wrapper instance
		return new HandlerWrapper() {
			@Override
			public HttpHandler wrap(final HttpHandler handler) {
				return new HttpHandler() {
					@Override
					public void handleRequest(final HttpServerExchange exchange) throws Exception {
						final long startTime = new Date().getTime();
						try {
							// Handle request
							handler.handleRequest(exchange);
						} finally {
							final long endTime = new Date().getTime();
							// Exclude requests starting with excluded paths
							if (!startsWith(exchange.getRelativePath(), excludeStartsWith)) {
								// Log request
								logWriter.write(toMessage(exchange, endTime - startTime));
							}
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

	/**
	 * Returns <code>true</code> when relative path starts with one of given
	 * strings.
	 * 
	 * @param relativePath
	 *            path to be checked.
	 * @param startsWith
	 *            checked relative paths start strings.
	 * @return start strings check result.
	 */
	private static boolean startsWith(String relativePath, String... startsWith) {
		boolean startsWithRes = false;
		if (relativePath != null) {
			for (String curStartsWith : startsWith) {
				startsWithRes = relativePath.startsWith(curStartsWith);
				if (startsWithRes) {
					break;
				}
			}
		}
		return startsWithRes;
	}

}

package com.pyjunkies.kemo.server.util;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.jboss.logging.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Simple handler redirecting unsecured requests to https.
 */
public class HttpsRedirectHandler implements HttpHandler {

    public static final Logger log = Logger.getLogger(HttpsRedirectHandler.class);

    private final Function<String, String> urlSwitchFn;

    public HttpsRedirectHandler(Integer httpsPort) {
        this.urlSwitchFn = toHttpsUrlFn(httpsPort);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.setStatusCode(StatusCodes.FOUND);
        exchange.getResponseHeaders().put(Headers.LOCATION, urlSwitchFn.apply(exchange.getRequestURL()));
        exchange.endExchange();
    }

    /**
     * Creates function converting URL strings into HTTPS variants specific for redirecting purposes.
     *
     * @param port configured port for https. Should be not null.
     * @return ready to use function.
     */
    static Function<String, String> toHttpsUrlFn(Integer port) {
        requireNonNull(port, "Cannot create redirect URL converter from null port.");
        return srcUrlStr -> {
            requireNonNull(srcUrlStr);
            try {
                URL srcUrl = new URL(srcUrlStr);
                StringBuilder addrBuilder = new StringBuilder();
                addrBuilder.append("https://");
                addrBuilder.append(srcUrl.getHost());
                if (port != 443) {
                    addrBuilder.append(":").append(port);
                }
                if (!srcUrl.getPath().trim().isEmpty()) {
                    if (!srcUrl.getPath().startsWith("/")) {
                        addrBuilder.append("/");
                    }
                    addrBuilder.append(srcUrl.getPath());
                }
                return addrBuilder.toString();
            } catch (MalformedURLException e) {
                String errMsg = format("Cannot read '%s' as URL. Https version of URL cannot be created.");
                log.error(errMsg, e);
                throw new RuntimeException(errMsg, e);
            }
        };
    }

}

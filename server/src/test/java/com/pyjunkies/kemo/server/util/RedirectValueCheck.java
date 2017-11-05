package com.pyjunkies.kemo.server.util;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributes;
import org.junit.Test;

import java.net.URL;

import static com.pyjunkies.kemo.server.util.HttpServerExchangeStub.createHttpExchange;
import static com.pyjunkies.kemo.server.util.HttpServerExchangeStub.setSource;
import static org.junit.Assert.assertEquals;

public class RedirectValueCheck {

    @Test
    public void redirect0() {
        ExchangeAttribute parserRes = ExchangeAttributes.parser(getClass().getClassLoader()).parse("https://%h%U");
        String redirectPath = parserRes.readAttribute(createHttpExchange(setSource("localhost", 80)));
        assertEquals("https://localhost", redirectPath);
    }

    @Test
    public void redirect1() throws Exception {
        URL url = new URL("http://127.0.0.1");
        String protocol = url.getProtocol();
        String host = url.getHost();
        int port = url.getPort();
        String path = url.getPath();

        ExchangeAttribute parserRes = ExchangeAttributes.parser(getClass().getClassLoader()).parse("https://%h%U");
        String redirectPath = parserRes.readAttribute(createHttpExchange(setSource("127.0.0.1", 80)));
        assertEquals("https://127.0.0.1", redirectPath);
    }

}

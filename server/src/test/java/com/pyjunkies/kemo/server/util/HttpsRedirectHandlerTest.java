package com.pyjunkies.kemo.server.util;

import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.*;

public class HttpsRedirectHandlerTest {

    @Test(expected = NullPointerException.class)
    public void toHttpsUrlFnWithNullPort() throws Exception {
        HttpsRedirectHandler.toHttpsUrlFn(null);
    }

    @Test
    public void toHttpsUrlCustomPort() throws Exception {
        Function<String, String> fn = HttpsRedirectHandler.toHttpsUrlFn(8443);
        assertEquals("https://127.0.0.1:8443", fn.apply("http://127.0.0.1"));
        assertEquals("https://127.0.0.1:8443/", fn.apply("http://127.0.0.1/"));
        assertEquals("https://127.0.0.1:8443/test", fn.apply("http://127.0.0.1/test"));
        assertEquals("https://127.0.0.1:8443/test/", fn.apply("http://127.0.0.1/test/"));
        assertEquals("https://127.0.0.1:8443/index.html", fn.apply("http://127.0.0.1/index.html"));
        assertEquals("https://127.0.0.1:8443/test/index.html", fn.apply("http://127.0.0.1/test/index.html"));
    }

    @Test
    public void toHttpsUrlDefaultPort() throws Exception {
        Function<String, String> fn = HttpsRedirectHandler.toHttpsUrlFn(443);
        assertEquals("https://127.0.0.1", fn.apply("http://127.0.0.1"));
        assertEquals("https://127.0.0.1/", fn.apply("http://127.0.0.1/"));
        assertEquals("https://127.0.0.1/test", fn.apply("http://127.0.0.1/test"));
        assertEquals("https://127.0.0.1/test/", fn.apply("http://127.0.0.1/test/"));
        assertEquals("https://127.0.0.1/index.html", fn.apply("http://127.0.0.1/index.html"));
        assertEquals("https://127.0.0.1/test/index.html", fn.apply("http://127.0.0.1/test/index.html"));
    }

}
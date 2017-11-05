package com.pyjunkies.kemo.server.util;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.undertow.connector.ByteBufferPool;
import io.undertow.server.ConnectorStatisticsImpl;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import io.undertow.server.protocol.http.HttpServerConnection;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import io.undertow.util.Protocols;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

import org.xnio.OptionMap;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.conduits.ConduitStreamSinkChannel;
import org.xnio.conduits.ConduitStreamSourceChannel;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.StreamSourceConduit;

public class HttpServerExchangeStub {

    public static HttpServerExchange createHttpExchange(Consumer<HttpServerExchange>... updateFn) {
        HeaderMap headerMap = new HeaderMap();
        StreamConnection streamConnection = createStreamConnection();
        OptionMap options = OptionMap.EMPTY;
        ServerConnection connection = new HttpServerConnection(streamConnection, null, null, options, 0, null);
        HttpServerExchange exchange = createHttpExchange(connection, headerMap);
        if (updateFn != null) {
            for (Consumer<HttpServerExchange> curUpdateFn : updateFn) {
                curUpdateFn.accept(exchange);
            }
        }
        return exchange;
    }

    public static Consumer<HttpServerExchange> setSource(String hostname, int port) {
        return ex -> ex.setSourceAddress(new InetSocketAddress(hostname, port));
    }


    private static StreamConnection createStreamConnection() {
        StreamConnection streamConnection = mock(StreamConnection.class);
        ConduitStreamSinkChannel sinkChannel = createSinkChannel();
        when(streamConnection.getSinkChannel()).thenReturn(sinkChannel);
        ConduitStreamSourceChannel sourceChannel = createSourceChannel();
        when(streamConnection.getSourceChannel()).thenReturn(sourceChannel);
        XnioIoThread ioThread = mock(XnioIoThread.class);
        when(streamConnection.getIoThread()).thenReturn(ioThread);
        return streamConnection;
    }

    private static ConduitStreamSinkChannel createSinkChannel() {
        try {
            StreamSinkConduit sinkConduit = mock(StreamSinkConduit.class);
            when(sinkConduit.write(any(ByteBuffer.class))).thenReturn(1);
            ConduitStreamSinkChannel sinkChannel = new ConduitStreamSinkChannel(null, sinkConduit);
            return sinkChannel;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ConduitStreamSourceChannel createSourceChannel() {
        StreamSourceConduit sourceConduit = mock(StreamSourceConduit.class);
        ConduitStreamSourceChannel sourceChannel = new ConduitStreamSourceChannel(null, sourceConduit);
        return sourceChannel;
    }

    private static HttpServerExchange createHttpExchange(ServerConnection connection, HeaderMap headerMap) {
        HttpServerExchange httpServerExchange = new HttpServerExchange(connection, null, headerMap, 200);
        httpServerExchange.setRequestMethod(new HttpString("GET"));
        httpServerExchange.setProtocol(Protocols.HTTP_1_1);
        httpServerExchange.setSourceAddress(new InetSocketAddress("127.0.0.1", 80));
        return httpServerExchange;
    }

}

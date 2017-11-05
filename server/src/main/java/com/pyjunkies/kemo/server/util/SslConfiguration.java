package com.pyjunkies.kemo.server.util;

import java.util.Optional;

/**
 * Reads and provides all configuration parameters related to SSL.
 */
public class SslConfiguration {

    private Optional<Integer> sslPortOpt = Optional.empty();

    private Optional<String> keyStoreOpt = Optional.empty();

    private Optional<String> keyPassOpt = Optional.empty();

    public SslConfiguration(CommandLineParams params) {
        this.sslPortOpt = params.getInt("--sslport");
        this.keyStoreOpt = params.get("--sslkeystore");
        this.keyPassOpt = params.get("--sslpassword");
    }

    public Optional<Integer> getSslPortOpt() {
        return sslPortOpt;
    }

    public Optional<String> getKeyStoreOpt() {
        return keyStoreOpt;
    }

    public Optional<String> getKeyPassOpt() {
        return keyPassOpt;
    }

    public boolean isSslConfigured() {
        return sslPortOpt.isPresent() && keyPassOpt.isPresent() && keyStoreOpt.isPresent();
    }

    @Override
    public String toString() {
        return "SslConfiguration{" +
                "sslPortOpt=" + sslPortOpt +
                ", keyStoreOpt=" + keyStoreOpt +
                ", keyPassOpt=" + keyPassOpt +
                '}';
    }
}

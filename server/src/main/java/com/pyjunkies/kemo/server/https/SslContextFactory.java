package com.pyjunkies.kemo.server.https;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SslContextFactory {

	public final static TrustManager[] TRUST_ALL_CERTS = new X509TrustManager[] { new DummyTrustManager() };

	interface Constants {
		String KEYSTORE_TYPE = "JKS";
	}

	private SslContextFactory() {
	}

	public static SSLContext createSslContext(String keyStoreName, String keyStorePassword) throws IOException {
		KeyStore keyStore = loadKeyStore(keyStoreName, keyStorePassword);

		KeyManager[] keyManagers = buildKeyManagers(keyStore, keyStorePassword.toCharArray());
		TrustManager[] trustManagers = buildTrustManagers(null);

		SSLContext sslContext;
		try {
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagers, trustManagers, null);
		} catch (NoSuchAlgorithmException | KeyManagementException exc) {
			throw new IOException("Unable to create and initialise the SSLContext", exc);
		}

		return sslContext;
	}

	private static KeyStore loadKeyStore(final String location, String storePassword) throws IOException {
		try (InputStream stream = SslContextFactory.class.getResourceAsStream(location)) {
			KeyStore loadedKeystore = KeyStore.getInstance(Constants.KEYSTORE_TYPE);
			loadedKeystore.load(stream, storePassword.toCharArray());
			return loadedKeystore;
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException exc) {
			throw new IOException(String.format("Unable to load KeyStore %s", location), exc);
		}
	}

	private static TrustManager[] buildTrustManagers(final KeyStore trustStore) throws IOException {
		TrustManager[] trustManagers = null;
		if (trustStore == null) {
			try {
				TrustManagerFactory trustManagerFactory = TrustManagerFactory
						.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				trustManagerFactory.init(trustStore);
				trustManagers = trustManagerFactory.getTrustManagers();
			} catch (NoSuchAlgorithmException | KeyStoreException exc) {
				throw new IOException("Unable to initialise TrustManager[]", exc);
			}
		} else {
			trustManagers = TRUST_ALL_CERTS;
		}
		return trustManagers;
	}

	private static KeyManager[] buildKeyManagers(final KeyStore keyStore, char[] storePassword)
			throws IOException {
		KeyManager[] keyManagers;
		try {
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory
					.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, storePassword);
			keyManagers = keyManagerFactory.getKeyManagers();
		} catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException exc) {
			throw new IOException("Unable to initialise KeyManager[]", exc);
		}
		return keyManagers;
	}
}

class DummyTrustManager implements X509TrustManager {

	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[] {};
	}

	public void checkClientTrusted(X509Certificate[] certs, String authType) {
	}

	public void checkServerTrusted(X509Certificate[] certs, String authType) {
	}
}

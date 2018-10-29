package com.rs.ssl.config;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

//	@Qualifier("SSLRestOperations")
	@Bean(name = "SSLRestOperations" )
	public RestTemplate restOperations(ClientHttpRequestFactory clientHttpRequestFactory) throws Exception {
		System.out.println("RestClientConfig#restOperations(ClientRequestFactory clientRequestFactoty");
		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
		return restTemplate;
	}

	@Bean
	public ClientHttpRequestFactory clientHttpRequestFactory(HttpClient httpClient) {
		System.out.println("RestClientConfig#clientHttpRequestFactory");
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		// timeout
		requestFactory.setReadTimeout(60 * 1000);
		requestFactory.setConnectTimeout(60 * 1000);
		return requestFactory;
	}

	@Bean
	public HttpClient httpClient(@Value("${keystore.file}") Resource file, @Value("${keystore.pass}") String password)
			throws Exception {
		System.out.println("RestClientConfig#httpClient");
		System.out.println(file.getFilename());
		System.out.println(password);
		String keystorePassword = password;

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

		InputStream instream = file.getInputStream();
		InputStream instreamKey = file.getInputStream();
		try {
			keyStore.load(instreamKey, keystorePassword.toCharArray());
			trustStore.load(instream, keystorePassword.toCharArray());

			kmf.init(keyStore, keystorePassword.toCharArray());
			tmf.init(trustStore);

		} finally {
			instream.close();
			instreamKey.close();
		}

		@SuppressWarnings("deprecation")
		SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy())
				.loadKeyMaterial(trustStore, keystorePassword.toCharArray()).build();

		SSLSocketFactory socketFactory = sslcontext.getSocketFactory();
		SSLSocket socket = (SSLSocket) socketFactory.createSocket();

		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1.2" }, null,
				new NoopHostnameVerifier());

		return HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()) // HostnameVerifier
																						// NoopHostnameVerifier
				.setSSLSocketFactory(sslsf).build();
	}
}

package com.rs.ssl.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class SampleController {
	private static final String SERVER_URI = "https://localhost:4014/test";
	private static final String CERT_PATH = "src/main/resources/springboot-ssl-client.jks";
	private static final String KEYSTORE_PASS = "123456";

	@Autowired
	@Qualifier("SSLRestOperations")
	private RestTemplate restTemplate;

	@GetMapping("/hello")
	public String sayHello() {
		File file = new File(CERT_PATH);
//		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> forEntity = restTemplate.getForEntity(SERVER_URI, String.class);
		return forEntity.getBody();
	}

//	public RestTemplate getRestTemplate() throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
//			IOException, KeyManagementException, UnrecoverableKeyException {
//		RestTemplate restTemplate = new RestTemplate();
//		File file = new File(CERT_PATH);
//		InputStream is = new FileInputStream(file);
//		KeyStore ks = KeyStore.getInstance("JKS");
//		ks.load(is, KEYSTORE_PASS.toCharArray());
//		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(ks)
//				.loadKeyMaterial(ks, KEYSTORE_PASS.toCharArray()).build();
//		HttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()) // HostnameVerifier
//				// NoopHostnameVerifier
//				.build();
//		
//	}
}

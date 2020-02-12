/*******************************************************************************
 * Copyright 2020 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package org.sdo.iotplatformsdk.ocs.fsimpl.fs;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.sdo.iotplatformsdk.common.rest.To0Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * This class is responsible for sending the HTTPS request to TO0Scheduler.
 */
@Component
public class FsRestClient {

  private final Logger logger = LoggerFactory.getLogger(FsRestClient.class);

  @Value("${server.ssl.key-store-type}")
  private String keyStoreType;

  @Value("${server.ssl.trust-store-type}")
  private String trustStoreType;

  @Value("${server.ssl.key-store}")
  private String keyStoreFile;

  @Value("${server.ssl.key-store-password}")
  private String keyStorePwd;

  @Value("${server.ssl.trust-store}")
  private String trustStoreFile;

  @Value("${server.ssl.trust-store-password}")
  private String trustStorePwd;

  // rest api url of To0Scheduler.
  @Value("${to0.rest.api}")
  private String to0RestApi;

  public FsRestClient() {}

  private RestTemplate getRestTemplate() {
    RestTemplate template = new RestTemplate(getRequestFactory());
    return template;
  }

  /**
   * Returns an instance of {@link ClientHttpRequestFactory}.
   *
   * <p>The keystore and truststore files are read to create {@link SSLContext}
   * instance, that is used to generate the http client. If the keystore and
   * truststore files are not valid, no request will be made by any method of this
   * application.
   *
   * @return http request factory.
   */
  private HttpComponentsClientHttpRequestFactory getRequestFactory() {
    try {

      KeyStore identityKeyStore = KeyStore.getInstance(keyStoreType);
      File keystoreFile = new File(keyStoreFile);
      FileInputStream identityKeyStoreFile = new FileInputStream(keystoreFile);
      identityKeyStore.load(identityKeyStoreFile, keyStorePwd.toCharArray());

      KeyManagerFactory kmf =
          KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(identityKeyStore, keyStorePwd.toCharArray());
      KeyManager[] km = kmf.getKeyManagers();

      KeyStore trustKeyStore = KeyStore.getInstance(trustStoreType);
      File truststoreFile = new File(trustStoreFile);
      FileInputStream trustKeyStoreFile = new FileInputStream(truststoreFile);
      trustKeyStore.load(trustKeyStoreFile, trustStorePwd.toCharArray());

      TrustManagerFactory tmf =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(trustKeyStore);
      TrustManager[] tm = tmf.getTrustManagers();

      SSLContext sslContext = new SSLContextBuilder().build();
      sslContext.init(km, tm, new SecureRandom());

      SSLConnectionSocketFactory csf =
          new SSLConnectionSocketFactory(sslContext, new DefaultHostnameVerifier());
      CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
      HttpComponentsClientHttpRequestFactory requestFactory =
          new HttpComponentsClientHttpRequestFactory();
      requestFactory.setHttpClient(httpClient);
      return requestFactory;
    } catch (Exception e) {
      logger.debug(e.getMessage(), e);
      logger.error(e.getMessage());
      return null;
    }
  }

  protected List<ClientHttpRequestInterceptor> getInspectors() {
    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
    // add auth headers
    return interceptors;
  }

  /**
   * Send a POST request with request body as {@link To0Request}.
   *
   * @param request content to be sent in request body.
   */
  public void postDevicesForTo0(final To0Request request) {
    try {
      final List<ClientHttpRequestInterceptor> interceptors = getInspectors();
      interceptors
          .add(new FsRequestInterceptor("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE));

      final RestTemplate restTemplate = getRestTemplate();
      restTemplate.setInterceptors(interceptors);

      restTemplate.postForObject(to0RestApi, request, String.class);
    } catch (Exception e) {
      logger.debug("Error occurred while sending devices for TO0. " + e.getMessage(), e);
    }
  }
}

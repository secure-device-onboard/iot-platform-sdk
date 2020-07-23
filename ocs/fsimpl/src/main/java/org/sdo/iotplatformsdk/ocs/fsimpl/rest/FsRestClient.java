// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ocs.fsimpl.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.sdo.iotplatformsdk.common.rest.To0Request;
import org.sdo.iotplatformsdk.ocs.fsimpl.fs.FsPropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for sending the HTTPS request to TO0Scheduler.
 */
public class FsRestClient {

  private final Logger logger = LoggerFactory.getLogger(FsRestClient.class);
  private final String keyStoreType = FsPropertiesLoader.getProperty("server.ssl.key-store-type");
  private final String trustStoreType =
      FsPropertiesLoader.getProperty("server.ssl.trust-store-type");
  private final String keyStoreFile = FsPropertiesLoader.getProperty("server.ssl.key-store");
  private final String keyStorePwd =
      FsPropertiesLoader.getProperty("server.ssl.key-store-password");
  private final String trustStoreFile = FsPropertiesLoader.getProperty("server.ssl.trust-store");
  private final String trustStorePwd =
      FsPropertiesLoader.getProperty("server.ssl.trust-store-password");
  // rest api url of To0Scheduler.
  private final String to0RestApi = FsPropertiesLoader.getProperty("to0.rest.api");
  private final Duration timeout = Duration.ofSeconds(5);

  private SSLContext sslContext;

  /**
   * Constructor.
   */
  public FsRestClient() {}

  /**
   * Returns an instance of {@link SSLContext}.
   *
   * <p>The keystore and truststore files are read to create {@link SSLContext}
   * instance, that is used to generate the http client. If the keystore and
   * truststore files are not valid, no request will be made by any method of this
   * application.
   *
   * @return sslContext.
   */
  private SSLContext sslContext() {
    if (null == sslContext) {
      try {
        final KeyStore identityKeyStore = KeyStore.getInstance(keyStoreType);
        final File keystoreFile = new File(keyStoreFile);
        final FileInputStream identityKeyStoreFile = new FileInputStream(keystoreFile);
        identityKeyStore.load(identityKeyStoreFile, keyStorePwd.toCharArray());

        final KeyManagerFactory kmf =
            KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(identityKeyStore, keyStorePwd.toCharArray());
        final KeyManager[] km = kmf.getKeyManagers();

        final KeyStore trustKeyStore = KeyStore.getInstance(trustStoreType);
        final File truststoreFile = new File(trustStoreFile);
        final FileInputStream trustKeyStoreFile = new FileInputStream(truststoreFile);
        trustKeyStore.load(trustKeyStoreFile, trustStorePwd.toCharArray());

        final TrustManagerFactory tmf =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustKeyStore);
        final TrustManager[] tm = tmf.getTrustManagers();

        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(km, tm, new SecureRandom());

      } catch (Exception e) {
        logger.debug(e.getMessage(), e);
        logger.error(e.getMessage());
      }
    }
    return sslContext;
  }

  /**
   * Send a POST request with request body as {@link To0Request}.
   *
   * @param request content to be sent in request body.
   */
  public void postDevicesForTo0(final To0Request request) {
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    HttpClient httpClient = HttpClient.newBuilder().sslContext(sslContext()).connectTimeout(timeout)
        .executor(executor).build();
    try {
      final String requestBody = new ObjectMapper().writeValueAsString(request);
      final HttpRequest.Builder httpRequestBuilder =
          HttpRequest.newBuilder().header("Content-Type", "application/json");
      final HttpRequest httpRequest = httpRequestBuilder.uri(URI.create(to0RestApi))
          .POST(BodyPublishers.ofString(requestBody)).build();
      final HttpResponse<String> httpResponse =
          httpClient.send(httpRequest, BodyHandlers.ofString());
      if (httpResponse.statusCode() != 200) {
        throw new IOException(httpResponse.toString() + " " + httpResponse.body());
      }
    } catch (Exception e) {
      logger.error("Error occurred while sending devices for TO0. " + e.getMessage());
      logger.debug(e.getMessage(), e);
    } finally {
      // avoiding memory leaks.
      executor.shutdownNow();
      httpClient = null;
    }
  }
}

// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.to0scheduler.rest;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.sdo.iotplatformsdk.common.protocol.config.SslContextFactory;
import org.sdo.iotplatformsdk.to0scheduler.to0service.To0PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates and returns a custom {@link SSLContext} by loading a separate keystore and truststore.
 * Used specifically for outgoing connections to Owner Companion Service.
 */
public class To0CustomSslContextFactory extends SslContextFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(To0CustomSslContextFactory.class);

  private final String keyStoreType = To0PropertiesLoader.getProperty("server.ssl.key-store-type");
  private final String trustStoreType =
      To0PropertiesLoader.getProperty("server.ssl.trust-store-type");
  private final String keyStoreFile = To0PropertiesLoader.getProperty("server.ssl.key-store");
  private final String keyStorePwd =
      To0PropertiesLoader.getProperty("server.ssl.key-store-password");
  private final String trustStoreFile = To0PropertiesLoader.getProperty("server.ssl.trust-store");
  private final String trustStorePwd =
      To0PropertiesLoader.getProperty("server.ssl.trust-store-password");

  public To0CustomSslContextFactory(SecureRandom secureRandom) {
    super(secureRandom);
  }

  @Override
  public SSLContext getObject() {
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

      final SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(km, tm, getSecureRandom());
      return sslContext;
    } catch (Exception e) {
      LOGGER.error("Error occurred while creating ssl context. ", e.getMessage());
      LOGGER.debug(e.getMessage(), e);
      return null;
    }
  }
}

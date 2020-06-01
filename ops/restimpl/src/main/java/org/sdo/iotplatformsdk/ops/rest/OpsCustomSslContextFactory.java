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

package org.sdo.iotplatformsdk.ops.rest;

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
import org.sdo.iotplatformsdk.ops.opsimpl.OpsPropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates and returns a custom {@link SSLContext} by loading a separate keystore and truststore.
 */
public class OpsCustomSslContextFactory extends SslContextFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(OpsCustomSslContextFactory.class);

  private final String keyStoreType = OpsPropertiesLoader.getProperty("client.ssl.key-store-type");
  private final String trustStoreType =
      OpsPropertiesLoader.getProperty("client.ssl.trust-store-type");
  private final String keyStoreFile = OpsPropertiesLoader.getProperty("client.ssl.key-store");
  private final String keyStorePwd =
      OpsPropertiesLoader.getProperty("client.ssl.key-store-password");
  private final String trustStoreFile = OpsPropertiesLoader.getProperty("client.ssl.trust-store");
  private final String trustStorePwd =
      OpsPropertiesLoader.getProperty("client.ssl.trust-store-password");

  public OpsCustomSslContextFactory(SecureRandom secureRandom) {
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

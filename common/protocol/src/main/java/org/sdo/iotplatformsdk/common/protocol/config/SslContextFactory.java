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

package org.sdo.iotplatformsdk.common.protocol.config;

import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import org.slf4j.LoggerFactory;

/**
 * Create and returns an instance of {@link SSLContext}.
 */
public class SslContextFactory implements ObjectFactory<SSLContext> {

  private final SecureRandom secureRandom;
  private static SSLContext sslContext;

  public SslContextFactory(SecureRandom secureRandom) {
    this.secureRandom = secureRandom;
  }

  @Override
  public SSLContext getObject() {
    if (null == sslContext) {
      try {
        initializeObject();
      } catch (KeyManagementException | NoSuchAlgorithmException e) {
        LoggerFactory.getLogger(getClass()).debug("Unable to create SSLContext.");
      }
    }
    return sslContext;
  }

  private void initializeObject() throws NoSuchAlgorithmException, KeyManagementException {
    TrustManager[] trustManagers = new TrustManager[] {new X509ExtendedTrustManager() {
      @Override
      public void checkClientTrusted(X509Certificate[] chain, String authType, Socket s) {}

      @Override
      public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine e) {}

      @Override
      public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {}

      @Override
      public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {}

      @Override
      public void checkServerTrusted(X509Certificate[] chain, String authType, Socket s) {}

      @Override
      public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine e) {}

      @Override
      public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
      }
    } };
    sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, trustManagers, getSecureRandom());
    LoggerFactory.getLogger(getClass()).debug("UNSAFE: no-op TrustManager installed");
  }

  public SecureRandom getSecureRandom() {
    return secureRandom;
  }
}

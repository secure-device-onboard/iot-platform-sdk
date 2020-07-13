// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.to0scheduler.rest;

import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import org.sdo.iotplatformsdk.common.protocol.config.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create and return a custom {@link SSLContext} using a NO-OP {@link TrustManager}.
 * Used specifically for outgoing connections to Rendezvous during TransferOwnership Protocol 0.
 */
public class To0NoopSslContextFactory extends SslContextFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(To0NoopSslContextFactory.class);

  public To0NoopSslContextFactory(SecureRandom secureRandom) {
    super(secureRandom);
  }

  @Override
  public SSLContext getObject() {
    try {
      final TrustManager[] trustManagers = new TrustManager[] {new X509ExtendedTrustManager() {
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
      final SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, trustManagers, getSecureRandom());
      LOGGER.debug("UNSAFE: no-op TrustManager installed for outgoing connections to Rendezvous.");
      return sslContext;
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      LOGGER.error("Error occurred while creating custom ssl context for Rendezvous. ",
          e.getMessage());
      LOGGER.debug(e.getMessage(), e);
      return null;
    }
  }
}

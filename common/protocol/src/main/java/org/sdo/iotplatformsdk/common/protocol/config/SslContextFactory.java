// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.config;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
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
        sslContext = SSLContext.getDefault();
      } catch (NoSuchAlgorithmException e) {
        LoggerFactory.getLogger(getClass()).debug("Unable to create default SSLContext.");
      }
    }
    return sslContext;
  }

  public SecureRandom getSecureRandom() {
    return secureRandom;
  }
}

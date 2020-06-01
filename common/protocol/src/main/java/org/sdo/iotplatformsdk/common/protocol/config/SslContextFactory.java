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

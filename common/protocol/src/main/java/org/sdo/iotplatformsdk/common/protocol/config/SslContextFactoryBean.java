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

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SslContextFactoryBean implements FactoryBean<SSLContext> {

  private ObjectFactory<SecureRandom> secureRandomProvider = null;

  @Override
  public SSLContext getObject() throws KeyManagementException, NoSuchAlgorithmException {

    TrustManager[] trustManagers = new TrustManager[] {new X509TrustManager() {
      @Override
      public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {}

      @Override
      public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {}

      @Override
      public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
      }
    } };

    ObjectFactory<SecureRandom> provider = getSecureRandomProvider();
    SecureRandom random;

    if (null != provider) {
      random = provider.getObject();

    } else {
      throw new FactoryBeanNotInitializedException("SecureRandom provider must not be null");
    }

    SSLContext context = SSLContext.getInstance("TLS");
    context.init(null, trustManagers, random);
    LoggerFactory.getLogger(getClass()).warn("UNSAFE: no-op TrustManager installed");

    return context;
  }

  @Override
  public Class<?> getObjectType() {
    return SSLContext.class;
  }

  public ObjectFactory<SecureRandom> getSecureRandomProvider() {
    return secureRandomProvider;
  }

  @Autowired
  public void setSecureRandomProvider(ObjectFactory<SecureRandom> secureRandomProvider) {
    this.secureRandomProvider = secureRandomProvider;
  }
}

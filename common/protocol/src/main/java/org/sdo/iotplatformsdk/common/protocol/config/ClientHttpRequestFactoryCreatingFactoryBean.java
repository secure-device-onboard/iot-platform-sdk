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

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

public class ClientHttpRequestFactoryCreatingFactoryBean
    implements FactoryBean<ClientHttpRequestFactory> {

  private SSLContext sslContext = null;

  @Override
  public ClientHttpRequestFactory getObject() {
    SSLContext context = getSslContext();
    if (null == context) {
      throw new FactoryBeanNotInitializedException("ssl context must not be null");
    }

    return new SimpleClientHttpRequestFactory() {

      @Override
      protected void prepareConnection(HttpURLConnection connection, String httpMethod)
          throws IOException {

        super.prepareConnection(connection, httpMethod);

        if (connection instanceof HttpsURLConnection) {

          HttpsURLConnection https = (HttpsURLConnection) connection;
          https.setHostnameVerifier((hostname, session) -> true);
          LoggerFactory.getLogger(getClass()).warn("UNSAFE: no-op HostnameVerifier installed");

          https.setSSLSocketFactory(context.getSocketFactory());
        }
      }
    };
  }

  @Override
  public Class<?> getObjectType() {
    return ClientHttpRequestFactory.class;
  }

  public SSLContext getSslContext() {
    return sslContext;
  }

  @Autowired
  public void setSslContext(SSLContext sslContext) {
    this.sslContext = sslContext;
  }
}

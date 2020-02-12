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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.PrivateKey;

import org.sdo.iotplatformsdk.common.protocol.security.PemPrivateKeySupplier;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

public class PrivateKeyFactoryBean implements FactoryBean<PrivateKey> {

  private URI uri = URI.create("");

  @Override
  public PrivateKey getObject() {

    URI uri = getUri();
    URL url = null;

    if (null != uri) {

      try {
        url = uri.toURL();

      } catch (MalformedURLException e) {
        LoggerFactory.getLogger(getClass()).error(e.getMessage());
      }
    }

    PrivateKey key = new PemPrivateKeySupplier(url).get();
    if (null == key) {
      LoggerFactory.getLogger(getClass()).error("key not found at " + uri);
    }

    return key;
  }

  @Override
  public Class<?> getObjectType() {
    return PrivateKey.class;
  }

  @Override
  public boolean isSingleton() {
    return false;
  }

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }
}

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
import java.security.cert.Certificate;

import org.sdo.iotplatformsdk.common.protocol.security.PemCertificateSupplier;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

public class CertificateFactoryBean implements FactoryBean<Certificate> {

  private URI uri = URI.create("");

  @Override
  public Certificate getObject() {

    URI uri = getUri();
    URL url = null;

    if (null != uri) {

      try {
        url = uri.toURL();

      } catch (MalformedURLException e) {
        LoggerFactory.getLogger(getClass()).error(e.getMessage());
      }
    }

    Certificate cert = new PemCertificateSupplier(url).get();
    if (null == cert) {
      LoggerFactory.getLogger(getClass()).error("certificate not found at " + uri);
    }

    return cert;
  }

  @Override
  public Class<?> getObjectType() {
    return Certificate.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }
}

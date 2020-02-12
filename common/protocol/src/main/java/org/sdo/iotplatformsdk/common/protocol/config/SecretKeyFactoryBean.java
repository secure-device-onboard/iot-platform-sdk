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

import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.sdo.iotplatformsdk.common.protocol.types.MacType;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SecretKeyFactoryBean implements FactoryBean<SecretKey> {

  private ObjectFactory<SecureRandom> secureRandomProvider = null;

  @Override
  public SecretKey getObject() throws Exception {
    byte[] secret = new byte[256 / 8];
    ObjectFactory<SecureRandom> provider = getSecureRandomProvider();

    if (null != provider) {
      SecureRandom random = provider.getObject();

      if (null == random) {
        throw new FactoryBeanNotInitializedException("SecureRandom must not be null");
      }
      random.nextBytes(secret);

    } else {
      throw new FactoryBeanNotInitializedException("SecureRandom provider must not be null");
    }

    return new SecretKeySpec(secret, MacType.HMAC_SHA256.getJceName());
  }

  @Override
  public Class<?> getObjectType() {
    return SecretKey.class;
  }

  @Override
  public boolean isSingleton() {
    return false;
  }

  public ObjectFactory<SecureRandom> getSecureRandomProvider() {
    return secureRandomProvider;
  }

  @Autowired
  public void setSecureRandomProvider(ObjectFactory<SecureRandom> secureRandomProvider) {
    this.secureRandomProvider = secureRandomProvider;
  }
}

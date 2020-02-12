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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.config.SecretKeyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.ObjectFactory;

class SecretKeyFactoryBeanTest {

  ObjectFactory<SecureRandom> secureRandomProvider;
  SecretKeyFactoryBean secretKeyFactoryBean;

  @BeforeEach
  void beforeEach() {

    secureRandomProvider = new ObjectFactory<SecureRandom>() {

      @Override
      public SecureRandom getObject() throws BeansException {
        return (new SecureRandom());
      }
    };
    secretKeyFactoryBean = new SecretKeyFactoryBean();
  }

  @Test
  void test_Bean() throws Exception {

    secretKeyFactoryBean.setSecureRandomProvider(secureRandomProvider);

    secretKeyFactoryBean.getObjectType();
    secretKeyFactoryBean.getSecureRandomProvider();
    secretKeyFactoryBean.isSingleton();
    secretKeyFactoryBean.getObject();
  }

  @SuppressWarnings("unchecked")
  @Test
  void test_BadRequest() throws Exception {

    secureRandomProvider = Mockito.mock(ObjectFactory.class);

    org.junit.jupiter.api.Assertions.assertThrows(FactoryBeanNotInitializedException.class, () -> {
      secretKeyFactoryBean.getObject();
    });
  }
}

// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.config;

import java.security.SecureRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SslContextFactoryBeanTest {

  ObjectFactory<SecureRandom> secureRandomProvider;
  SslContextFactory sslContextFactoryBean;

  @BeforeEach
  void beforeEach() throws Exception {

    secureRandomProvider = Mockito.mock(ObjectFactory.class);
    sslContextFactoryBean = new SslContextFactory(secureRandomProvider.getObject());
  }

  @Test
  void test_Bean() throws Exception {
    sslContextFactoryBean.getObject();
  }
}

// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.config;

import java.io.IOException;
import java.security.SecureRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SecureRandomFactoryBeanTest {

  SecureRandomFactory secureRandomFactoryBean;

  @BeforeEach
  void beforeEach() {
    secureRandomFactoryBean = new SecureRandomFactory();
  }

  @Test
  void test_getObject() throws IOException {
    SecureRandom random = secureRandomFactoryBean.getObject();
  }
}

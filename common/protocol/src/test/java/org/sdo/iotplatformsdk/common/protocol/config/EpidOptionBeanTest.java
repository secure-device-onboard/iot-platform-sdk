// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.config;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.config.EpidOptionBean;

class EpidOptionBeanTest {

  EpidOptionBean epidOptionBean;

  @BeforeEach
  void beforeEach() {

    epidOptionBean = new EpidOptionBean();
  }

  @Test
  void test_Bean() throws IOException {

    epidOptionBean.setEpidOnlineUrl("http://www.intel.com");
    epidOptionBean.getEpidOnlineUrl();
    epidOptionBean.setTestMode(true);
    epidOptionBean.getTestMode();
  }
}

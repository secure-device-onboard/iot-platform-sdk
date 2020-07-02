// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.To2NextDeviceServiceInfo;

class To2NextDeviceServiceInfoTest {

  ServiceInfo dsi;
  To2NextDeviceServiceInfo to2NextDeviceServiceInfo;

  @BeforeEach
  void beforeEach() {

    dsi = new ServiceInfo();
    to2NextDeviceServiceInfo = new To2NextDeviceServiceInfo(0, dsi);
  }

  @Test
  void test_Bean() {

    assertEquals(Integer.valueOf(0), to2NextDeviceServiceInfo.getNn());
    assertEquals(dsi, to2NextDeviceServiceInfo.getDsi());

    to2NextDeviceServiceInfo.getType();
    to2NextDeviceServiceInfo.getVersion();
  }

}

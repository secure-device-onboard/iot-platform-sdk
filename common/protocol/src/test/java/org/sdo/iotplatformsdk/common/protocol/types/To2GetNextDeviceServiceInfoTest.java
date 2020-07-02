// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.PreServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.To2GetNextDeviceServiceInfo;

class To2GetNextDeviceServiceInfoTest {

  PreServiceInfo psi;
  To2GetNextDeviceServiceInfo to2GetNextDeviceServiceInfo;

  @BeforeEach
  void beforeEach() {

    psi = new PreServiceInfo();
    to2GetNextDeviceServiceInfo = new To2GetNextDeviceServiceInfo(0, psi);
  }

  @Test
  void test_Bean() {

    assertEquals(Integer.valueOf(0), to2GetNextDeviceServiceInfo.getNn());
    assertEquals(psi, to2GetNextDeviceServiceInfo.getPsi());

    to2GetNextDeviceServiceInfo.getType();
    to2GetNextDeviceServiceInfo.getVersion();
  }

}

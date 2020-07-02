// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.To2SetupDevice;

class To2SetupDeviceTest {

  To2SetupDevice to2SetupDevice;

  @BeforeEach
  void beforeEach() {

    to2SetupDevice = new To2SetupDevice(0, null);
  }

  @Test
  void test_Bean() {

    assertEquals(null, to2SetupDevice.getNoh());
    to2SetupDevice.getOsinn();
    to2SetupDevice.getType();
    to2SetupDevice.getVersion();
  }

}

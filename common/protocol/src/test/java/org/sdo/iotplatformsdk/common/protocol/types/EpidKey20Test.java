// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.EpidKey20;

class EpidKey20Test {

  byte[] groupId = {0x00, 0x00, 0x00, 0x0d, (byte) 0xdd, (byte) 0xdd, (byte) 0xcc, (byte) 0xcc,
      0x00, 0x00, 0x00, 0x00, (byte) 0xee, (byte) 0xee, (byte) 0xee, 0x05};
  EpidKey20 epidKey20;

  @BeforeEach
  void beforeEach() {

    epidKey20 = new EpidKey20(groupId);
  }

  @Test
  void test_Bean() {

    epidKey20.getAlgorithm();
    epidKey20.getFormat();
    epidKey20.getType();
  }

}

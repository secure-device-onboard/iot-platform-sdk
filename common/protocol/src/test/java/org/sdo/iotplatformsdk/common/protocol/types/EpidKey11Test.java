// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.EpidKey11;

class EpidKey11Test {

  byte[] groupId = {0x00, 0x00, 0x00, 0x0d, (byte) 0xdd, (byte) 0xdd, (byte) 0xcc, (byte) 0xcc,
      0x00, 0x00, 0x00, 0x00, (byte) 0xee, (byte) 0xee, (byte) 0xee, 0x05};
  EpidKey11 epidKey11;

  @BeforeEach
  void beforeEach() {

    epidKey11 = new EpidKey11(groupId);
  }

  @Test
  void test_Bean() {

    epidKey11.getAlgorithm();
    epidKey11.getFormat();
    epidKey11.getType();
  }

}

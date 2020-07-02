// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.To2GetNextOwnerServiceInfo;

class To2GetNextOwnerServiceInfoTest {

  To2GetNextOwnerServiceInfo to2GetNextOwnerServiceInfo;

  @BeforeEach
  void beforeEach() {

    to2GetNextOwnerServiceInfo = new To2GetNextOwnerServiceInfo(0);
  }

  @Test
  void test_Bean() {

    assertEquals(Integer.valueOf(0), to2GetNextOwnerServiceInfo.getNn());

    to2GetNextOwnerServiceInfo.getType();
    to2GetNextOwnerServiceInfo.getVersion();
  }

}

// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.To2OwnerServiceInfo;

class To2OwnerServiceInfoTest {

  To2OwnerServiceInfo to2OwnerServiceInfo;

  @BeforeEach
  void beforeEach() {

    to2OwnerServiceInfo = new To2OwnerServiceInfo(0, "Test");
  }

  @Test
  void test_Bean() {

    assertEquals(Integer.valueOf(0), to2OwnerServiceInfo.getNn());
    assertEquals("Test", to2OwnerServiceInfo.getSv());

    to2OwnerServiceInfo.getType();
    to2OwnerServiceInfo.getVersion();
  }

}

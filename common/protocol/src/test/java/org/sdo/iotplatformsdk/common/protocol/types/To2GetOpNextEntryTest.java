// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.To2GetOpNextEntry;

class To2GetOpNextEntryTest {

  To2GetOpNextEntry to2GetNextOpEntry;

  @BeforeEach
  void beforeEach() {

    to2GetNextOpEntry = new To2GetOpNextEntry(0);
  }

  @Test
  void test_Bean() {

    assertEquals(Integer.valueOf(0), to2GetNextOpEntry.getEnn());

    to2GetNextOpEntry.getVersion();
    to2GetNextOpEntry.getType();
  }

}

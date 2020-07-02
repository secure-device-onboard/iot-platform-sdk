// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.rest.ProtocolError;

class ProtocolErrorTest {

  ProtocolError pe;

  @BeforeEach
  void beforeEach() {
    pe = new ProtocolError();
  }

  @Test
  void test_Bean() throws IOException {
    pe.setEc(100);
    pe.setEm("Test");
    pe.setEmsg(500);

    pe.getEc();
    pe.getEm();
    pe.getEmsg();
  }

}


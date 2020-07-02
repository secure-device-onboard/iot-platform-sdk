// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.rest.To0Request;

class To0RequestTest {

  To0Request to0Request;

  @BeforeEach
  void beforeEach() {
    to0Request = new To0Request();
  }

  @Test
  void test_Bean() throws IOException {
    to0Request.setGuids(null);
    to0Request.setWaitSeconds("100");

    to0Request.getGuids();
    to0Request.getWaitSeconds();
  }

}


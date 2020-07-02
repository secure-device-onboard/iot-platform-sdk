// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.rest.SviMessage;

class SviMessageTest {

  SviMessage sviMessage;

  @BeforeEach
  void beforeEach() {
    sviMessage = new SviMessage();
  }

  @Test
  void test_Bean() throws IOException {
    sviMessage.setEnc("Test");
    sviMessage.setModule("Test");
    sviMessage.setMsg("Test");
    sviMessage.setValueId("1");
    sviMessage.setValueLen(50);

    sviMessage.getEnc();
    sviMessage.getModule();
    sviMessage.getMsg();
    sviMessage.getValueId();
    sviMessage.getValueLen();
  }

}


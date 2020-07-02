// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.rest.ModuleMessage;

class ModuleMessageTest {

  ModuleMessage moduleMessage;

  @BeforeEach
  void beforeEach() {
    moduleMessage = new ModuleMessage();
  }

  @Test
  void test_Bean() throws IOException {
    moduleMessage.setModule("sdo_sys");
    moduleMessage.setMsg("Hello SDO");
    moduleMessage.setValue("test");

    moduleMessage.getModule();
    moduleMessage.getMsg();
    moduleMessage.getValue();
  }

}


// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.rest.RendezvousInstruction;
import org.sdo.iotplatformsdk.common.rest.SetupInfoResponse;

class SetupInfoResponseTest {

  List<RendezvousInstruction> r3;
  SetupInfoResponse setupInfoResponse;

  @SuppressWarnings("unchecked")
  @BeforeEach
  void beforeEach() {
    r3 = Mockito.mock(List.class);
    setupInfoResponse = new SetupInfoResponse();
  }

  @Test
  void test_Bean() throws IOException {
    setupInfoResponse.setG3("Test");
    setupInfoResponse.setR3(r3);

    setupInfoResponse.getG3();
    setupInfoResponse.getR3();

  }

}


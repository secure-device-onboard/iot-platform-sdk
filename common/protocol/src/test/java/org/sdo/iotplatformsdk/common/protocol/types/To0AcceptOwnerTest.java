// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.To0AcceptOwner;

class To0AcceptOwnerTest {

  To0AcceptOwner to0AcceptOwner;

  @BeforeEach
  void beforeEach() {

    to0AcceptOwner = new To0AcceptOwner(Duration.ofMillis(10000));
  }

  @Test
  void test_Bean() {

    to0AcceptOwner.setWs(Duration.ofMillis(10000));

    assertEquals(Duration.ofMillis(10000), to0AcceptOwner.getWs());
  }

}

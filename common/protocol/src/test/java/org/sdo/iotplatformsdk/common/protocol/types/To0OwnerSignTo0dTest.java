// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.SecureRandom;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.To0OwnerSignTo0d;

class To0OwnerSignTo0dTest {

  Duration ws;
  OwnershipProxy op;
  Nonce n3;
  To0OwnerSignTo0d to0OwnerSignTo0d;

  @BeforeEach
  void beforeEach() {

    n3 = new Nonce(new SecureRandom());
    op = new OwnershipProxy();
    ws = Duration.ofMillis(10000);
    to0OwnerSignTo0d = new To0OwnerSignTo0d(op, ws, n3);
  }

  @Test
  void test_Bean() {

    to0OwnerSignTo0d.setN3(n3);
    to0OwnerSignTo0d.setOp(op);
    to0OwnerSignTo0d.setWs(ws);

    assertEquals(n3, to0OwnerSignTo0d.getN3());
    assertEquals(op, to0OwnerSignTo0d.getOp());
    assertEquals(ws, to0OwnerSignTo0d.getWs());
  }

}

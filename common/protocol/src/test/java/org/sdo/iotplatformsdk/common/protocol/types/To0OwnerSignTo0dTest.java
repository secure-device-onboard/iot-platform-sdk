/*******************************************************************************
 * Copyright 2020 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

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

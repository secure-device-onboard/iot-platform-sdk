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
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.RendezvousInfo;
import org.sdo.iotplatformsdk.common.protocol.types.To2SetupDeviceNoh;

class To2SetupDeviceNohTest {

  RendezvousInfo r3;
  UUID g3;
  Nonce n7;
  To2SetupDeviceNoh to2SetupDeviceNoh;

  @BeforeEach
  void beforeEach() {

    r3 = new RendezvousInfo();
    g3 = UUID.randomUUID();
    n7 = new Nonce(new SecureRandom());
    to2SetupDeviceNoh = new To2SetupDeviceNoh(r3, g3, n7);
  }

  @Test
  void test_Bean() {

    to2SetupDeviceNoh.setG3(g3);
    to2SetupDeviceNoh.setN7(n7);
    to2SetupDeviceNoh.setR3(r3);

    assertEquals(g3, to2SetupDeviceNoh.getG3());
    assertEquals(n7, to2SetupDeviceNoh.getN7());
    assertEquals(r3, to2SetupDeviceNoh.getR3());
  }

}

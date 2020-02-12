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

package org.sdo.iotplatformsdk.common.protocol.security.keyexchange;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import java.security.SecureRandom;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.DiffieHellmanKeyExchange.Group14;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.DiffieHellmanKeyExchange.Group15;

class DiffieHellmanKeyExchangeTest {

  static SecureRandom secureRandom;

  @BeforeAll
  static void beforeAll() throws Exception {
    secureRandom = SecureRandom.getInstance("SHA1PRNG");
  }

  @Test
  void dh14_generateSharedSecret_secretsMatch() throws Exception {

    KeyExchange kxa = new Group14(secureRandom);
    KeyExchange kxb = new Group14(secureRandom);
    ByteBuffer xa = kxa.getMessage();
    ByteBuffer xb = kxb.getMessage();
    ByteBuffer shSeA = kxa.generateSharedSecret(xb);
    ByteBuffer shSeB = kxb.generateSharedSecret(xa);
    assertEquals(shSeA, shSeB);
  }

  @Test
  void dh15_generateSharedSecret_secretsMatch() throws Exception {

    KeyExchange kxa = new Group15(secureRandom);
    KeyExchange kxb = new Group15(secureRandom);
    ByteBuffer xa = kxa.getMessage();
    ByteBuffer xb = kxb.getMessage();
    ByteBuffer shSeA = kxa.generateSharedSecret(xb);
    ByteBuffer shSeB = kxb.generateSharedSecret(xa);
    assertEquals(shSeA, shSeB);
  }
}

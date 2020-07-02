// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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

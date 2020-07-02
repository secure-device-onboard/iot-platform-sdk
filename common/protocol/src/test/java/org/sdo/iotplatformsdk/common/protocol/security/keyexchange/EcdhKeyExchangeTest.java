// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security.keyexchange;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.EcdhKeyExchange;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.KeyExchange;

class EcdhKeyExchangeTest {

  static SecureRandom secureRandom;
  static KeyPairGenerator gen;

  @BeforeAll
  static void beforeAll() throws Exception {
    secureRandom = SecureRandom.getInstance("SHA1PRNG");
  }

  @Test
  void p256_generateSharedSecret_secretsMatch() throws Exception {

    KeyExchange kxa = new EcdhKeyExchange.P256(secureRandom);
    KeyExchange kxb = new EcdhKeyExchange.P256(secureRandom);

    ByteBuffer xa = kxa.getMessage();
    ByteBuffer xb = kxb.getMessage();

    final ByteBuffer shSeA = kxa.generateSharedSecret(xb);
    ByteBuffer shSeB = kxb.generateSharedSecret(xa);
    // The secret shSeB is assembled in owner's order. Since, we don't have the code for device's
    // way of assembling the shared secret, reassemble the buffer in the device's order.
    byte[] bsecret = new byte[32];
    byte[] bmyrandom = new byte[16];
    byte[] btheirrandom = new byte[16];
    shSeB.get(bsecret, 0, 32);
    shSeB.get(bmyrandom, 0, 16);
    shSeB.get(btheirrandom, 0, 16);
    ByteBuffer finalShSeB = ByteBuffer.allocate(64);
    finalShSeB.put(bsecret);
    finalShSeB.put(btheirrandom);
    finalShSeB.put(bmyrandom);
    finalShSeB.flip();

    assertEquals(shSeA, finalShSeB);
  }

  @Test
  void p384_generateSharedSecret_secretsMatch() throws Exception {

    KeyExchange kxa = new EcdhKeyExchange.P384(secureRandom);
    KeyExchange kxb = new EcdhKeyExchange.P384(secureRandom);

    ByteBuffer xa = kxa.getMessage();
    ByteBuffer xb = kxb.getMessage();

    final ByteBuffer shSeA = kxa.generateSharedSecret(xb);
    ByteBuffer shSeB = kxb.generateSharedSecret(xa);

    // The secret shSeB is assembled in owner's order. Since, we don't have the code for device's
    // way of assembling the shared secret, reassemble the buffer in the device's order.
    byte[] bsecret = new byte[48];
    byte[] bmyrandom = new byte[48];
    byte[] btheirrandom = new byte[48];
    shSeB.get(bsecret, 0, 48);
    shSeB.get(bmyrandom, 0, 48);
    shSeB.get(btheirrandom, 0, 48);
    ByteBuffer finalShSeB = ByteBuffer.allocate(144);
    finalShSeB.put(bsecret);
    finalShSeB.put(btheirrandom);
    finalShSeB.put(bmyrandom);
    finalShSeB.flip();

    assertEquals(shSeA, finalShSeB);
  }
}

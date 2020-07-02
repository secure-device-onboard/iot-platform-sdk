// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.CharBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyEntryCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.PublicKeyCodec;
import org.sdo.iotplatformsdk.common.protocol.types.HashDigest;
import org.sdo.iotplatformsdk.common.protocol.types.KeyEncoding;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxyEntry;

class OwnershipProxyEntryCodecTest {

  HashDigest hc;
  HashDigest hp;
  KeyPairGenerator keyGen;
  KeyPair keypair;
  PublicKey pk;
  OwnershipProxyEntry ownershipProxyEntry;
  OwnershipProxyEntryCodec ownershipProxyEntryCodec;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() throws NoSuchAlgorithmException {

    hc = new HashDigest();
    hp = new HashDigest();
    keyGen = KeyPairGenerator.getInstance("RSA");
    keyGen.initialize(1024);
    keypair = keyGen.genKeyPair();
    pk = keypair.getPublic();
    ownershipProxyEntry = new OwnershipProxyEntry(hp, hc, pk);
  }

  @Test
  void test_Encoder() throws IOException {

    ownershipProxyEntry.setHc(hc);
    ownershipProxyEntry.setHp(hp);
    ownershipProxyEntry.setPk(pk);

    new OwnershipProxyEntryCodec.Encoder(new PublicKeyCodec.Encoder(KeyEncoding.RSAMODEXP))
        .encode(writer, ownershipProxyEntry);
  }

  @Test
  void test_Decoder() throws IOException {

    new OwnershipProxyEntryCodec.Decoder().decode(CharBuffer.wrap(writer.toString()));
  }
}

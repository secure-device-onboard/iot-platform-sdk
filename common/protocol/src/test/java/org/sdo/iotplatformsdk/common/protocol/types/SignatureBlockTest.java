// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;

class SignatureBlockTest {

  ByteBuffer sg;
  CharSequence bo;
  KeyPairGenerator keyGen;
  KeyPair keypair;
  PublicKey publicKey;
  SignatureBlock signatureBlock;
  SignatureBlock signatureBlock1;

  @BeforeEach
  void beforeEach() throws NoSuchAlgorithmException {

    bo = "Test";
    keyGen = KeyPairGenerator.getInstance("RSA");
    keyGen.initialize(1024);
    keypair = keyGen.genKeyPair();
    publicKey = keypair.getPublic();
    sg = ByteBuffer.wrap("Test".getBytes());
    signatureBlock = new SignatureBlock("Test", publicKey, sg);
    signatureBlock1 = Mockito.mock(SignatureBlock.class);
  }

  @Test
  void test_Bean() {

    signatureBlock.setBo(bo);
    signatureBlock.setPk(publicKey);
    signatureBlock.setSg(sg);

    assertEquals(CharBuffer.wrap(bo), signatureBlock.getBo());
    assertEquals(publicKey, signatureBlock.getPk());
    assertEquals(sg, signatureBlock.getSg());
    signatureBlock.hashCode();
    signatureBlock.equals(null);
    signatureBlock.equals(signatureBlock);
    signatureBlock.equals(signatureBlock1);
  }

}

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

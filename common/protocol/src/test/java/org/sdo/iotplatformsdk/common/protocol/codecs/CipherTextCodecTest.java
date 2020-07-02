// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.SecureRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.codecs.CipherTextCodec;
import org.sdo.iotplatformsdk.common.protocol.types.CipherText;

class CipherTextCodecTest {

  CipherText cipherText;
  CipherTextCodec cipherTextCodec;
  String encodedCt =
      "[[16,\"Dv6cfEh+AzezA9cJi8nlRg==\"],48,\"2SkYVxL9kGy0HUWnWrj6s28VydNbGdIev6iIMgW4m8R06"
          + "qsY4EBLi21TAz2Q/Xcs\"]";
  SecureRandom secureRandom;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {}

  @BeforeEach
  void beforeEach() {
    cipherText = Mockito.mock(CipherText.class);
    cipherTextCodec = new CipherTextCodec();
    writer = new StringWriter();
    secureRandom = new SecureRandom();
  }

  @Test
  void test_Encoder() throws IOException {

    byte[] iv = new byte[16];
    byte[] ct = new byte[35];
    secureRandom.nextBytes(iv);
    secureRandom.nextBytes(ct);
    Mockito.when(cipherText.getCt()).thenReturn(ByteBuffer.wrap(ct));
    Mockito.when(cipherText.getIv()).thenReturn(ByteBuffer.wrap(iv));

    cipherTextCodec.encoder().apply(writer, cipherText);
  }

  @Test
  void test_Decoder() throws IOException {
    writer.append(encodedCt);
    cipherTextCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}

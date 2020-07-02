// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.SecureRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.codecs.EncryptedMessageCodec;
import org.sdo.iotplatformsdk.common.protocol.types.CipherText;
import org.sdo.iotplatformsdk.common.protocol.types.EncryptedMessage;
import org.sdo.iotplatformsdk.common.protocol.types.To2CipherHashMac;

class EncryptedMessageCodecTest {

  CipherText ct;
  EncryptedMessageCodec encryptedMessageCodec;
  EncryptedMessage encryptedMessage;
  To2CipherHashMac mac;
  SecureRandom secureRandom;
  StringWriter writer;

  @BeforeEach
  void beforeEach() {
    writer = new StringWriter();
    secureRandom = new SecureRandom();
    ct = Mockito.mock(CipherText.class);
    encryptedMessageCodec = new EncryptedMessageCodec();
    encryptedMessage = Mockito.mock(EncryptedMessage.class);
    mac = new To2CipherHashMac(ByteBuffer.allocate(8));
  }

  @Test
  void test_Encoder() throws IOException {
    byte[] iv = new byte[16];
    byte[] cipherTextBuf = new byte[35];
    secureRandom.nextBytes(iv);
    Mockito.when(ct.getCt()).thenReturn(ByteBuffer.wrap(cipherTextBuf));
    Mockito.when(ct.getIv()).thenReturn(ByteBuffer.wrap(iv));
    Mockito.when(encryptedMessage.getCt()).thenReturn(ct);
    Mockito.when(encryptedMessage.getHmac()).thenReturn(mac);

    encryptedMessageCodec.encoder().apply(writer, encryptedMessage);
  }

  @Test
  void test_Decoder() throws IOException {

    String encodedEncryptedMessage =
        "{\"ct\":[[16,\"Dv6cfEh+AzezA9cJi8nlRg==\"],48,\"2SkYVxL9kGy0HUWnWrj6s28VydNbGdIev6iIMgW4m"
            + "8R06qsY4EBLi21TAz2Q/Xcs\"],\"hmac\":[32,\"1FQBn3ARIZljuV2KV+b45ZgBLQF6vDJNpCQe3zz9"
            + "vdM=\"]}";
    encryptedMessageCodec.decoder().apply(CharBuffer.wrap(encodedEncryptedMessage));
  }
}

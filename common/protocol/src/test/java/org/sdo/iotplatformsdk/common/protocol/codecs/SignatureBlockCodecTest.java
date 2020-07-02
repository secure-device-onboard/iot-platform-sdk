// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.PublicKeyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.SignatureBlockCodec;
import org.sdo.iotplatformsdk.common.protocol.types.KeyEncoding;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;

class SignatureBlockCodecTest {

  static StringWriter writer;
  SignatureBlock signatureBlock;
  PublicKey pk;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() throws NoSuchAlgorithmException {

    signatureBlock = new SignatureBlock("pk", null, ByteBuffer.allocate(8));
  }

  @Test
  void test_Encoder() throws IOException {
    new SignatureBlockCodec.Encoder(new PublicKeyCodec.Encoder(KeyEncoding.X_509)).encode(writer,
        signatureBlock);
  }

  @Test
  void test_Decoder() throws IOException {
    String input = "{\"bo\":{\"r3\":[2,[4,{\"dn\":\"localhost\",\"only\":\"owner\",\"pow\":8040,"
        + "\"pr\":\"http\"}],[4,{\"dn\":\"localhost\",\"only\":\"dev\",\"po\":8040,\"pr\""
        + ":\"http\"}]],\"g3\":\"iavdTkTMT7uHZdjSoVhN8w==\",\"n7\":\"BRswKDfuye3ymYCQeiL"
        + "hjA==\"},\"pk\":[1,3,[257,\"ALROfFsfUuAVkzXa05o9/pCVzSrjgJN7IEMLxdGPLl6N/xAnk"
        + "v4Y+Ug5sRuX0pwZZhlSicBldW01REQ2eLoeGltX1jJqFHeMXMbp+DeAfflg0kJV//kXd8ASxJCkHz"
        + "a+5VPWYrX2hpASsbNt2NBgmB38IfdU418zbRswM7+Bf0qkOOSq0mn0CCBI+9srmnQa0Ljflx5GoKS"
        + "cBbS2K9K27UWX0i3Er6BmbacFvSXyA57QvnqqKnMDqzEVmQJ8gGOdhhWaivfnIyZV7qAEuLh/quih"
        + "4CwubtK3KS4g2CYIr+erOKcHeCC0rkRkqIQmLCCCJPaw/rVVqhbBmRg3GV1NMwk=\",3,\"AQAB\"]]"
        + ",\"sg\":[256,\"TK5KSrmuV++xRf6h6Q6DecHHRSYtdMI6Xui/U8K3khMMBoqtYndSJ7KXZhcfAn"
        + "IQ4RK4piC5824/antcBX9UiVQtOD1S6vqGzmOt9ijcEuwXWbAfE2UbU9IoFzMf65v8/Z8S00uoyIKj"
        + "5vAhegG6pUZx5TcGSBsuP+klAK6L32uljfkkglcA5+cxMTX6Y3nq3ZWAowfIR3qwQli4d5dYma8wIx"
        + "DFPRkuVI0jJU2vk0IhKmyJcl+a8gCYgg1GJd0CGycKUmPeF8ml+hxrKbzF8eEgcOuRSuIsc4nhR+nJ"
        + "LcXKWUTRSUVpd+yssYuu4lF8Mdx8FbwooU0ZX/rib8JZmw==\"]}";
    new SignatureBlockCodec.Decoder(null).decode(CharBuffer.wrap(input.toCharArray()));
  }
}

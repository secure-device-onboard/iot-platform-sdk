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

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.PublicKeyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.SignatureBlockCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2SetupDeviceCodec;
import org.sdo.iotplatformsdk.common.protocol.types.KeyEncoding;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;
import org.sdo.iotplatformsdk.common.protocol.types.To2SetupDevice;

class To2SetupDeviceCodecTest {

  To2SetupDevice to2SetupDevice;
  SignatureBlock signatureBlock;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() throws NoSuchAlgorithmException {

    signatureBlock = new SignatureBlock(CharBuffer.allocate(8), null, ByteBuffer.allocate(8));
    to2SetupDevice = new To2SetupDevice(1, signatureBlock);
  }

  @Test
  void test_Encoder() throws IOException {
    new To2SetupDeviceCodec.Encoder(
        new SignatureBlockCodec.Encoder(new PublicKeyCodec.Encoder(KeyEncoding.X_509)))
            .encode(writer, to2SetupDevice);
  }

  @Test
  void test_Decoder() throws IOException {
    String input = "{\"osinn\":4,\"noh\":{\"bo\":{\"r3\":[2,[4,{\"dn\":\"localhost\","
        + "\"only\":\"owner\",\"pow\":8040,\"pr\":\"http\"}],[4,{\"dn\":"
        + "\"localhost\",\"only\":\"dev\",\"po\":8040,\"pr\":\"http\"}]],"
        + "\"g3\":\"iavdTkTMT7uHZdjSoVhN8w==\",\"n7\":\"BRswKDfuye3ymYCQei"
        + "LhjA==\"},\"pk\":[1,3,[257,\"ALROfFsfUuAVkzXa05o9/pCVzSrjgJN7IEM"
        + "LxdGPLl6N/xAnkv4Y+Ug5sRuX0pwZZhlSicBldW01REQ2eLoeGltX1jJqFHeMXM"
        + "bp+DeAfflg0kJV//kXd8ASxJCkHza+5VPWYrX2hpASsbNt2NBgmB38IfdU418zb"
        + "RswM7+Bf0qkOOSq0mn0CCBI+9srmnQa0Ljflx5GoKScBbS2K9K27UWX0i3Er6Bm"
        + "bacFvSXyA57QvnqqKnMDqzEVmQJ8gGOdhhWaivfnIyZV7qAEuLh/quih4CwubtK"
        + "3KS4g2CYIr+erOKcHeCC0rkRkqIQmLCCCJPaw/rVVqhbBmRg3GV1NMwk=\",3,"
        + "\"AQAB\"]],\"sg\":[256,\"TK5KSrmuV++xRf6h6Q6DecHHRSYtdMI6Xui/U8"
        + "K3khMMBoqtYndSJ7KXZhcfAnIQ4RK4piC5824/antcBX9UiVQtOD1S6vqGzmOt9"
        + "ijcEuwXWbAfE2UbU9IoFzMf65v8/Z8S00uoyIKj5vAhegG6pUZx5TcGSBsuP+kl"
        + "AK6L32uljfkkglcA5+cxMTX6Y3nq3ZWAowfIR3qwQli4d5dYma8wIxDFPRkuVI0"
        + "jJU2vk0IhKmyJcl+a8gCYgg1GJd0CGycKUmPeF8ml+hxrKbzF8eEgcOuRSuIsc4"
        + "nhR+nJLcXKWUTRSUVpd+yssYuu4lF8Mdx8FbwooU0ZX/rib8JZmw==\"]}}";
    new To2SetupDeviceCodec.Decoder(new SignatureBlockCodec.Decoder(null))
        .decode(CharBuffer.wrap(input.toCharArray()));
  }
}

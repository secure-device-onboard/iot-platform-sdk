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
import java.security.PublicKey;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.PublicKeyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.SignatureBlockCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2OpNextEntryCodec;
import org.sdo.iotplatformsdk.common.protocol.types.KeyEncoding;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;
import org.sdo.iotplatformsdk.common.protocol.types.To2OpNextEntry;

class To2OpNextEntryCodecTest {

  To2OpNextEntry to2OpNextEntry;
  SignatureBlock signatureBlock;
  PublicKey publicKey;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() throws NoSuchAlgorithmException {

    signatureBlock = new SignatureBlock(CharBuffer.allocate(8), null, ByteBuffer.allocate(8));
    to2OpNextEntry = new To2OpNextEntry(2, signatureBlock);
  }

  @Test
  void test_Encoder() throws IOException {
    new To2OpNextEntryCodec.Encoder(
        new SignatureBlockCodec.Encoder(new PublicKeyCodec.Encoder(KeyEncoding.X_509)))
            .encode(writer, to2OpNextEntry);
  }

  @Test
  void test_Decoder() throws IOException {
    String input = "{\"enn\":0,\"eni\":{\"bo\":{\"hp\":[32,8,\"RfZ5kVQoEmNMUc1l2aCO0"
        + "GI8YBRL+9m1+u1p/6pNvqg=\"],\"hc\":[32,8,\"k77cB+VPf5oFTqmfKePG"
        + "OwzsAAh2j4r75q9t3EKGnLE=\"],\"pk\":[1,3,[257,\"ALROfFsfUuAVkzX"
        + "a05o9/pCVzSrjgJN7IEMLxdGPLl6N/xAnkv4Y+Ug5sRuX0pwZZhlSicBldW01R"
        + "EQ2eLoeGltX1jJqFHeMXMbp+DeAfflg0kJV//kXd8ASxJCkHza+5VPWYrX2hpA"
        + "SsbNt2NBgmB38IfdU418zbRswM7+Bf0qkOOSq0mn0CCBI+9srmnQa0Ljflx5Go"
        + "KScBbS2K9K27UWX0i3Er6BmbacFvSXyA57QvnqqKnMDqzEVmQJ8gGOdhhWaivf"
        + "nIyZV7qAEuLh/quih4CwubtK3KS4g2CYIr+erOKcHeCC0rkRkqIQmLCCCJPaw/"
        + "rVVqhbBmRg3GV1NMwk=\",3,\"AQAB\"]]},\"pk\":[0,0,[0]],\"sg\":[256,"
        + "\"Da81MXRiSV6TfHqzrnZZ01SlEb2PmtygvAnq5sixwfsWAugQeIbd4qreE1nYR"
        + "QbrHdf52jqsqS0PAzFMrsgi3gHht1SBCORUFTbyUti/iAx8EGg+msW/n9+M5qjT"
        + "T8vbh4VijIPBXN7EFnzWPJepsddcjZa82jLJAsRlfR5eCayIfKJv1Gx8UCVv9kI"
        + "A1DaM9aBpSmqjoUb99s7gEkeLsEwkpUSDT0hcnsJtZRf2kqtlr/suIrdweQa7b/"
        + "qDvS8+mnESeuWNHZXHhPWVwttCoAqB9tQqVakfRHSuihaUc+gyYmVTrp+OJ4TVf"
        + "/U7IU+7sL/k3n48zX+3dKHNEDZCpQ==\"]}}";
    new To2OpNextEntryCodec.Decoder().decode(CharBuffer.wrap(input.toCharArray()));
  }
}

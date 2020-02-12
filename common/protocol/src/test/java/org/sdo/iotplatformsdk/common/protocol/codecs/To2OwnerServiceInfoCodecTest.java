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
import java.nio.CharBuffer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2OwnerServiceInfoCodec;
import org.sdo.iotplatformsdk.common.protocol.types.To2OwnerServiceInfo;

class To2OwnerServiceInfoCodecTest {

  To2OwnerServiceInfoCodec to2OwnerServiceInfoCodec;
  To2OwnerServiceInfo to2OwnerServiceInfo;
  static StringWriter writer;
  String serviceInfo = "{\"sdo_sys:filedesc\":\"dGVzdC16aXAuemlw\",\"sdo_sys:write\":"
      + "\"UEsDBBQAAAAAAClcxk7WBX5zFAAAABQAAAANAAAAYmluc2gtbGludXg2N"
      + "C9iaW4vc2gAbGludXg2NC5zaAAAUEsDBBQAAAAAAGBt8U4AAAAAAAAAAAAA"
      + "AAAPAAAAZWNobyBoZWxsb3dvcmxkUEsDBBQAAAAAAClcxk6nJqu3CwAAAAs"
      + "AAAAMAAAAcGF5bG9hZF9uYW1lcGF5bG9hZC5iaW5QSwMEFAAAAAAAKVzGTj"
      + "lC/2YPAAAADwAAAAoAAABzaC1saW51eDY0c2gAbGludXg2NC5zaAAAUEsBA"
      + "hQAFAAAAAAAKVzGTtYFfnMUAAAAFAAAAA0AAAAAAAAAAQAgAAAAAAAAAGJp"
      + "bnNoLWxpbnV4NjRQSwECFAAUAAAAAABgbfFOAAAAAAAAAAAAAAAADwAAAAA"
      + "AAAAAACAAAAA/AAAAZWNobyBoZWxsb3dvcmxkUEsBAhQAFAAAAAAAKVzGTq"
      + "cmq7cLAAAACwAAAAwAAAAAAAAAAQAgAAAAbAAAAHBheWxvYWRfbmFtZVBLA"
      + "QIUABQAAAAAAClcxk45Qv9mDwAAAA8AAAAKAAAAAAAAAAEAIAAAAKEAAABz"
      + "aC1saW51eDY0UEsFBgAAAAAEAAQA6gAAANgAAAAAAA==\",\"sdo_sys:fi"
      + "ledesc\":\"ZWNobyBoZWxsb3dvcmxk\",\"sdo_sys:write\":\"H4sIA"
      + "OasrVsAA+3Sy2rDMBCFYa3zFNN075tia5dVX0RWpnWoiYMll+Tt60BCCyV0"
      + "k1BK/m8zghHowNHeh3f/prm5o2LmXH2apauL7/PClNbW1laNtaUpymo+GKn"
      + "vGepiismPIia12unor977bf9P7c/9D7t28OMmi93t3zgV3DSr6/0799V/Ze"
      + "f+V9W8luL2UX568P6fn/J2u8tbH7uFhm6Q5Yt+bIPK+UPoRuIUgsb4OvX9\"}";

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() {
    to2OwnerServiceInfo = new To2OwnerServiceInfo(1, serviceInfo);
    to2OwnerServiceInfoCodec = new To2OwnerServiceInfoCodec();
  }

  @Test
  void test_Encoder() throws IOException {
    to2OwnerServiceInfoCodec.encoder().apply(writer, to2OwnerServiceInfo);
  }

  @Test
  void test_Decoder() throws IOException {
    To2OwnerServiceInfo receivedTo2OwnerServiceInfo =
        to2OwnerServiceInfoCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
    Assertions.assertEquals(to2OwnerServiceInfo.getNn(), receivedTo2OwnerServiceInfo.getNn());
    Assertions.assertEquals(to2OwnerServiceInfo.getSv(), receivedTo2OwnerServiceInfo.getSv());
  }
}

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

import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.COMMA;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;
import org.sdo.iotplatformsdk.common.protocol.types.To2SetupDevice;

public class To2SetupDeviceCodec {

  private static final String NOH = "noh";
  private static final String OSINN = "osinn";

  public static class Decoder implements SdoDecoder<To2SetupDevice> {

    private final SignatureBlockCodec.Decoder sgDecoder;

    public Decoder(SignatureBlockCodec.Decoder sgDecoder) {
      this.sgDecoder = sgDecoder;
    }

    @Override
    public To2SetupDevice decode(CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);

      expect(in, Json.asKey(OSINN));
      final Number osinn = new Uint32Codec().decoder().apply(in);

      expect(in, COMMA);
      expect(in, Json.asKey(NOH));
      final SignatureBlock noh = sgDecoder.decode(in);

      expect(in, END_OBJECT);

      return new To2SetupDevice(osinn.intValue(), noh);
    }
  }

  public static class Encoder implements SdoEncoder<To2SetupDevice> {

    private final SignatureBlockCodec.Encoder nohEncoder;

    public Encoder(SignatureBlockCodec.Encoder nohEncoder) {
      this.nohEncoder = nohEncoder;
    }

    @Override
    public void encode(Writer writer, To2SetupDevice value) throws IOException {

      writer.write(BEGIN_OBJECT);

      writer.write(Json.asKey(OSINN));
      new Uint32Codec().encoder().apply(writer, value.getOsinn());

      writer.write(COMMA);
      writer.write(Json.asKey(NOH));
      nohEncoder.encode(writer, value.getNoh());

      writer.write(END_OBJECT);
    }
  }

}

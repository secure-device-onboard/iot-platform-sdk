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
import org.sdo.iotplatformsdk.common.protocol.types.To2OpNextEntry;

public class To2OpNextEntryCodec {

  private static final String ENI = "eni";
  private static final String ENN = "enn";

  public static class Decoder implements SdoDecoder<To2OpNextEntry> {

    @Override
    public To2OpNextEntry decode(CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);

      expect(in, Json.asKey(ENN));
      final Number enn = new Uint32Codec().decoder().apply(in);

      expect(in, COMMA);
      expect(in, Json.asKey(ENI));
      final SignatureBlock eni = new SignatureBlockCodec.Decoder(null).decode(in);

      expect(in, END_OBJECT);

      return new To2OpNextEntry(enn.intValue(), eni);
    }
  }

  public static class Encoder implements SdoEncoder<To2OpNextEntry> {

    private final SignatureBlockCodec.Encoder eniEncoder;

    public Encoder(SignatureBlockCodec.Encoder eniEncoder) {
      this.eniEncoder = eniEncoder;
    }

    @Override
    public void encode(Writer writer, To2OpNextEntry value) throws IOException {

      writer.write(BEGIN_OBJECT);

      writer.write(Json.asKey(ENN));
      new Uint32Codec().encoder().apply(writer, value.getEnn());

      writer.write(COMMA);
      writer.write(Json.asKey(ENI));
      eniEncoder.encode(writer, value.getEni());

      writer.write(END_OBJECT);
    }
  }
}

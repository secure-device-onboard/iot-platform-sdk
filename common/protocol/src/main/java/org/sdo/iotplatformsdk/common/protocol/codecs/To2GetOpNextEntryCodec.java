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
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

import org.sdo.iotplatformsdk.common.protocol.types.To2GetOpNextEntry;

public class To2GetOpNextEntryCodec extends Codec<To2GetOpNextEntry> {

  private static final String ENN = "enn";

  private final Codec<Number> ennCodec = new Uint32Codec();

  @Override
  public Codec<To2GetOpNextEntry>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<To2GetOpNextEntry>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<Number> getEnnCodec() {
    return ennCodec;
  }

  private class Decoder extends Codec<To2GetOpNextEntry>.Decoder {

    @Override
    public To2GetOpNextEntry apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);
      expect(in, Json.asKey(ENN));
      Number enn = getEnnCodec().decoder().apply(in);
      expect(in, END_OBJECT);

      return new To2GetOpNextEntry(enn.intValue());
    }
  }

  private class Encoder extends Codec<To2GetOpNextEntry>.Encoder {

    @Override
    public void apply(Writer writer, To2GetOpNextEntry value) throws IOException {

      writer.write(BEGIN_OBJECT);
      writer.write(Json.asKey(ENN));
      getEnnCodec().encoder().apply(writer, value.getEnn());
      writer.write(END_OBJECT);
    }
  }
}

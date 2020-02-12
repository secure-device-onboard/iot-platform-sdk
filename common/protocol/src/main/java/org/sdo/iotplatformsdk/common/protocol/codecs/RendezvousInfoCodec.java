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

import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_ARRAY;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.COMMA;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_ARRAY;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

import org.sdo.iotplatformsdk.common.protocol.types.RendezvousInfo;
import org.sdo.iotplatformsdk.common.protocol.types.RendezvousInstr;

public class RendezvousInfoCodec extends Codec<RendezvousInfo> {

  private final Codec<Number> lengthCodec = new Uint8Codec();

  @Override
  public Codec<RendezvousInfo>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<RendezvousInfo>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<Number> getLengthCodec() {
    return lengthCodec;
  }

  private class Decoder extends Codec<RendezvousInfo>.Decoder {

    @Override
    public RendezvousInfo apply(CharBuffer in) throws IOException {

      RendezvousInfo value = new RendezvousInfo();

      expect(in, BEGIN_ARRAY);

      Long length = getLengthCodec().decoder().apply(in).longValue();

      for (Long l = 0L; l < length; ++l) {
        expect(in, COMMA);
        value.add(new RendezvousInstr(in));
      }

      expect(in, END_ARRAY);

      return value;
    }
  }

  private class Encoder extends Codec<RendezvousInfo>.Encoder {

    @Override
    public void apply(Writer writer, RendezvousInfo value) throws IOException {

      writer.write(BEGIN_ARRAY);

      getLengthCodec().encoder().apply(writer, value.size());

      for (RendezvousInstr rvi : value) {
        writer.write(COMMA);
        writer.write(rvi.toString());
      }

      writer.write(END_ARRAY);
    }
  }
}

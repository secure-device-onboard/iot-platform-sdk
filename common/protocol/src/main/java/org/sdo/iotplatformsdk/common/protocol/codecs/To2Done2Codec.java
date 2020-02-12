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

import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.To2Done2;

public class To2Done2Codec extends Codec<To2Done2> {

  private static final String N7 = "n7";

  @Override
  public Codec<To2Done2>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<To2Done2>.Encoder encoder() {
    return new Encoder();
  }

  private class Decoder extends Codec<To2Done2>.Decoder {

    @Override
    public To2Done2 apply(CharBuffer in) throws IOException {
      expect(in, BEGIN_OBJECT);

      expect(in, Json.asKey(N7));
      final Nonce n7 = new Nonce(in);

      expect(in, END_OBJECT);

      return new To2Done2(n7);
    }
  }

  private class Encoder extends Codec<To2Done2>.Encoder {

    @Override
    public void apply(Writer writer, To2Done2 value) throws IOException {
      writer.write(BEGIN_OBJECT);

      writer.write(Json.asKey(N7));
      writer.write(value.getN7().toString());

      writer.write(END_OBJECT);
    }
  }
}

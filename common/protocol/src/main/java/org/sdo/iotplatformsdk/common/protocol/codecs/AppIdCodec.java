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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * Codec for AppID.
 *
 * @see "SDO Protocol Specification, 1.13b, 3.2: Composite Types"
 */
public class AppIdCodec extends Codec<ByteBuffer> {

  private final Codec<ByteBuffer> appIdBytesCodec = new ByteArrayCodec();
  private final Codec<Number> lengthCodec = new Uint8Codec();
  private final Codec<Number> typeCodec = new Uint8Codec();

  @Override
  public Codec<ByteBuffer>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<ByteBuffer>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<ByteBuffer> getAppIdBytesCodec() {
    return appIdBytesCodec;
  }

  private Codec<Number> getLengthCodec() {
    return lengthCodec;
  }

  private Codec<Number> getTypeCodec() {
    return typeCodec;
  }

  private class Decoder extends Codec<ByteBuffer>.Decoder {

    @Override
    public ByteBuffer apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_ARRAY);
      final int length = getLengthCodec().decoder().apply(in).intValue();

      expect(in, COMMA);
      getTypeCodec().decoder().apply(in); // unused, see spec 3.2.5

      expect(in, COMMA);
      final ByteBuffer appIdBytes = getAppIdBytesCodec().decoder().apply(in);

      expect(in, END_ARRAY);

      if (appIdBytes.remaining() != length) {
        throw new IOException("length mismatch");
      }

      return appIdBytes;
    }
  }

  private class Encoder extends Codec<ByteBuffer>.Encoder {

    @Override
    public void apply(Writer writer, ByteBuffer value) throws IOException {

      writer.write(BEGIN_ARRAY);
      getLengthCodec().encoder().apply(writer, value.remaining());

      writer.write(COMMA);
      getTypeCodec().encoder().apply(writer, 0); // unused, see spec 3.2.5

      writer.write(COMMA);
      getAppIdBytesCodec().encoder().apply(writer, value);

      writer.write(END_ARRAY);
    }
  }
}

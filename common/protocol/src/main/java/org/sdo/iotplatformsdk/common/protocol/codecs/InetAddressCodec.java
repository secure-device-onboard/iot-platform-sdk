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
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import org.sdo.iotplatformsdk.common.protocol.util.Buffers;

/**
 * Codec for SDO "IP Address" type.
 *
 * @see "SDO Protocol Specification, 1.13b, 3.2: Composite Types"
 */
public class InetAddressCodec extends Codec<InetAddress> {

  private final Codec<ByteBuffer> dataCodec = new ByteArrayCodec();
  private final Codec<Number> lengthCodec = new Uint8Codec();

  @Override
  public Codec<InetAddress>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<InetAddress>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<ByteBuffer> getDataCodec() {
    return dataCodec;
  }

  private Codec<Number> getLengthCodec() {
    return lengthCodec;
  }

  private class Decoder extends Codec<InetAddress>.Decoder {

    @Override
    public InetAddress apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_ARRAY);

      int len = getLengthCodec().decoder().apply(in).intValue();

      expect(in, COMMA);
      ByteBuffer value = getDataCodec().decoder().apply(in);

      expect(in, END_ARRAY);

      if (len != value.remaining()) {
        throw new IOException("length mismatch");
      }

      return InetAddress.getByAddress(Buffers.unwrap(value));
    }
  }

  private class Encoder extends Codec<InetAddress>.Encoder {

    @Override
    public void apply(Writer writer, InetAddress value) throws IOException {

      ByteBuffer bytes = ByteBuffer.wrap(value.getAddress());

      writer.write(BEGIN_ARRAY);
      getLengthCodec().encoder().apply(writer, bytes.remaining());

      writer.append(COMMA);
      getDataCodec().encoder().apply(writer, bytes);

      writer.write(END_ARRAY);
    }
  }
}

// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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
 * Codec for SDO "IVData" type.
 *
 * @see "SDO Protocol Specification, 1.13b, 3.2: Composite Types"
 */
public class IvDataCodec extends Codec<ByteBuffer> {

  private final Codec<ByteBuffer> dataCodec = new ByteArrayCodec();
  private final Codec<Number> lengthCodec = new Uint8Codec();

  @Override
  public Codec<ByteBuffer>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<ByteBuffer>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<ByteBuffer> getDataCodec() {
    return dataCodec;
  }

  private Codec<Number> getLengthCodec() {
    return lengthCodec;
  }

  private class Decoder extends Codec<ByteBuffer>.Decoder {

    @Override
    public ByteBuffer apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_ARRAY);

      int len = getLengthCodec().decoder().apply(in).intValue();

      expect(in, COMMA);
      ByteBuffer value = getDataCodec().decoder().apply(in);

      expect(in, END_ARRAY);

      if (len != value.remaining()) {
        throw new IOException("length mismatch");
      }

      return value;
    }
  }

  private class Encoder extends Codec<ByteBuffer>.Encoder {

    @Override
    public void apply(Writer writer, ByteBuffer value) throws IOException {

      writer.write(BEGIN_ARRAY);
      getLengthCodec().encoder().apply(writer, value.remaining());

      writer.append(COMMA);
      getDataCodec().encoder().apply(writer, value);

      writer.write(END_ARRAY);
    }
  }
}

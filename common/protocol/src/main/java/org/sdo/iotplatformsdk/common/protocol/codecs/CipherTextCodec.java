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
import org.sdo.iotplatformsdk.common.protocol.types.CipherText;

/**
 * Codec for {@link CipherText}.
 */
public class CipherTextCodec extends Codec<CipherText> {

  private final Codec<ByteBuffer> ivDataCodec = new IvDataCodec();
  private final Codec<ByteBuffer> cryptTextCodec = new ByteArrayCodec();
  private final Codec<Number> lengthCodec = new Uint32Codec();

  @Override
  public Codec<CipherText>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<CipherText>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<ByteBuffer> getIvDataCodec() {
    return ivDataCodec;
  }

  private Codec<ByteBuffer> getCryptTextCodec() {
    return cryptTextCodec;
  }

  private Codec<Number> getLengthCodec() {
    return lengthCodec;
  }

  private class Decoder extends Codec<CipherText>.Decoder {

    @Override
    public CipherText apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_ARRAY);
      final ByteBuffer iv = getIvDataCodec().decoder().apply(in);

      expect(in, COMMA);
      final long size = getLengthCodec().decoder().apply(in).longValue();

      expect(in, COMMA);
      final ByteBuffer ct = getCryptTextCodec().decoder().apply(in);

      expect(in, END_ARRAY);

      if (size != ct.remaining()) {
        throw new IOException("length mismatch");
      }

      return new CipherText(iv, ct);
    }
  }

  private class Encoder extends Codec<CipherText>.Encoder {

    @Override
    public void apply(Writer writer, CipherText value) throws IOException {

      writer.write(BEGIN_ARRAY);
      final ByteBuffer iv = value.getIv();
      getIvDataCodec().encoder().apply(writer, iv);

      writer.write(COMMA);
      final ByteBuffer ct = value.getCt();
      getLengthCodec().encoder().apply(writer, ct.remaining());

      writer.write(COMMA);
      getCryptTextCodec().encoder().apply(writer, ct);

      writer.write(END_ARRAY);
    }
  }
}

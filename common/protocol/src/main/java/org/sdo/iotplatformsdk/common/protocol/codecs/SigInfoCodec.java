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
import org.sdo.iotplatformsdk.common.protocol.types.SigInfo;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureType;

/**
 * Codec for {@link SigInfo}.
 */
public class SigInfoCodec extends Codec<SigInfo> {

  private final Codec<ByteBuffer> infoCodec = new ByteArrayCodec();
  private final Codec<Number> lengthCodec = new Uint16Codec();
  private final Codec<SignatureType> sgTypeCodec = new SignatureTypeCodec();

  @Override
  public Codec<SigInfo>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<SigInfo>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<ByteBuffer> getInfoCodec() {
    return infoCodec;
  }

  private Codec<Number> getLengthCodec() {
    return lengthCodec;
  }

  private Codec<SignatureType> getSgTypeCodec() {
    return sgTypeCodec;
  }

  private class Decoder extends Codec<SigInfo>.Decoder {

    @Override
    public SigInfo apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_ARRAY);
      final SignatureType sgType = getSgTypeCodec().decoder().apply(in);

      expect(in, COMMA);
      final int length = getLengthCodec().decoder().apply(in).intValue();

      expect(in, COMMA);
      final ByteBuffer info = getInfoCodec().decoder().apply(in);

      expect(in, END_ARRAY);

      if (info.remaining() != length) {
        throw new IOException("length mismatch");
      }

      return new SigInfo(sgType, info);
    }
  }

  private class Encoder extends Codec<SigInfo>.Encoder {

    @Override
    public void apply(Writer writer, SigInfo value) throws IOException {

      writer.write(BEGIN_ARRAY);

      getSgTypeCodec().encoder().apply(writer, value.getSgType());

      writer.write(COMMA);
      ByteBuffer info = value.getInfo();
      getLengthCodec().encoder().apply(writer, info.remaining());

      writer.write(COMMA);
      getInfoCodec().encoder().apply(writer, value.getInfo());

      writer.write(END_ARRAY);
    }
  }
}

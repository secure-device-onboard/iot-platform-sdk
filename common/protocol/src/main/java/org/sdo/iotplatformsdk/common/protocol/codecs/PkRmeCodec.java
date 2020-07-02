// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_ARRAY;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.COMMA;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_ARRAY;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import org.sdo.iotplatformsdk.common.protocol.types.Keys;
import org.sdo.iotplatformsdk.common.protocol.util.Buffers;

class PkRmeCodec extends Codec<PublicKey> {

  private final Codec<ByteBuffer> dataCodec = new ByteArrayCodec();
  private final Codec<Number> lengthCodec = new Uint32Codec();

  @Override
  public Codec<PublicKey>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<PublicKey>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<ByteBuffer> getDataCodec() {
    return dataCodec;
  }

  private Codec<Number> getLengthCodec() {
    return lengthCodec;
  }

  private class Decoder extends Codec<PublicKey>.Decoder {

    @Override
    public PublicKey apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_ARRAY);
      final long modBytes = getLengthCodec().decoder().apply(in).longValue();

      expect(in, COMMA);
      final ByteBuffer mod = getDataCodec().decoder().apply(in);

      expect(in, COMMA);
      long expBytes = getLengthCodec().decoder().apply(in).longValue();

      expect(in, COMMA);
      final ByteBuffer exp = getDataCodec().decoder().apply(in);

      expect(in, END_ARRAY);

      if (!(mod.remaining() == modBytes && exp.remaining() == expBytes)) {
        throw new IOException("length mismatch");
      }

      try {
        return Keys.toPublicKey(new RSAPublicKeySpec(new BigInteger(1, Buffers.unwrap(mod)),
            new BigInteger(1, Buffers.unwrap(exp))));

      } catch (NoSuchAlgorithmException e) {
        throw new IOException(e);
      }
    }
  }

  private class Encoder extends Codec<PublicKey>.Encoder {

    @Override
    public void apply(Writer writer, PublicKey value) throws IOException {

      if (!(value instanceof RSAPublicKey)) {
        throw new IllegalArgumentException(value.getClass().getCanonicalName());
      }

      RSAPublicKey key = (RSAPublicKey) value;
      final ByteBuffer mod = ByteBuffer.wrap(key.getModulus().toByteArray());
      final ByteBuffer exp = ByteBuffer.wrap(key.getPublicExponent().toByteArray());

      writer.write(BEGIN_ARRAY);
      getLengthCodec().encoder().apply(writer, mod.remaining());

      writer.write(COMMA);
      getDataCodec().encoder().apply(writer, mod);

      writer.write(COMMA);
      getLengthCodec().encoder().apply(writer, exp.remaining());

      writer.write(COMMA);
      getDataCodec().encoder().apply(writer, exp);

      writer.write(END_ARRAY);
    }
  }
}

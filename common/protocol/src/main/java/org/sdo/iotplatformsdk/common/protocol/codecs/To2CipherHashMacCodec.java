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
import org.sdo.iotplatformsdk.common.protocol.types.HashMac;
import org.sdo.iotplatformsdk.common.protocol.types.To2CipherHashMac;

/**
 * Codec for {@link HashMac}.
 */
public abstract class To2CipherHashMacCodec {

  public static class To2CipherHashMacDecoder implements SdoDecoder<To2CipherHashMac> {

    @Override
    public To2CipherHashMac decode(CharBuffer in) throws IOException {

      final Uint8Codec.Decoder u8d = new Uint8Codec().new Decoder();

      expect(in, BEGIN_ARRAY);
      final int length = u8d.apply(in).intValue();

      expect(in, COMMA);
      final ByteBuffer hash = new ByteArrayCodec().decoder().apply(in);

      expect(in, END_ARRAY);

      if (hash.remaining() != length) {
        throw new IOException("length mismatch");
      }

      return new To2CipherHashMac(hash);
    }
  }

  public static class To2CipherHashMacEncoder implements SdoEncoder<To2CipherHashMac> {

    @Override
    public void encode(Writer out, To2CipherHashMac val) throws IOException {

      final Uint8Codec.Encoder u8e = new Uint8Codec().new Encoder();
      final ByteBuffer hash = val.getHash();

      out.write(BEGIN_ARRAY);
      u8e.apply(out, hash.remaining());

      out.write(COMMA);
      new ByteArrayCodec().encoder().apply(out, hash);

      out.write(END_ARRAY);
    }
  }
}

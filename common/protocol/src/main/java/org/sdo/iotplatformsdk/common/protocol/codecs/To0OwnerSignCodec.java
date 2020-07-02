// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.COMMA;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import org.sdo.iotplatformsdk.common.protocol.codecs.To0OwnerSignTo0dCodec.To0dDecoder;
import org.sdo.iotplatformsdk.common.protocol.codecs.To0OwnerSignTo0dCodec.To0dEncoder;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;
import org.sdo.iotplatformsdk.common.protocol.types.To0OwnerSign;
import org.sdo.iotplatformsdk.common.protocol.types.To0OwnerSignTo0d;

public abstract class To0OwnerSignCodec {

  private static final String TO0D = "to0d";
  private static final String TO1D = "to1d";

  public static class To0OwnerSignDecoder implements SdoDecoder<To0OwnerSign> {

    private final To0dDecoder to0dDec = new To0dDecoder();
    private char[] lastTo1d = new char[0];

    @Override
    public To0OwnerSign decode(final CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);
      expect(in, Json.asKey(TO0D));
      final To0OwnerSignTo0d to0d = to0dDec.decode(in);

      expect(in, COMMA);
      expect(in, Json.asKey(TO1D));
      CharBuffer to1dBuf = in.asReadOnlyBuffer();
      final SignatureBlock to1d = new SignatureBlockCodec.Decoder(null).decode(in);
      to1dBuf.limit(in.position());
      lastTo1d = new char[to1dBuf.remaining()];
      to1dBuf.get(lastTo1d);

      expect(in, END_OBJECT);

      return new To0OwnerSign(to0d, to1d);
    }

    public CharBuffer getLastDc() {
      return to0dDec.getLastDc();
    }

    /**
     * Returns the 'to1d' tag as {@link CharBuffer}.
     *
     * @return {@link CharBuffer}
     */
    public CharBuffer getLastTo1d() {
      CharBuffer cbuf = CharBuffer.allocate(lastTo1d.length);
      cbuf.put(lastTo1d);
      cbuf.flip();
      return cbuf;
    }
  }

  public static class To0OwnerSignEncoder implements SdoEncoder<To0OwnerSign> {

    private final SignatureBlockCodec.Encoder sgEncoder;

    public To0OwnerSignEncoder(SignatureBlockCodec.Encoder sgEncoder) {
      this.sgEncoder = sgEncoder;
    }

    @Override
    public void encode(Writer writer, To0OwnerSign value) throws IOException {

      writer.write(BEGIN_OBJECT);
      writer.write(Json.asKey(TO0D));
      new To0dEncoder().encode(writer, value.getTo0d());

      writer.write(COMMA);
      writer.write(Json.asKey(TO1D));
      sgEncoder.encode(writer, value.getTo1d());

      writer.write(END_OBJECT);
    }
  }
}

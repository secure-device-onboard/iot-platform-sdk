// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_ARRAY;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.COMMA;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_ARRAY;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.PublicKey;
import java.util.function.Consumer;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;

public class SignatureBlockCodec {

  private static final String BO = "bo";
  private static final String PK = "pk";
  private static final String SG = "sg";

  public static class Decoder implements SdoDecoder<SignatureBlock> {

    private final Consumer<CharBuffer> hashFn;

    public Decoder(Consumer<CharBuffer> hashFn) {
      this.hashFn = hashFn;
    }

    @Override
    public SignatureBlock decode(CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);
      expect(in, Json.asKey(BO));

      // the body must be a JSON object
      final CharBuffer bo = in.asReadOnlyBuffer();
      expect(in, BEGIN_OBJECT);

      // loop until all outstanding braces close
      int depth = 1;

      while (depth > 0) {
        char c = in.get();

        if (BEGIN_OBJECT == c) {
          ++depth;

        } else if (END_OBJECT == c) {
          --depth;
        }
      }
      bo.limit(in.position());

      expect(in, COMMA);
      expect(in, Json.asKey(PK));
      CharBuffer pkBuf = in.asReadOnlyBuffer();
      final PublicKey pk = new PublicKeyCodec.Decoder().decode(in);
      pkBuf.limit(in.position());

      Consumer<CharBuffer> hashFn = this.hashFn;
      if (null != hashFn) {
        hashFn.accept(pkBuf);
      }

      expect(in, COMMA);
      expect(in, Json.asKey(SG));

      expect(in, BEGIN_ARRAY);
      final long sglen = new Uint16Codec().decoder().apply(in).longValue();

      expect(in, COMMA);
      ByteBuffer sg = new ByteArrayCodec().decoder().apply(in);
      if (sg.remaining() != sglen) {
        throw new IOException("sg length mismatch");
      }

      expect(in, END_ARRAY);
      expect(in, END_OBJECT);

      return new SignatureBlock(bo, pk, sg);
    }
  }

  public static class Encoder implements SdoEncoder<SignatureBlock> {

    private final PublicKeyCodec.Encoder publicKeyEncoder;

    public Encoder(PublicKeyCodec.Encoder publicKeyEncoder) {
      this.publicKeyEncoder = publicKeyEncoder;
    }

    @Override
    public void encode(Writer writer, SignatureBlock value) throws IOException {

      writer.write(BEGIN_OBJECT);
      writer.write(Json.asKey(BO));
      writer.append(value.getBo());

      writer.write(COMMA);
      writer.write(Json.asKey(PK));
      publicKeyEncoder.encode(writer, value.getPk());

      writer.write(COMMA);
      writer.write(Json.asKey(SG));

      ByteBuffer sg = value.getSg();

      writer.write(BEGIN_ARRAY);
      new Uint16Codec().encoder().apply(writer, sg.remaining());

      writer.write(COMMA);
      new ByteArrayCodec().encoder().apply(writer, sg);

      writer.write(END_ARRAY);
      writer.write(END_OBJECT);
    }
  }
}

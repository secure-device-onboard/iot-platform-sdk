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
import org.sdo.iotplatformsdk.common.protocol.types.HashMac;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.To2Done;

public class To2DoneCodec extends Codec<To2Done> {

  private static final String HMAC = "hmac";
  private static String N6 = "n6";

  @Override
  public Codec<To2Done>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<To2Done>.Encoder encoder() {
    return new Encoder();
  }

  private class Decoder extends Codec<To2Done>.Decoder {

    @Override
    public To2Done apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);

      expect(in, Json.asKey(HMAC));
      final HashMac hmac = new HashMac(in);

      expect(in, COMMA);
      expect(in, Json.asKey(N6));
      final Nonce n6 = new Nonce(in);

      expect(in, END_OBJECT);

      return new To2Done(hmac, n6);
    }
  }

  private class Encoder extends Codec<To2Done>.Encoder {

    @Override
    public void apply(Writer writer, To2Done value) throws IOException {

      writer.write(BEGIN_OBJECT);

      writer.write(Json.asKey(HMAC));
      writer.write(value.getHmac().toString());

      writer.write(COMMA);
      writer.write(Json.asKey(N6));
      writer.write(value.getN6().toString());

      writer.write(END_OBJECT);
    }
  }
}

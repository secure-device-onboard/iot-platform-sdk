// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.To0HelloAck;

public class To0HelloAckCodec extends Codec<To0HelloAck> {

  private static final String N3 = "n3";

  @Override
  public Codec<To0HelloAck>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<To0HelloAck>.Encoder encoder() {
    return new Encoder();
  }

  private class Decoder extends Codec<To0HelloAck>.Decoder {

    @Override
    public To0HelloAck apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);

      expect(in, Json.asKey(N3));
      Nonce n3 = new Nonce(in);

      expect(in, END_OBJECT);

      return new To0HelloAck(n3);
    }
  }

  private class Encoder extends Codec<To0HelloAck>.Encoder {

    @Override
    public void apply(Writer writer, To0HelloAck value) throws IOException {

      writer.write(BEGIN_OBJECT);

      writer.write(Json.asKey(N3));
      writer.write(value.getN3().toString());

      writer.write(END_OBJECT);
    }
  }
}

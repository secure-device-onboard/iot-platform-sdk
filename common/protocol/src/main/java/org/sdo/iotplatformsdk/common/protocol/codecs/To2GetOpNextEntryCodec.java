// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import org.sdo.iotplatformsdk.common.protocol.types.To2GetOpNextEntry;

public class To2GetOpNextEntryCodec extends Codec<To2GetOpNextEntry> {

  private static final String ENN = "enn";

  private final Codec<Number> ennCodec = new Uint32Codec();

  @Override
  public Codec<To2GetOpNextEntry>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<To2GetOpNextEntry>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<Number> getEnnCodec() {
    return ennCodec;
  }

  private class Decoder extends Codec<To2GetOpNextEntry>.Decoder {

    @Override
    public To2GetOpNextEntry apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);
      expect(in, Json.asKey(ENN));
      Number enn = getEnnCodec().decoder().apply(in);
      expect(in, END_OBJECT);

      return new To2GetOpNextEntry(enn.intValue());
    }
  }

  private class Encoder extends Codec<To2GetOpNextEntry>.Encoder {

    @Override
    public void apply(Writer writer, To2GetOpNextEntry value) throws IOException {

      writer.write(BEGIN_OBJECT);
      writer.write(Json.asKey(ENN));
      getEnnCodec().encoder().apply(writer, value.getEnn());
      writer.write(END_OBJECT);
    }
  }
}

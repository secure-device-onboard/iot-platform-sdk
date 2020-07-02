// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;

public class SdoErrorCodeCodec extends Codec<SdoErrorCode> {

  private final Codec<Number> numberCodec = new Uint16Codec();

  @Override
  public Codec<SdoErrorCode>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<SdoErrorCode>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<Number> getNumberCodec() {
    return numberCodec;
  }

  private class Decoder extends Codec<SdoErrorCode>.Decoder {

    @Override
    public SdoErrorCode apply(CharBuffer in) throws IOException {

      return SdoErrorCode.fromNumber(getNumberCodec().decoder().apply(in));
    }
  }

  private class Encoder extends Codec<SdoErrorCode>.Encoder {

    @Override
    public void apply(Writer writer, SdoErrorCode value) throws IOException {
      getNumberCodec().encoder().apply(writer, value.toInteger());
    }
  }
}

// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureType;

/**
 * Codec for {@link SignatureType}.
 */
public class SignatureTypeCodec extends Codec<SignatureType> {

  private final Codec<Number> codec = new Uint8Codec();

  private Codec<Number> getCodec() {
    return codec;
  }

  @Override
  public Codec<SignatureType>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<SignatureType>.Encoder encoder() {
    return new Encoder();
  }

  private class Decoder extends Codec<SignatureType>.Decoder {

    @Override
    public SignatureType apply(CharBuffer in) throws IOException {
      return SignatureType.fromNumber(getCodec().decoder().apply(in));
    }
  }

  private class Encoder extends Codec<SignatureType>.Encoder {

    @Override
    public void apply(Writer writer, SignatureType value) throws IOException {
      getCodec().encoder().apply(writer, value.toInteger());
    }
  }
}

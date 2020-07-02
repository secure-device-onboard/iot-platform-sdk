// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import org.sdo.iotplatformsdk.common.protocol.types.KeyType;

/**
 * Codec for {@link KeyType}.
 */
public class KeyTypeCodec extends Codec<KeyType> {

  private final Codec<Number> numberCodec = new Uint8Codec();

  @Override
  public Codec<KeyType>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<KeyType>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<Number> getNumberCodec() {
    return numberCodec;
  }

  private class Decoder extends Codec<KeyType>.Decoder {

    @Override
    public KeyType apply(CharBuffer in) throws IOException {

      return KeyType.fromNumber(getNumberCodec().decoder().apply(in));
    }
  }

  private class Encoder extends Codec<KeyType>.Encoder {

    @Override
    public void apply(Writer writer, KeyType value) throws IOException {
      getNumberCodec().encoder().apply(writer, value.toInteger());
    }
  }
}

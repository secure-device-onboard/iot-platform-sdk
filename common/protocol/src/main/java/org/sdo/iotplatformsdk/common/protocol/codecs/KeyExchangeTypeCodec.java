// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import org.sdo.iotplatformsdk.common.protocol.types.KeyExchangeType;

/**
 * Codec for {@link KeyExchangeType}.
 */
public class KeyExchangeTypeCodec extends Codec<KeyExchangeType> {

  private final Codec<String> stringCodec = new StringCodec();

  @Override
  public Codec<KeyExchangeType>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<KeyExchangeType>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<String> getStringCodec() {
    return stringCodec;
  }

  private class Decoder extends Codec<KeyExchangeType>.Decoder {

    @Override
    public KeyExchangeType apply(CharBuffer in) throws IOException {

      String s = getStringCodec().decoder().apply(in);
      return KeyExchangeType.valueOf(s);
    }
  }

  private class Encoder extends Codec<KeyExchangeType>.Encoder {

    @Override
    public void apply(Writer writer, KeyExchangeType value) throws IOException {
      getStringCodec().encoder().apply(writer, value.toString());
    }
  }
}

// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import org.sdo.iotplatformsdk.common.protocol.types.To0Hello;

public class To0HelloCodec extends Codec<To0Hello> {

  @Override
  public Codec<To0Hello>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<To0Hello>.Encoder encoder() {
    return new Encoder();
  }

  private class Decoder extends Codec<To0Hello>.Decoder {

    @Override
    public To0Hello apply(CharBuffer in) throws IOException {
      return new To0Hello();
    }
  }

  private class Encoder extends Codec<To0Hello>.Encoder {

    @Override
    public void apply(Writer writer, To0Hello value) throws IOException {
      // no body
    }
  }
}

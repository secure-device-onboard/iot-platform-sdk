/*******************************************************************************
 * Copyright 2020 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

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

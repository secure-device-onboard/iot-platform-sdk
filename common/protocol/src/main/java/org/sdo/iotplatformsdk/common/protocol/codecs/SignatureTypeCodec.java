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

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

import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import java.security.PublicKey;

class PkNullCodec extends Codec<PublicKey> {

  private static final String PK_NULL = "[0]";

  @Override
  public Codec<PublicKey>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<PublicKey>.Encoder encoder() {
    return new Encoder();
  }

  private class Decoder extends Codec<PublicKey>.Decoder {

    @Override
    public PublicKey apply(CharBuffer in) throws IOException {

      expect(in, PK_NULL);
      return null;
    }
  }

  private class Encoder extends Codec<PublicKey>.Encoder {

    @Override
    public void apply(Writer writer, PublicKey value) throws IOException {
      writer.write(PK_NULL);
    }
  }
}

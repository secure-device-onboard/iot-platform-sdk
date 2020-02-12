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

import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.QUOTE;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Codec for ByteArray.
 *
 * @see "SDO Protocol Specification, 1.13b, 3.2: Composite Types"
 */
public class ByteArrayCodec extends Codec<ByteBuffer> {

  @Override
  public Codec<ByteBuffer>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<ByteBuffer>.Encoder encoder() {
    return new Encoder();
  }

  private class Decoder extends Codec<ByteBuffer>.Decoder {

    private static final int CHARS_PER_B64_BLOCK = 4;

    @Override
    public ByteBuffer apply(CharBuffer in) throws IOException {

      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      WritableByteChannel byteChannel = Channels.newChannel(bytes);
      char[] b64 = new char[CHARS_PER_B64_BLOCK];

      expect(in, QUOTE);

      for (;;) {

        // Is the string ending, or is this another b64 block?
        b64[0] = in.get();
        if (QUOTE.equals(b64[0])) {
          return ByteBuffer.wrap(bytes.toByteArray());

        } else {
          in.get(b64, 1, CHARS_PER_B64_BLOCK - 1);
        }

        // Base64 expects ISO 8859.1 byte data, not java chars
        ByteBuffer b64AsIso8859 = StandardCharsets.ISO_8859_1.encode(CharBuffer.wrap(b64));
        ByteBuffer decodedBytes = Base64.getDecoder().decode(b64AsIso8859);
        byteChannel.write(decodedBytes);
      }
    }
  }

  private class Encoder extends Codec<ByteBuffer>.Encoder {

    @Override
    public void apply(Writer writer, ByteBuffer value) throws IOException {

      writer.append(QUOTE);
      ByteBuffer b64 = Base64.getEncoder().encode(value); // Base64 encodes to ISO 8859.1
      writer.append(StandardCharsets.ISO_8859_1.decode(b64));
      writer.append(QUOTE);
    }
  }
}

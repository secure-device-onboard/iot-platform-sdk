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

import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_ARRAY;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.COMMA;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_ARRAY;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import org.sdo.iotplatformsdk.common.protocol.types.HashMac;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;

/**
 * Codec for {@link HashMac}.
 */
public abstract class HashMacCodec {

  public static class HashMacDecoder implements SdoDecoder<HashMac> {

    @Override
    public HashMac decode(CharBuffer in) throws IOException {

      final Uint8Codec.Decoder u8d = new Uint8Codec().new Decoder();

      expect(in, BEGIN_ARRAY);
      final int length = u8d.apply(in).intValue();

      expect(in, COMMA);
      final int hashType = u8d.apply(in).intValue();

      expect(in, COMMA);
      final ByteBuffer hash = new ByteArrayCodec().decoder().apply(in);

      expect(in, END_ARRAY);

      if (hash.remaining() != length) {
        throw new IOException("length mismatch");
      }

      return new HashMac(MacType.fromNumber(hashType), hash);
    }
  }

  public static class HashMacEncoder implements SdoEncoder<HashMac> {

    @Override
    public void encode(Writer out, HashMac val) throws IOException {

      final Uint8Codec.Encoder u8e = new Uint8Codec().new Encoder();
      final ByteBuffer hash = val.getHash();

      out.write(BEGIN_ARRAY);
      u8e.apply(out, hash.remaining());

      out.write(COMMA);
      u8e.apply(out, val.getType().getCode());

      out.write(COMMA);
      new ByteArrayCodec().encoder().apply(out, hash);

      out.write(END_ARRAY);
    }
  }
}

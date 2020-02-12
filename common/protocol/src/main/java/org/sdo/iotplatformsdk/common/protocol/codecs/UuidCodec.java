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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.UUID;

public class UuidCodec extends Codec<UUID> {

  private final Codec<ByteBuffer> arrayCodec = new ByteArrayCodec();

  @Override
  public Codec<UUID>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<UUID>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<ByteBuffer> getArrayCodec() {
    return arrayCodec;
  }

  private class Decoder extends Codec<UUID>.Decoder {

    @Override
    public UUID apply(CharBuffer in) throws IOException {

      ByteBuffer bb = getArrayCodec().decoder().apply(in);
      long hword = bb.getLong();
      long lword = bb.getLong();
      return new UUID(hword, lword);
    }
  }

  private class Encoder extends Codec<UUID>.Encoder {

    @Override
    public void apply(Writer writer, UUID value) throws IOException {

      ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
      bb.putLong(value.getMostSignificantBits());
      bb.putLong(value.getLeastSignificantBits());
      bb.flip();
      getArrayCodec().encoder().apply(writer, bb);
    }
  }
}

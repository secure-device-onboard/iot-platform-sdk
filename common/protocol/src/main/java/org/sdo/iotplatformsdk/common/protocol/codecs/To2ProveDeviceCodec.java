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

import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.COMMA;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.UUID;

import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.To2ProveDevice;

public class To2ProveDeviceCodec extends Codec<To2ProveDevice> {

  private static final String AI = "ai";
  private static final String G2 = "g2";
  private static final String N6 = "n6";
  private static final String N7 = "n7";
  private static final String NN = "nn";
  private static final String XB = "xB";

  private final Codec<ByteBuffer> aiCodec = new AppIdCodec();
  private final Codec<UUID> g2Codec = new UuidCodec();
  private final Codec<Number> nnCodec = new Uint32Codec();
  private final Codec<ByteBuffer> xbCodec = new KexParamCodec();

  @Override
  public Codec<To2ProveDevice>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<To2ProveDevice>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<ByteBuffer> getAiCodec() {
    return aiCodec;
  }

  private Codec<UUID> getG2Codec() {
    return g2Codec;
  }

  private Codec<Number> getNnCodec() {
    return nnCodec;
  }

  private Codec<ByteBuffer> getXbCodec() {
    return xbCodec;
  }

  private class Decoder extends Codec<To2ProveDevice>.Decoder {

    @Override
    public To2ProveDevice apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);

      expect(in, Json.asKey(AI));
      final ByteBuffer ai = getAiCodec().decoder().apply(in);

      expect(in, COMMA);
      expect(in, Json.asKey(N6));
      final Nonce n6 = new Nonce(in);

      expect(in, COMMA);
      expect(in, Json.asKey(N7));
      final Nonce n7 = new Nonce(in);

      expect(in, COMMA);
      expect(in, Json.asKey(G2));
      final UUID g2 = getG2Codec().decoder().apply(in);

      expect(in, COMMA);
      expect(in, Json.asKey(NN));
      final Number nn = getNnCodec().decoder().apply(in);

      expect(in, COMMA);
      expect(in, Json.asKey(XB));
      final ByteBuffer xb = getXbCodec().decoder().apply(in);

      expect(in, END_OBJECT);

      return new To2ProveDevice(ai, n6, n7, g2, nn.intValue(), xb);
    }
  }

  private class Encoder extends Codec<To2ProveDevice>.Encoder {

    @Override
    public void apply(Writer writer, To2ProveDevice value) throws IOException {

      writer.write(BEGIN_OBJECT);

      writer.write(Json.asKey(AI));
      getAiCodec().encoder().apply(writer, value.getAi());

      writer.write(COMMA);
      writer.write(Json.asKey(N6));
      writer.write(value.getN6().toString());

      writer.write(COMMA);
      writer.write(Json.asKey(N7));
      writer.write(value.getN7().toString());

      writer.write(COMMA);
      writer.write(Json.asKey(G2));
      getG2Codec().encoder().apply(writer, value.getG2());

      writer.write(COMMA);
      writer.write(Json.asKey(NN));
      getNnCodec().encoder().apply(writer, value.getNn());

      writer.write(COMMA);
      writer.write(Json.asKey(XB));
      getXbCodec().encoder().apply(writer, value.getXb());

      writer.write(END_OBJECT);
    }
  }
}

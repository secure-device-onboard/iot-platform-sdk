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
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import java.time.Duration;

import org.sdo.iotplatformsdk.common.protocol.types.To0AcceptOwner;

public class To0AcceptOwnerCodec extends Codec<To0AcceptOwner> {

  private static final String WS = "ws";

  private final Codec<Number> wsCodec = new Uint32Codec();

  @Override
  public Codec<To0AcceptOwner>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<To0AcceptOwner>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<Number> getWsCodec() {
    return wsCodec;
  }

  private class Decoder extends Codec<To0AcceptOwner>.Decoder {

    @Override
    public To0AcceptOwner apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);

      expect(in, Json.asKey(WS));
      Duration ws = Duration.ofSeconds(getWsCodec().decoder().apply(in).longValue());

      expect(in, END_OBJECT);

      return new To0AcceptOwner(ws);
    }
  }

  private class Encoder extends Codec<To0AcceptOwner>.Encoder {

    @Override
    public void apply(Writer writer, To0AcceptOwner value) throws IOException {

      writer.write(BEGIN_OBJECT);

      writer.write(Json.asKey(WS));
      getWsCodec().encoder().apply(writer, value.getWs().getSeconds());

      writer.write(END_OBJECT);
    }
  }
}

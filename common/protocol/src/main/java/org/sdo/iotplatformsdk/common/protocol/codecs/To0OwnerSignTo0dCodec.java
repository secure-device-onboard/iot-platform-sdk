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
import java.nio.CharBuffer;
import java.time.Duration;

import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec.OwnershipProxyDecoder;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec.OwnershipProxyEncoder;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.To0OwnerSignTo0d;

public abstract class To0OwnerSignTo0dCodec {

  private static final String N3 = "n3";
  private static final String OP = "op";
  private static final String WS = "ws";

  public static class To0dDecoder implements SdoDecoder<To0OwnerSignTo0d> {

    private final OwnershipProxyDecoder opDec = new OwnershipProxyDecoder();

    @Override
    public To0OwnerSignTo0d decode(final CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);
      expect(in, Json.asKey(OP));
      final OwnershipProxy op = opDec.decode(in);

      expect(in, COMMA);
      expect(in, Json.asKey(WS));
      final Duration ws = Duration.ofSeconds(new Uint32Codec().decoder().apply(in).longValue());

      expect(in, COMMA);
      expect(in, Json.asKey(N3));
      final Nonce n3 = new Nonce(in);

      expect(in, END_OBJECT);

      return new To0OwnerSignTo0d(op, ws, n3);
    }

    CharBuffer getLastDc() {
      return opDec.getLastDc();
    }
  }

  public static class To0dEncoder implements SdoEncoder<To0OwnerSignTo0d> {

    @Override
    public void encode(final Writer writer, final To0OwnerSignTo0d value) throws IOException {

      writer.write(BEGIN_OBJECT);
      writer.write(Json.asKey(OP));
      new OwnershipProxyEncoder().encode(writer, value.getOp());

      writer.write(COMMA);
      writer.write(Json.asKey(WS));
      new Uint32Codec().encoder().apply(writer, value.getWs().getSeconds());

      writer.write(COMMA);
      writer.write(Json.asKey(N3));
      writer.write(value.getN3().toString());

      writer.write(END_OBJECT);
    }
  }
}

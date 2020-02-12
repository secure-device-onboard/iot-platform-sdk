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

import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;

public class SdoErrorCodec extends Codec<SdoError> {

  private static final String EC = "ec";
  private static final String EM = "em";
  private static final String EMSG = "emsg";

  private final Codec<SdoErrorCode> ecCodec = new SdoErrorCodeCodec();
  private final Codec<String> emCodec = new StringCodec();
  private final Codec<Number> emsgCodec = new Uint8Codec();

  @Override
  public Codec<SdoError>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<SdoError>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<SdoErrorCode> getEcCodec() {
    return ecCodec;
  }

  private Codec<String> getEmCodec() {
    return emCodec;
  }

  private Codec<Number> getEmsgCodec() {
    return emsgCodec;
  }

  private class Decoder extends Codec<SdoError>.Decoder {

    @Override
    public SdoError apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);
      expect(in, Json.asKey(EC));
      final SdoErrorCode ec = getEcCodec().decoder().apply(in);

      expect(in, COMMA);
      expect(in, Json.asKey(EMSG));
      final Number emsg = getEmsgCodec().decoder().apply(in);

      expect(in, COMMA);
      expect(in, Json.asKey(EM));
      final String em = getEmCodec().decoder().apply(in);

      expect(in, END_OBJECT);

      return new SdoError(ec, emsg.intValue(), em);
    }
  }

  private class Encoder extends Codec<SdoError>.Encoder {

    @Override
    public void apply(Writer writer, SdoError value) throws IOException {

      writer.write(BEGIN_OBJECT);
      writer.write(Json.asKey(EC));
      getEcCodec().encoder().apply(writer, value.getEc());

      writer.write(COMMA);
      writer.write(Json.asKey(EMSG));
      getEmsgCodec().encoder().apply(writer, value.getEmsg());

      writer.write(COMMA);
      writer.write(Json.asKey(EM));
      getEmCodec().encoder().apply(writer, value.getEm());

      writer.write(END_OBJECT);
    }
  }
}

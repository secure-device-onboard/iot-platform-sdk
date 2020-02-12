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
import java.io.StringWriter;
import java.io.Writer;
import java.nio.CharBuffer;

import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.To2OwnerServiceInfo;

public class To2OwnerServiceInfoCodec extends Codec<To2OwnerServiceInfo> {

  private static String NN = "nn";
  private static String SV = "sv";

  private final Codec<Number> nnCodec = new Uint32Codec();
  private final Codec<ServiceInfo> svCodec = new ServiceInfoCodec();

  @Override
  public Codec<To2OwnerServiceInfo>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<To2OwnerServiceInfo>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<Number> getNnCodec() {
    return nnCodec;
  }

  private Codec<ServiceInfo> getSvCodec() {
    return svCodec;
  }

  private class Decoder extends Codec<To2OwnerServiceInfo>.Decoder {

    @Override
    public To2OwnerServiceInfo apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);

      expect(in, Json.asKey(NN));
      final Number nn = getNnCodec().decoder().apply(in);

      expect(in, COMMA);
      expect(in, Json.asKey(SV));
      final ServiceInfo sv = getSvCodec().decoder().apply(in);

      expect(in, END_OBJECT);

      final StringWriter writer = new StringWriter();
      getSvCodec().encoder().apply(writer, sv);

      return new To2OwnerServiceInfo(nn.intValue(), writer.toString());
    }
  }

  private class Encoder extends Codec<To2OwnerServiceInfo>.Encoder {

    @Override
    public void apply(Writer writer, To2OwnerServiceInfo value) throws IOException {

      writer.write(BEGIN_OBJECT);

      writer.write(Json.asKey(NN));
      getNnCodec().encoder().apply(writer, value.getNn());

      writer.write(COMMA);
      writer.write(Json.asKey(SV));
      // getSvCodec().encoder().apply(writer, value.getSv());
      writer.write(value.getSv());

      writer.write(END_OBJECT);
    }
  }
}

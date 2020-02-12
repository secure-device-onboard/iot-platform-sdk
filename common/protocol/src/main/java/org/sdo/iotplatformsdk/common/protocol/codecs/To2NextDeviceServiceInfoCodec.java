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

import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.To2NextDeviceServiceInfo;

public class To2NextDeviceServiceInfoCodec extends Codec<To2NextDeviceServiceInfo> {

  private static String DSI = "dsi";
  private static String NN = "nn";

  private final Codec<ServiceInfo> dsiCodec = new ServiceInfoCodec();
  private final Codec<Number> nnCodec = new Uint32Codec();

  @Override
  public Codec<To2NextDeviceServiceInfo>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<To2NextDeviceServiceInfo>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<ServiceInfo> getDsiCodec() {
    return dsiCodec;
  }

  private Codec<Number> getNnCodec() {
    return nnCodec;
  }

  private class Decoder extends Codec<To2NextDeviceServiceInfo>.Decoder {

    @Override
    public To2NextDeviceServiceInfo apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);

      expect(in, Json.asKey(NN));
      final Number nn = getNnCodec().decoder().apply(in);

      expect(in, COMMA);
      expect(in, Json.asKey(DSI));
      final ServiceInfo dsi = getDsiCodec().decoder().apply(in);

      expect(in, END_OBJECT);

      return new To2NextDeviceServiceInfo(nn.intValue(), dsi);
    }
  }

  private class Encoder extends Codec<To2NextDeviceServiceInfo>.Encoder {

    @Override
    public void apply(Writer writer, To2NextDeviceServiceInfo value) throws IOException {

      writer.write(BEGIN_OBJECT);

      writer.write(Json.asKey(NN));
      getNnCodec().encoder().apply(writer, value.getNn());

      writer.write(COMMA);
      writer.write(Json.asKey(DSI));
      getDsiCodec().encoder().apply(writer, value.getDsi());

      writer.write(END_OBJECT);
    }
  }
}

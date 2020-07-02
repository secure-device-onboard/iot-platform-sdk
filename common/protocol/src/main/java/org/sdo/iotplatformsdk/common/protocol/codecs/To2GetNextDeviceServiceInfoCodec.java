// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.COMMA;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import org.sdo.iotplatformsdk.common.protocol.types.PreServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.To2GetNextDeviceServiceInfo;

public class To2GetNextDeviceServiceInfoCodec extends Codec<To2GetNextDeviceServiceInfo> {

  private static final String NN = "nn";
  private static final String PSI = "psi";

  private final Codec<Number> nnCodec = new Uint32Codec();
  private final Codec<PreServiceInfo> psiCodec = new PreServiceInfoCodec();

  @Override
  public Codec<To2GetNextDeviceServiceInfo>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<To2GetNextDeviceServiceInfo>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<Number> getNnCodec() {
    return nnCodec;
  }

  private Codec<PreServiceInfo> getPsiCodec() {
    return psiCodec;
  }

  private class Decoder extends Codec<To2GetNextDeviceServiceInfo>.Decoder {

    @Override
    public To2GetNextDeviceServiceInfo apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);
      expect(in, Json.asKey(NN));
      final Number nn = getNnCodec().decoder().apply(in);

      expect(in, COMMA);
      expect(in, Json.asKey(PSI));
      final PreServiceInfo psi = getPsiCodec().decoder().apply(in);

      expect(in, END_OBJECT);

      return new To2GetNextDeviceServiceInfo(nn.intValue(), psi);
    }
  }

  private class Encoder extends Codec<To2GetNextDeviceServiceInfo>.Encoder {

    @Override
    public void apply(Writer writer, To2GetNextDeviceServiceInfo value) throws IOException {

      writer.write(BEGIN_OBJECT);
      writer.write(Json.asKey(NN));
      getNnCodec().encoder().apply(writer, value.getNn());

      writer.write(COMMA);
      writer.write(Json.asKey(PSI));
      getPsiCodec().encoder().apply(writer, value.getPsi());

      writer.write(END_OBJECT);
    }
  }
}

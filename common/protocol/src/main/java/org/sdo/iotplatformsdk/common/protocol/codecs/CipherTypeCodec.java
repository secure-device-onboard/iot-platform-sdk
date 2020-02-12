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
import java.nio.CharBuffer;

import org.sdo.iotplatformsdk.common.protocol.types.CipherAlgorithm;
import org.sdo.iotplatformsdk.common.protocol.types.CipherBlockMode;
import org.sdo.iotplatformsdk.common.protocol.types.CipherType;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;

/**
 * Codec for {@link CipherType}.
 *
 * <p>In SDO, cipher transformations are written as algorithm/mode/mac-type Unlike JCE cipher
 * transforms, SDO encodes the key length in the algorithm part. Unlike JCE, SDO omits the padding
 * part. In SDO padding is implied by mode. Unlike JCE, SDO cipher transforms also include a MAC
 * step.
 */
public class CipherTypeCodec extends Codec<CipherType> {

  private final Decoder decoder = new Decoder();
  private final Encoder encoder = new Encoder();

  @Override
  public Codec<CipherType>.Decoder decoder() {
    return decoder;
  }

  @Override
  public Codec<CipherType>.Encoder encoder() {
    return encoder;
  }

  private class Decoder extends Codec<CipherType>.Decoder {

    @Override
    public CipherType apply(CharBuffer in) throws IOException {

      String ins = new StringCodec().decoder().apply(in);
      String[] fields = ins.split(SEPARATOR);
      if (3 != fields.length) {
        throw new IllegalArgumentException();
      }

      final CipherAlgorithm algorithm = CipherAlgorithm.valueOf(fields[0]);
      final CipherBlockMode mode = CipherBlockMode.valueOf(fields[1]);
      final MacType macType = MacType.fromSdoName(fields[2]);

      return new CipherType(algorithm, mode, macType);
    }
  }

  private static final String SEPARATOR = "/";

  private class Encoder extends Codec<CipherType>.Encoder {

    @Override
    public void apply(Writer writer, CipherType value) throws IOException {

      StringBuilder builder = new StringBuilder();
      builder.append(value.getAlgorithm().toString());

      builder.append(SEPARATOR);
      builder.append(value.getMode().toString());

      builder.append(SEPARATOR);
      builder.append(value.getMacType().getSdoName());

      new StringCodec().encoder().apply(writer, builder.toString());
    }
  }
}

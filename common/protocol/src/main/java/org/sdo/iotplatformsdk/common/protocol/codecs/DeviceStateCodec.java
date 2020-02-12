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

import org.sdo.iotplatformsdk.common.protocol.types.DeviceState;

/**
 * Codec for {@link DeviceState}.
 */
public class DeviceStateCodec extends Codec<DeviceState> {

  private final Codec<Number> numberCodec = new Uint32Codec();

  @Override
  public Codec<DeviceState>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<DeviceState>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<Number> getNumberCodec() {
    return numberCodec;
  }

  private class Decoder extends Codec<DeviceState>.Decoder {

    @Override
    public DeviceState apply(CharBuffer in) throws IOException {
      return DeviceState.fromNumber(getNumberCodec().decoder().apply(in));
    }
  }

  private class Encoder extends Codec<DeviceState>.Encoder {

    @Override
    public void apply(Writer writer, DeviceState value) throws IOException {
      getNumberCodec().encoder().apply(writer, value.toInteger());
    }
  }
}

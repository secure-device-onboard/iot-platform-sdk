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
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.COLON;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.COMMA;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.QUOTE;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfoEntry;

public class ServiceInfoCodec extends Codec<ServiceInfo> {

  private final Codec<String> keyCodec = new StringCodec();
  private final Codec<String> valueCodec = new StringCodec();

  @Override
  public Codec<ServiceInfo>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<ServiceInfo>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<String> getValueCodec() {
    return valueCodec;
  }

  private Codec<String> getKeyCodec() {
    return keyCodec;
  }

  private class Decoder extends Codec<ServiceInfo>.Decoder {

    @Override
    public ServiceInfo apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);

      ServiceInfo result = new ServiceInfo();
      Codec<String>.Decoder keyDec = getKeyCodec().decoder();
      Codec<String>.Decoder valueDec = getValueCodec().decoder();

      while (true) {

        // This could be:
        // A quote (") if this is the beginning of a new key.
        // A comma (,) if we just finished reading a record and there are more to come...
        // END_OBJECT (}) if the list has finished,
        in.mark();
        char c = in.get();

        if (QUOTE.equals(c)) {

          // The string codec will expect the quote, so put it back before decoding
          in.reset();
          String key = keyDec.apply(in);
          expect(in, COLON);
          String val = valueDec.apply(in);
          result.add(new ServiceInfoEntry(key, val));

        } else if (COMMA.equals(c)) {

          // If we've already seen one element, a comma is expected between them.
          if (result.size() < 1) {
            throw new IOException("unexpected separator");
          }

        } else if (END_OBJECT.equals(c)) {

          return result;

        } else {
          throw new IOException("unexpected input: " + c);
        }
      }
    }
  }

  private class Encoder extends Codec<ServiceInfo>.Encoder {

    @Override
    public void apply(Writer writer, ServiceInfo value) throws IOException {

      writer.write(BEGIN_OBJECT);

      Character separator = null;
      Codec<String>.Encoder keyEnc = getKeyCodec().encoder();
      Codec<String>.Encoder valueEnc = getValueCodec().encoder();

      for (ServiceInfoEntry entry : value) {

        if (null != separator) {
          writer.write(separator);

        } else {
          separator = COMMA;
        }

        keyEnc.apply(writer, entry.getKey().toString());
        writer.write(COLON);
        valueEnc.apply(writer, entry.getValue().toString());
      }

      writer.write(END_OBJECT);
    }
  }
}

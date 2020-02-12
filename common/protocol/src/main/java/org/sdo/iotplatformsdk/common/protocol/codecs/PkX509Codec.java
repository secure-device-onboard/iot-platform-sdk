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

import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_ARRAY;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.COMMA;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_ARRAY;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import org.sdo.iotplatformsdk.common.protocol.types.Keys;
import org.sdo.iotplatformsdk.common.protocol.util.Buffers;

class PkX509Codec extends Codec<PublicKey> {

  private final Codec<ByteBuffer> dataCodec = new ByteArrayCodec();
  private final Codec<Number> lengthCodec = new Uint32Codec();

  @Override
  public Codec<PublicKey>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<PublicKey>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<ByteBuffer> getDataCodec() {
    return dataCodec;
  }

  private Codec<Number> getLengthCodec() {
    return lengthCodec;
  }

  private class Decoder extends Codec<PublicKey>.Decoder {

    @Override
    public PublicKey apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_ARRAY);
      long len = getLengthCodec().decoder().apply(in).longValue();

      expect(in, COMMA);
      ByteBuffer encoded = getDataCodec().decoder().apply(in);

      expect(in, END_ARRAY);

      if (encoded.remaining() != len) {
        throw new IOException("length mismatch");
      }

      try {
        return Keys.toPublicKey(new X509EncodedKeySpec(Buffers.unwrap(encoded)));

      } catch (NoSuchAlgorithmException e) {
        throw new IOException(e);
      }
    }
  }

  private class Encoder extends Codec<PublicKey>.Encoder {

    @Override
    public void apply(Writer writer, PublicKey value) throws IOException {

      X509EncodedKeySpec x509 = new X509EncodedKeySpec(value.getEncoded());
      ByteBuffer encoded = ByteBuffer.wrap(x509.getEncoded());

      writer.write(BEGIN_ARRAY);
      getLengthCodec().encoder().apply(writer, encoded.remaining());

      writer.write(COMMA);
      getDataCodec().encoder().apply(writer, encoded);

      writer.write(END_ARRAY);
    }
  }
}

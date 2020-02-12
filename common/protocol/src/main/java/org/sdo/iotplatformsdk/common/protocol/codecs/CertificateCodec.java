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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import org.sdo.iotplatformsdk.common.protocol.util.Buffers;

/**
 * Codec for {@link Certificate}.
 */
public class CertificateCodec extends Codec<Certificate> {

  static final String X_509 = "X.509";
  private final Codec<ByteBuffer> certBytesCodec = new ByteArrayCodec();
  private final Codec<Number> lengthCodec = new Uint16Codec();

  @Override
  public Codec<Certificate>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<Certificate>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<ByteBuffer> getCertBytesCodec() {
    return certBytesCodec;
  }

  private Codec<Number> getLengthCodec() {
    return lengthCodec;
  }

  private class Decoder extends Codec<Certificate>.Decoder {

    @Override
    public Certificate apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_ARRAY);

      final int length = getLengthCodec().decoder().apply(in).intValue();

      expect(in, COMMA);
      ByteBuffer certBytes = getCertBytesCodec().decoder().apply(in);
      if (certBytes.remaining() != length) {
        throw new IOException("certBytes length mismatch");
      }
      expect(in, END_ARRAY);

      try {
        CertificateFactory certificateFactory = CertificateFactory.getInstance(X_509);
        return certificateFactory
            .generateCertificate(new ByteArrayInputStream(Buffers.unwrap(certBytes)));

      } catch (CertificateException e) {
        throw new IOException(e);
      }
    }
  }

  private class Encoder extends Codec<Certificate>.Encoder {

    @Override
    public void apply(Writer writer, Certificate value) throws IOException {

      ByteBuffer der;
      try {
        der = ByteBuffer.wrap(value.getEncoded()); // X509 encodes to ASN.1 DER

      } catch (CertificateEncodingException e) {
        throw new IOException(e);
      }

      writer.write(BEGIN_ARRAY);
      getLengthCodec().encoder().apply(writer, der.remaining());

      writer.write(COMMA);
      getCertBytesCodec().encoder().apply(writer, der);

      writer.write(END_ARRAY);
    }
  }
}

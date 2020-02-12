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
import java.nio.CharBuffer;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Codec for {@link CertPath}.
 */
public class CertPathCodec extends Codec<CertPath> {

  // only X509 certificates are supported, with no indication of future expansion,
  // so a single constant will suffice as a placeholder.
  private static final int TYPE_X509 = 1;

  private final Codec<Number> typeCodec = new Uint8Codec();
  private final Codec<Number> lengthCodec = new Uint8Codec();
  private final Codec<Certificate> certCodec = new CertificateCodec();

  private Codec<Certificate> getCertCodec() {
    return certCodec;
  }

  private Codec<Number> getTypeCodec() {
    return typeCodec;
  }

  private Codec<Number> getLengthCodec() {
    return lengthCodec;
  }

  @Override
  public Codec<CertPath>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<CertPath>.Encoder encoder() {
    return new Encoder();
  }

  private class Decoder extends Codec<CertPath>.Decoder {

    @Override
    public CertPath apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_ARRAY);
      final int type = getTypeCodec().decoder().apply(in).intValue();

      if (TYPE_X509 != type) {
        throw new UnsupportedOperationException("unsupported certificate type: " + type);
      }

      expect(in, COMMA);
      final int length = getLengthCodec().decoder().apply(in).intValue();

      expect(in, COMMA);
      expect(in, BEGIN_ARRAY);
      Character separator = null;
      List<Certificate> certificates = new ArrayList<>();

      for (int i = 0; i < length; ++i) {

        if (separator != null) {
          expect(in, separator);

        } else {
          separator = COMMA;
        }

        certificates.add(getCertCodec().decoder().apply(in));
      }

      expect(in, END_ARRAY);
      expect(in, END_ARRAY);

      try {
        CertificateFactory factory = CertificateFactory.getInstance(X_509);
        return factory.generateCertPath(certificates);

      } catch (CertificateException e) {
        throw new IOException(e);
      }
    }
  }

  private static final String X_509 = "X.509";

  private class Encoder extends Codec<CertPath>.Encoder {

    @Override
    public void apply(Writer writer, CertPath value) throws IOException {


      writer.write(BEGIN_ARRAY);
      getTypeCodec().encoder().apply(writer, TYPE_X509);

      List<? extends Certificate> certificates = value.getCertificates();
      writer.write(COMMA);
      getLengthCodec().encoder().apply(writer, certificates.size());

      writer.write(COMMA);
      writer.write(BEGIN_ARRAY);
      Character separator = null;

      for (Certificate cert : certificates) {

        if (separator != null) {
          writer.append(separator);

        } else {
          separator = COMMA;
        }

        getCertCodec().encoder().apply(writer, cert);
      }

      writer.write(END_ARRAY);
      writer.write(END_ARRAY);
    }
  }
}

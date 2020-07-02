// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.CharBuffer;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.CertificateCodec;

class CertificateCodecTest {

  Certificate certificate;
  CertificateCodec certificateCodec;
  CertificateFactory certificateFactory;
  InputStream certificateInputStream;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() throws CertificateException, FileNotFoundException {

    certificateInputStream = new FileInputStream("sample-x509-certificate.crt");
    certificateFactory = CertificateFactory.getInstance("X.509");
    certificate = certificateFactory.generateCertificate(certificateInputStream);
    certificateCodec = new CertificateCodec();
  }

  @Test
  void test_Encoder() throws IOException, CertificateEncodingException {

    certificateCodec.encoder().apply(writer, certificate);
  }

  @Test
  void test_Decoder() throws IOException {

    certificateCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}

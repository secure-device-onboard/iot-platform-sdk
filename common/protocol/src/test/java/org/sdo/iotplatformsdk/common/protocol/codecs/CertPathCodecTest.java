// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.CharBuffer;
import java.security.cert.CertPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.codecs.CertPathCodec;

class CertPathCodecTest {

  CertPath certPath;
  CertPathCodec certPathCodec;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() {

    certPath = Mockito.mock(CertPath.class);
    certPathCodec = new CertPathCodec();
  }

  @Test
  void test_Encoder() throws IOException {

    certPathCodec.encoder().apply(writer, certPath);
  }

  @Test
  void test_Decoder() throws IOException {

    certPathCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}

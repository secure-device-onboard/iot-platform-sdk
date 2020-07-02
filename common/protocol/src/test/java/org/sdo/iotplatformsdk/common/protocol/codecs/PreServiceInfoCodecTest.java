// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.CharBuffer;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.PreServiceInfoCodec;
import org.sdo.iotplatformsdk.common.protocol.types.PreServiceInfo;

class PreServiceInfoCodecTest {

  PreServiceInfo preServiceInfo;
  PreServiceInfoCodec preServiceInfoCodec;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() throws NoSuchAlgorithmException {

    preServiceInfo = new PreServiceInfo();
    preServiceInfoCodec = new PreServiceInfoCodec();
  }

  @Test
  void test_Encoder() throws IOException {

    preServiceInfoCodec.encoder().apply(writer, preServiceInfo);
  }

  @Test
  void test_Decoder() throws IOException {

    preServiceInfoCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}

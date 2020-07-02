// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.CharBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.ServiceInfoCodec;
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfoEntry;

class ServiceInfoCodecTest {

  static StringWriter writer;
  ServiceInfoCodec serviceInfoCodec;
  ServiceInfo serviceInfo;
  ServiceInfoEntry serviceInfoEntry;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() {

    serviceInfoEntry = new ServiceInfoEntry(CharBuffer.allocate(8), CharBuffer.allocate(8));
    serviceInfo = new ServiceInfo();
    serviceInfoCodec = new ServiceInfoCodec();
  }

  @Test
  void test_Encoder() throws IOException {
    serviceInfoCodec.encoder().apply(writer, serviceInfo);
  }

  @Test
  void test_Decoder() throws IOException {
    serviceInfoCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}

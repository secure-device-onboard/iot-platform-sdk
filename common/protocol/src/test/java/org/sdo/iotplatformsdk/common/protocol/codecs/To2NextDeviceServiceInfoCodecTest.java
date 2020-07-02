// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.CharBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2NextDeviceServiceInfoCodec;
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.To2NextDeviceServiceInfo;

class To2NextDeviceServiceInfoCodecTest {

  To2NextDeviceServiceInfoCodec to2NextDeviceServiceInfoCodec;
  To2NextDeviceServiceInfo to2NextDeviceServiceInfo;
  ServiceInfo serviceInfo;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() {

    serviceInfo = new ServiceInfo();
    to2NextDeviceServiceInfo = new To2NextDeviceServiceInfo(1, serviceInfo);
    to2NextDeviceServiceInfoCodec = new To2NextDeviceServiceInfoCodec();
  }

  @Test
  void test_Encoder() throws IOException {
    to2NextDeviceServiceInfoCodec.encoder().apply(writer, to2NextDeviceServiceInfo);
  }

  @Test
  void test_Decoder() throws IOException {
    to2NextDeviceServiceInfoCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}

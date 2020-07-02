// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.CharBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2GetNextDeviceServiceInfoCodec;
import org.sdo.iotplatformsdk.common.protocol.types.PreServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.To2GetNextDeviceServiceInfo;

class To2GetNextDeviceServiceInfoCodecTest {

  static StringWriter writer;
  To2GetNextDeviceServiceInfoCodec to2GetNextDeviceServiceInfoCodec;
  To2GetNextDeviceServiceInfo to2GetNextDeviceServiceInfo;
  PreServiceInfo preServiceInfo;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @SuppressWarnings("unchecked")
  @BeforeEach
  void beforeEach() {

    preServiceInfo = new PreServiceInfo();
    to2GetNextDeviceServiceInfo = new To2GetNextDeviceServiceInfo(1, preServiceInfo);
    to2GetNextDeviceServiceInfoCodec = new To2GetNextDeviceServiceInfoCodec();
  }

  @Test
  void test_Encoder() throws IOException {

    to2GetNextDeviceServiceInfoCodec.encoder().apply(writer, to2GetNextDeviceServiceInfo);
  }

  @Test
  void test_Decoder() throws IOException {

    to2GetNextDeviceServiceInfoCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}

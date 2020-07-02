// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.CharBuffer;
import java.security.SecureRandom;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2SetupDeviceNohCodec;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.RendezvousInfo;
import org.sdo.iotplatformsdk.common.protocol.types.To2SetupDeviceNoh;

class To2SetupDeviceNohCodecTest {

  To2SetupDeviceNohCodec to2SetupDeviceNohCodec;
  To2SetupDeviceNoh to2SetupDeviceNoh;
  Nonce nonce;
  SecureRandom secureRandom;
  RendezvousInfo rendezvousInfo;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() {
    secureRandom = new SecureRandom();
    nonce = new Nonce(secureRandom);
    rendezvousInfo = new RendezvousInfo();
    to2SetupDeviceNoh = new To2SetupDeviceNoh(rendezvousInfo, UUID.randomUUID(), nonce);
    to2SetupDeviceNohCodec = new To2SetupDeviceNohCodec();
  }

  @Test
  void test_Encoder() throws IOException {
    to2SetupDeviceNohCodec.encoder().apply(writer, to2SetupDeviceNoh);
  }

  @Test
  void test_Decoder() throws IOException {
    to2SetupDeviceNohCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}

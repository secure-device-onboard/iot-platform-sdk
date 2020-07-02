// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.CharBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.To1SdoRedirectCodec;
import org.sdo.iotplatformsdk.common.protocol.types.DigestType;
import org.sdo.iotplatformsdk.common.protocol.types.HashDigest;
import org.sdo.iotplatformsdk.common.protocol.types.To1SdoRedirect;

class To1SdoRedirectCodecTest {

  To1SdoRedirectCodec to1SdoRedirectCodec;
  To1SdoRedirect to1SdoRedirect;
  InetAddress inetAddress;
  HashDigest hashDigest;
  DigestType digestType;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() throws UnknownHostException {

    inetAddress = InetAddress.getByName("www.intel.com");
    hashDigest = new HashDigest();
    to1SdoRedirect = new To1SdoRedirect(inetAddress, "test", 8008, hashDigest);
    to1SdoRedirectCodec = new To1SdoRedirectCodec();
  }

  @Test
  void test_Encoder() throws IOException {
    to1SdoRedirectCodec.encoder().apply(writer, to1SdoRedirect);
  }

  @Test
  void test_Decoder() throws IOException {
    to1SdoRedirectCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}

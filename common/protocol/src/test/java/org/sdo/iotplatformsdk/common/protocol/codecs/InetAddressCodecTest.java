// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.InetAddressCodec;

class InetAddressCodecTest {

  ByteBuffer out;
  InetAddress inetAddress;
  InetAddressCodec inetAddressCodec;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() throws UnknownHostException {

    inetAddressCodec = new InetAddressCodec();
    inetAddress = InetAddress.getByName("www.intel.com");
    out = ByteBuffer.allocate(8);
  }

  @Test
  void test_Encoder() throws IOException {

    inetAddressCodec.encoder().apply(writer, inetAddress);
  }

  @Test
  void test_Decoder() throws IOException {

    inetAddressCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}

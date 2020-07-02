// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.CharBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.UriCodec;

class UriCodecTest {

  UriCodec uriCodec;
  URI uri;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() throws URISyntaxException {

    uri = new URI("www.intel.com");
    uriCodec = new UriCodec();
  }

  @Test
  void test_Encoder() throws IOException {
    uriCodec.encoder().apply(writer, uri);
  }

  @Test
  void test_Decoder() throws IOException {
    uriCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}

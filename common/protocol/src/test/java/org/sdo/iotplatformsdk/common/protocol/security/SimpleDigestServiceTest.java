// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.security.SimpleDigestService;
import org.sdo.iotplatformsdk.common.protocol.types.DigestType;


class SimpleDigestServiceTest {

  ByteBuffer[] ins;
  ReadableByteChannel[] readableByteChannel;
  SimpleDigestService simpleDigestService;

  @BeforeEach
  void beforeEach() throws NoSuchAlgorithmException {

    ins = new ByteBuffer[2];
    ins[0] = ByteBuffer.wrap("Hello World".getBytes());
    ins[1] = ByteBuffer.wrap("Test".getBytes());
    readableByteChannel = new ReadableByteChannel[2];
    readableByteChannel[0] = Channels
        .newChannel(new ByteArrayInputStream("Hello World".getBytes(StandardCharsets.UTF_8)));
    readableByteChannel[1] =
        Channels.newChannel(new ByteArrayInputStream("Test".getBytes(StandardCharsets.UTF_8)));
    simpleDigestService = new SimpleDigestService(DigestType.SHA256);

  }

  @Test
  void test_DigestOf() throws IOException, InvalidKeyException, NoSuchAlgorithmException {

    simpleDigestService.digestOf(readableByteChannel);
    simpleDigestService.digestOf(ins);
  }


}

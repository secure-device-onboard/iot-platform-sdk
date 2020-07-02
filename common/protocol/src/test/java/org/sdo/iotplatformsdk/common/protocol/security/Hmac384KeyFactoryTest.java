// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.security.Hmac384KeyFactory;

class Hmac384KeyFactoryTest {

  Hmac384KeyFactory hmac384KeyFactory;
  ByteBuffer sharedSecret;

  @BeforeEach
  void beforeEach() {

    sharedSecret = ByteBuffer.allocate(128);
    hmac384KeyFactory = new Hmac384KeyFactory(sharedSecret);
  }

  @Test
  void test_Build() throws IOException, InvalidKeyException, NoSuchAlgorithmException {

    hmac384KeyFactory.build();

  }

}

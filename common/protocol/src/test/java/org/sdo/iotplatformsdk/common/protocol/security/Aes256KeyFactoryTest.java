// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.security.Aes256KeyFactory;

class Aes256KeyFactoryTest {

  Aes256KeyFactory aes256KeyFactory;
  ByteBuffer sharedSecret;

  @BeforeEach
  void beforeEach() {

    sharedSecret = ByteBuffer.allocate(256);
    aes256KeyFactory = new Aes256KeyFactory(sharedSecret);
  }

  @Test
  void test_Build() throws IOException, InvalidKeyException, NoSuchAlgorithmException {

    aes256KeyFactory.build();
  }

}

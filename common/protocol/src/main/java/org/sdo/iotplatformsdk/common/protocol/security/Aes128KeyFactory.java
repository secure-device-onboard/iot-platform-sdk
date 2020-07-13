// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

class Aes128KeyFactory extends SessionEncryptionKeyFactory {

  Aes128KeyFactory(ByteBuffer sharedSecret) {
    super(sharedSecret);
  }

  @Override
  SecretKey build() throws NoSuchAlgorithmException, InvalidKeyException {

    KeyMaterialFactory keyMaterial1Factory = new KeyMaterialFactory(1, "HmacSHA256",
        ByteBuffer.wrap(getKdfContext()).asReadOnlyBuffer());

    byte[] keyMaterial1 = keyMaterial1Factory.build();
    return new SecretKeySpec(Arrays.copyOfRange(keyMaterial1, 0, 16), AES);
  }
}

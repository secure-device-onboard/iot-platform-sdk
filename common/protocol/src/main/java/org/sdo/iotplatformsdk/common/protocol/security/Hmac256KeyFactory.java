/*******************************************************************************
 * Copyright 2020 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package org.sdo.iotplatformsdk.common.protocol.security;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

class Hmac256KeyFactory extends SessionVerificationKeyFactory {

  private static final String HMACSHA256 = "HmacSHA256";

  Hmac256KeyFactory(ByteBuffer sharedSecret) {
    super(sharedSecret);
  }

  @Override
  SecretKey build() throws NoSuchAlgorithmException, InvalidKeyException {

    KeyMaterialFactory keyMaterial2Factory =
        new KeyMaterialFactory(2, HMACSHA256, ByteBuffer.wrap(getKdfContext()).asReadOnlyBuffer());

    byte[] keyMaterial2 = keyMaterial2Factory.build();
    return new SecretKeySpec(Arrays.copyOfRange(keyMaterial2, 0, 32), HMACSHA256);
  }
}

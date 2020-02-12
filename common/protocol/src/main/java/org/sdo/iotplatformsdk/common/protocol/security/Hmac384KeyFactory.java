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

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

class Hmac384KeyFactory extends SessionVerificationKeyFactory {

  private static final String HMACSHA384 = "HmacSHA384";

  Hmac384KeyFactory(ByteBuffer sharedSecret) {
    super(sharedSecret);
  }

  @Override
  SecretKey build() throws NoSuchAlgorithmException, InvalidKeyException {

    KeyMaterialFactory keyMaterial2aFactory =
        new KeyMaterialFactory(2, HMACSHA384, ByteBuffer.wrap(getKdfContext()).asReadOnlyBuffer());
    KeyMaterialFactory keyMaterial2bFactory =
        new KeyMaterialFactory(3, HMACSHA384, ByteBuffer.wrap(getKdfContext()).asReadOnlyBuffer());

    byte[] keyMaterial2a = keyMaterial2aFactory.build();
    byte[] keyMaterial2b = keyMaterial2bFactory.build();

    byte[] combinedKeyMaterial = new byte[64];
    ByteBuffer bbuf = ByteBuffer.wrap(combinedKeyMaterial);
    bbuf.put(keyMaterial2a, 0, 48);
    bbuf.put(keyMaterial2b, 0, 16);

    return new SecretKeySpec(combinedKeyMaterial, HMACSHA384);
  }
}

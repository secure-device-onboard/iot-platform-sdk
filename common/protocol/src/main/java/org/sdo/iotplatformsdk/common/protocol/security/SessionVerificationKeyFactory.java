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
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.SecretKey;

import org.sdo.iotplatformsdk.common.protocol.util.Buffers;

abstract class SessionVerificationKeyFactory {

  // Key-derivation context has two parts: this static text and the shared secret nonce.
  private static final byte[] CONTEXT_HEADER =
      Buffers.unwrap(StandardCharsets.US_ASCII.encode("AutomaticProvisioning-hmac"));

  private final byte[] kdfContext;

  SessionVerificationKeyFactory(ByteBuffer sharedSecret) {

    this.kdfContext =
        Arrays.copyOf(CONTEXT_HEADER, CONTEXT_HEADER.length + sharedSecret.remaining());
    sharedSecret.get(this.kdfContext, CONTEXT_HEADER.length, sharedSecret.remaining());
  }

  abstract SecretKey build() throws NoSuchAlgorithmException, InvalidKeyException;

  byte[] getKdfContext() {
    return kdfContext;
  }
}

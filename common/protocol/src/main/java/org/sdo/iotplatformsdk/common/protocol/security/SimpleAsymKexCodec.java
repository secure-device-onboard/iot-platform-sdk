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
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * A simple implementation of {@link AsymKexCodec} which uses a basic KeyPair.
 *
 * <p>This class should only be used for testing.
 */
public class SimpleAsymKexCodec implements AsymKexCodec {

  private final KeyPair keys;
  private final SecureRandom secureRandom;

  public SimpleAsymKexCodec(final KeyPair keys, final SecureRandom secureRandom) {
    this.keys = keys;
    this.secureRandom = secureRandom;
  }

  @Override
  public ByteBuffer buildEncipher(String cipherAlgorithm, ByteBuffer pt, UUID uuid) {

    final Cipher cipher;
    try {
      cipher = Cipher.getInstance(cipherAlgorithm, BouncyCastleSupplier.load());
      cipher.init(Cipher.ENCRYPT_MODE, keys.getPublic(), secureRandom);
      byte[] byteArray = new byte[pt.remaining()];
      pt.get(byteArray, 0, byteArray.length);
      byte[] data = cipher.doFinal(byteArray);
      return ByteBuffer.wrap(data);
    } catch (InvalidKeyException | IllegalBlockSizeException | NoSuchAlgorithmException
        | NoSuchPaddingException | BadPaddingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ByteBuffer buildDecipher(String cipherAlgorithm, ByteBuffer ct, UUID uuid) {

    final Cipher cipher;
    try {
      cipher = Cipher.getInstance(cipherAlgorithm, BouncyCastleSupplier.load());
      cipher.init(Cipher.DECRYPT_MODE, keys.getPrivate(), secureRandom);
      byte[] byteArray = new byte[ct.remaining()];
      ct.get(byteArray, 0, byteArray.length);
      byte[] data = cipher.doFinal(byteArray);
      return ByteBuffer.wrap(data);
    } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException
        | InvalidKeyException | NoSuchPaddingException e) {
      throw new RuntimeException(e);
    }
  }
}

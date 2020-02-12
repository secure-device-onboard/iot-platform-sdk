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

package org.sdo.iotplatformsdk.common.protocol.security.cipher;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import org.sdo.iotplatformsdk.common.protocol.rest.SdoConstants;

/**
 * Builds an {@link IvParameterSpec} for the given {@link Cipher}.
 */
public class CbcIvParameterSpec extends To2IvParameterSpec {

  public CbcIvParameterSpec(final SecureRandom secureRandom, final int cipherMode,
      final byte[] ivSeed) {
    super(secureRandom, cipherMode, ivSeed);
  }

  /**
   * Depending on the cipher mode, generates and returns {@link IvParameterSpec}. If the input IV is
   * empty, a new IV is generated.
   *
   * @return {@link IvParameterSpec}
   */
  @Override
  public IvParameterSpec build() {
    switch (getCipherMode()) {
      case Cipher.ENCRYPT_MODE:
        getSecureRandom().nextBytes(getIvSeed());
        return new IvParameterSpec(getIvSeed());

      case Cipher.DECRYPT_MODE:
        assert getIvSeed().length == SdoConstants.AES_BLOCK_SIZE;
        return new IvParameterSpec(getIvSeed());

      default:
        throw new RuntimeException("Invalid cipher operation.");
    }
  }

}

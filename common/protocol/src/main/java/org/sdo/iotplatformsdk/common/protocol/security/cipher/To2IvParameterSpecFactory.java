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

import org.sdo.iotplatformsdk.common.protocol.types.CipherBlockMode;

public final class To2IvParameterSpecFactory {

  /**
   * Build and return either{@link Ctr2IvParameterSpec} or {@link CbcIvParameterSpec} instance
   * depending on the {@link CipherBlockMode}.
   *
   * @param secureRandom {@link SecureRandom} instance
   * @param cipherBlockMode {@link CipherBlockMode} instance that decides the instance to be
   *        created.
   * @param cipherMode either encrypt (1) or decrypt (2)
   * @param ivSeed initialization vector byte array
   * @param ctrCounter CTR mode counter that is used excusively in CTR mode
   *
   * @return {@link To2IvParameterSpec} derived class instance
   */
  public static synchronized To2IvParameterSpec build(final SecureRandom secureRandom,
      final CipherBlockMode cipherBlockMode, final int cipherMode, final byte[] ivSeed,
      final long ctrCounter) {
    switch (cipherBlockMode) {
      case CTR:
        return new CtrIvParameterSpec(secureRandom, cipherMode, ivSeed, ctrCounter);
      case CBC:
        return new CbcIvParameterSpec(secureRandom, cipherMode, ivSeed);
      default:
        throw new RuntimeException("invalid cipher block mode");
    }
  }
}

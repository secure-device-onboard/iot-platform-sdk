// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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

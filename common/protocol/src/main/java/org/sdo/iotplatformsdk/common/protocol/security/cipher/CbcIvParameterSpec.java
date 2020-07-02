// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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

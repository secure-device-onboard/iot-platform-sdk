// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security.cipher;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import org.sdo.iotplatformsdk.common.protocol.rest.SdoConstants;

/**
 * Builds an {@link IvParameterSpec} for the given {@link Cipher}.
 */
public abstract class To2IvParameterSpec {

  private final SecureRandom secureRandom;
  private final int cipherMode;
  private byte[] ivSeed;

  /**
   * Constructor.
   *
   * @param secureRandom {@link SecureRandom} instance
   * @param cipherMode either encrypt (1) or decrypt (2)
   * @param ivSeed initialization vector byte array
   */
  public To2IvParameterSpec(SecureRandom secureRandom, int cipherMode, byte[] ivSeed) {
    this.secureRandom = secureRandom;
    this.cipherMode = cipherMode;
    setIvSeed(ivSeed);
  }

  /**
   * Generates and returns {@link IvParameterSpec} by initializing the IV.
   *
   * @return
   */
  public abstract IvParameterSpec build();

  protected SecureRandom getSecureRandom() {
    return secureRandom;
  }

  protected int getCipherMode() {
    return cipherMode;
  }

  protected byte[] getIvSeed() {
    return ivSeed;
  }

  protected void setIvSeed(byte[] ivSeed) {
    this.ivSeed = new byte[SdoConstants.AES_BLOCK_SIZE];
    ByteBuffer.wrap(this.ivSeed).put(ivSeed);
  }
}

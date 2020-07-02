// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security.cipher;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import org.sdo.iotplatformsdk.common.protocol.rest.SdoConstants;

/**
 * Builds an {@link IvParameterSpec} for the given {@link Cipher}.
 */
public class CtrIvParameterSpec extends To2IvParameterSpec {

  private long ctrCounter;
  private static final IntBuffer EMPTY_INT_BUFFER =
      IntBuffer.allocate(SdoConstants.AES_BLOCK_SIZE / Integer.BYTES);

  public CtrIvParameterSpec(final SecureRandom secureRandom, final int cipherMode,
      final byte[] ivSeed, final long counter) {
    super(secureRandom, cipherMode, ivSeed);
    this.ctrCounter = counter;
  }

  /**
   * Depending on the cipher mode, generates and returns {@link IvParameterSpec}. If the input IV is
   * empty, a new IV is generated.
   *
   * @return {@link IvParameterSpec} instance containing the IV to be used
   */
  @Override
  public IvParameterSpec build() {
    switch (getCipherMode()) {
      case Cipher.ENCRYPT_MODE:
        assert getIvSeed().length == SdoConstants.AES_BLOCK_SIZE;
        final IntBuffer ivBuffer = ByteBuffer.wrap(getIvSeed()).asIntBuffer();
        // compare the ivSeed with an empty 16-byte buffer. If true, then both nonce and counter
        // have to be created. If false, nonce is present, so only update the counter.
        if (ivBuffer.compareTo(EMPTY_INT_BUFFER) == 0) {
          final byte[] nonce = new byte[SdoConstants.CTR_NONCE_SIZE];
          getSecureRandom().nextBytes(nonce);
          ivBuffer.put(ByteBuffer.wrap(nonce).asIntBuffer());
          assert ivBuffer.remaining() == 1;
          ivBuffer.put(BigInteger.ZERO.intValue());
          assert !ivBuffer.hasRemaining();
          return new IvParameterSpec(getIvSeed());
        }
        // move the position to last so as to update/overwrite the 4-byte counter.
        ivBuffer.position(ivBuffer.limit() - 1);
        ivBuffer.put((int) getCtrCounter());
        assert !ivBuffer.hasRemaining();
        return new IvParameterSpec(getIvSeed());

      case Cipher.DECRYPT_MODE:
        assert getIvSeed().length == SdoConstants.AES_BLOCK_SIZE;
        return new IvParameterSpec(getIvSeed());

      default:
        throw new RuntimeException("Invalid cipher operation.");
    }
  }

  private long getCtrCounter() {
    return ctrCounter;
  }
}

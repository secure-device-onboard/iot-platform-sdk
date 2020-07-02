// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security.cipher;

import java.nio.ByteBuffer;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

/**
 * The class that performs the cipher operation, given an already initialized cipher instance. Since
 * the encipher and decipher operations are symmetrical, one method is responsible for doing both of
 * these operations.
 */
public class To2Cipher {

  private Cipher cipher;

  public To2Cipher(final Cipher cipher) {
    this.cipher = cipher;
  }

  public Cipher getCipher() {
    return this.cipher;
  }

  /**
   * Performs the encipher/decipher operation on the input {@link ByteBuffer}, depending on the
   * cipher instance.
   *
   * @param inputBuf input ByteBuffer
   * @return resultant output ByteBuffer of cipher operation
   */
  public ByteBuffer cipherOperation(final ByteBuffer inputBuf) {
    final byte[] inputBufArray = inputBuf.array();
    final byte[] outputBufArray;
    try {
      outputBufArray = getCipher().doFinal(inputBufArray);
    } catch (IllegalBlockSizeException | BadPaddingException e) {
      throw new RuntimeException(e);
    }
    return ByteBuffer.wrap(outputBufArray);
  }
}

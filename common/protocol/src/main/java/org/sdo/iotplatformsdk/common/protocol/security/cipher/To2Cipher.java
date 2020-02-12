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

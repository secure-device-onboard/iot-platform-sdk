/*******************************************************************************
 * Copyright 2020 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/

package org.sdo.iotplatformsdk.common.protocol.security.cipher;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.sdo.iotplatformsdk.common.protocol.rest.SdoConstants;
import org.sdo.iotplatformsdk.common.protocol.types.CipherBlockMode;
import org.sdo.iotplatformsdk.common.protocol.types.MessageType;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;
import org.sdo.iotplatformsdk.common.protocol.types.SdoProtocolException;

/**
 * Builds Ciphers for the specified algorithm and block mode.
 */
public class To2CipherFactory {

  private final CipherBlockMode mode;
  private final To2IvParameterSpec ivParameterSpecFactory;
  private final SecureRandom secureRandom;

  /**
   * Constructor.
   *
   * @param secureRandom {@link SecureRandom} instance
   * @param mode {@link CipherBlockMode} instance
   * @param ivParameterSpecFactory {@link IvParameterSpec} instance
   */
  public To2CipherFactory(SecureRandom secureRandom, CipherBlockMode mode,
      To2IvParameterSpec ivParameterSpecFactory) {
    this.mode = mode;
    this.ivParameterSpecFactory = ivParameterSpecFactory;
    this.secureRandom = secureRandom;
  }

  /**
   * Builds a new Cipher.
   *
   * @throws InvalidAlgorithmParameterException thrown when such an exception occurs due to the use
   *         of APIs
   * @throws InvalidKeyException thrown when such an exception occurs due to the use of APIs
   */
  public To2Cipher build(final int cipherMode, final SecretKey sessionKey)
      throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
      InvalidKeyException, InvalidAlgorithmParameterException {

    final Cipher cipher;
    final To2Cipher to2Cipher;
    switch (getMode()) {
      case CTR:
        cipher =
            Cipher.getInstance(SdoConstants.CIPHER_TRANSFORM_CTR, SdoConstants.SECURITY_PROVIDER);
        break;
      case CBC:
        cipher =
            Cipher.getInstance(SdoConstants.CIPHER_TRANSFORM_CBC, SdoConstants.SECURITY_PROVIDER);
        break;
      default:
        final SdoError sdoErr = new SdoError(SdoErrorCode.MessageRefused,
            MessageType.ERROR.intValue(), "invalid cipher algorithm");
        throw new SdoProtocolException(sdoErr);
    }
    cipher.init(cipherMode, sessionKey, getIvParameterSpecFactory().build(), getSecureRandom());
    to2Cipher = new To2Cipher(cipher);
    return to2Cipher;
  }

  public CipherBlockMode getMode() {
    return mode;
  }

  private To2IvParameterSpec getIvParameterSpecFactory() {
    return ivParameterSpecFactory;
  }

  private SecureRandom getSecureRandom() {
    return secureRandom;
  }

}

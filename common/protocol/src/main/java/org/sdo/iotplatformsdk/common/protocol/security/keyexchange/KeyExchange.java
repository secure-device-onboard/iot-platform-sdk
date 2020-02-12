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

package org.sdo.iotplatformsdk.common.protocol.security.keyexchange;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.sdo.iotplatformsdk.common.protocol.types.KeyExchangeType;

/**
 * The interface contract for SDO 'key exchange' algorithms.
 */
public interface KeyExchange {

  /**
   * Returns a shared secret given the remote's output message.
   *
   * <p>The remote's output is our input (and vice versa). Both parts are used to generate
   * the shared secret.
   */
  ByteBuffer generateSharedSecret(ByteBuffer message)
      throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException,
      NoSuchAlgorithmException, NoSuchPaddingException, ShortBufferException,
      InvalidKeySpecException, InvalidAlgorithmParameterException;

  /**
   * Returns the output message for this key exchange.
   *
   * <p>Each endpoint in the key exchange generates one output, and receives one input in order to
   * generate their shared secret.
   */
  ByteBuffer getMessage()
      throws InvalidAlgorithmParameterException, IOException, NoSuchAlgorithmException;

  /**
   * Return {@link KeyExchangeType}.
   *
   * @return The {@link KeyExchangeType} of this object.
   */
  KeyExchangeType getType();
}

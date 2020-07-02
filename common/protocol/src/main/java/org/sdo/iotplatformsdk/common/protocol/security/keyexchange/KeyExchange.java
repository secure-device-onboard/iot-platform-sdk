// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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

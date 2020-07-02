// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security;

import java.security.PublicKey;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.sdo.iotplatformsdk.common.protocol.types.DigestType;
import org.sdo.iotplatformsdk.common.protocol.types.KeyExchangeType;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;

/**
 * Implements an SDO Crypto Level, as defined in the SDO Protocol Specification.
 */
public interface CryptoLevel {

  /**
   * Return {@link DigestService}.
   *
   * @return a {@link DigestService}.
   */
  DigestService getDigestService();

  /**
   * Return {@link KeyExchangeType}.
   *
   * @return the preferred key exchange type for the given device key.
   */
  KeyExchangeType getKeyExchangeType(final PublicKey key);

  /**
   * Return {@link MacService}.
   *
   * @return a {@link MacService}.
   */
  MacService getMacService();

  /**
   * Return SEK.
   *
   * @return the Session Encryption Key (SEK) derivation function.
   */
  Function<byte[], SecretKey> getSekDerivationFunction();

  /**
   * Return SVK.
   *
   * @return the Session Verification Key (SVK) derivation function.
   */
  Function<byte[], SecretKey> getSvkDerivationFunction();

  /**
   * Return boolean representing whether the given digestType belongs to this CryptoLevel.
   *
   * @return true if the given digestType belongs to this CryptoLevel.
   */
  boolean hasType(final DigestType digestType);

  /**
   * Return boolean representing whether the given macType belongs to this CryptoLevel.
   *
   * @return true if the given macType belongs to this CryptoLevel.
   */
  boolean hasType(final MacType macType);

  /**
   * Return boolean representing whether the given keyExchangeType belongs to this CryptoLevel.
   *
   * @return true if the given keyExchangeType belongs to this CryptoLevel.
   */
  boolean hasType(final KeyExchangeType type);

  /**
   * Return the version of the protocol.
   *
   * @return the version string for this CryptoLevel.
   */
  String version();
}

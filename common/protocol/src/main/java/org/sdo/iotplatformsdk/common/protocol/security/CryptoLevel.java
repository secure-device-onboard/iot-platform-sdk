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

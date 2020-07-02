// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.ECKey;
import java.util.Set;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.sdo.iotplatformsdk.common.protocol.types.DigestType;
import org.sdo.iotplatformsdk.common.protocol.types.KeyExchangeType;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;

/**
 * Implements the SDO 1.1 crypto level.
 */
public class CryptoLevel11 implements CryptoLevel {

  private static final DigestType DIGEST_TYPE = DigestType.SHA256;
  private static final Set<KeyExchangeType> KX_TYPES =
      Set.of(KeyExchangeType.ASYMKEX, KeyExchangeType.DHKEXid14, KeyExchangeType.ECDH);
  private static final MacType MAC_TYPE = MacType.HMAC_SHA256;

  @Override
  public DigestService getDigestService() {
    return new SimpleDigestService(DIGEST_TYPE);
  }

  /**
   * Return {@link KeyExchangeType}.
   *
   * @return the preferred key exchange type for the given device key.
   */
  @Override
  public KeyExchangeType getKeyExchangeType(final PublicKey key) {
    if (key instanceof ECKey) {
      return KeyExchangeType.ECDH;
    } else {
      return KeyExchangeType.DHKEXid14;
    }
  }

  @Override
  public MacService getMacService() {
    return new SimpleMacService(MAC_TYPE);
  }

  @Override
  public Function<byte[], SecretKey> getSekDerivationFunction() {
    return in -> {
      try {
        return new Aes128KeyFactory(ByteBuffer.wrap(in)).build();

      } catch (InvalidKeyException | NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
      }
    };
  }

  @Override
  public Function<byte[], SecretKey> getSvkDerivationFunction() {
    return in -> {
      try {
        return new Hmac256KeyFactory(ByteBuffer.wrap(in)).build();

      } catch (InvalidKeyException | NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
      }
    };
  }

  @Override
  public boolean hasType(final DigestType digestType) {
    return DIGEST_TYPE == digestType;
  }

  @Override
  public boolean hasType(final MacType type) {
    return MAC_TYPE == type;
  }

  @Override
  public boolean hasType(final KeyExchangeType type) {
    return (KX_TYPES.contains(type));
  }

  @Override
  public String version() {
    return "1.1";
  }
}

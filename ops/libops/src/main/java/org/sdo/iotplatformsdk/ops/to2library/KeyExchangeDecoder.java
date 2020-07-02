// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.to2library;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.UUID;
import org.sdo.iotplatformsdk.common.protocol.security.AsymKexCodec;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.AsymmetricKeyExchange;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.DiffieHellmanKeyExchange;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.EcdhKeyExchange;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.KeyExchange;
import org.sdo.iotplatformsdk.common.protocol.types.KeyExchangeType;

public class KeyExchangeDecoder {

  private final AsymKexCodec asymKexCodec;
  private final SecureRandom secureRandom;

  public KeyExchangeDecoder(AsymKexCodec asymKexCodec, SecureRandom secureRandom) {
    this.asymKexCodec = asymKexCodec;
    this.secureRandom = secureRandom;
  }

  /**
   * Return {@link KeyExchange} corresponding to the {@link KeyExchangeType}.
   *
   * @param kxType key exchange type
   * @param uuid the device identifier
   * @return
   */
  public synchronized KeyExchange getKeyExchangeType(KeyExchangeType kxType, UUID uuid) {

    final KeyExchange keyExchange;
    switch (kxType) {

      case ASYMKEX:
        keyExchange =
            new AsymmetricKeyExchange.AsymKex2048(getAsymKexCodec(), getSecureRandom(), uuid);
        break;

      case ASYMKEX3072:
        keyExchange =
            new AsymmetricKeyExchange.AsymKex3072(getAsymKexCodec(), getSecureRandom(), uuid);
        break;

      case DHKEXid14:
        keyExchange = new DiffieHellmanKeyExchange.Group14(getSecureRandom());
        break;

      case DHKEXid15:
        keyExchange = new DiffieHellmanKeyExchange.Group15(getSecureRandom());
        break;

      case ECDH:
        keyExchange = new EcdhKeyExchange.P256(getSecureRandom());
        break;

      case ECDH384:
        keyExchange = new EcdhKeyExchange.P384(getSecureRandom());
        break;

      default:
        throw new RuntimeException("unexpected key exchange type"); // if we got here, bug.
    }
    return keyExchange;
  }

  private AsymKexCodec getAsymKexCodec() {
    return asymKexCodec;
  }

  private SecureRandom getSecureRandom() {
    return Objects.requireNonNull(secureRandom);
  }
}

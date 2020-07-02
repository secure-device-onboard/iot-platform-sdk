// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.to2library;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import org.sdo.iotplatformsdk.common.protocol.codecs.ByteArrayCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.CipherTypeCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.KeyExchangeTypeCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec;
import org.sdo.iotplatformsdk.common.protocol.security.CryptoLevel;
import org.sdo.iotplatformsdk.common.protocol.security.CryptoLevels;
import org.sdo.iotplatformsdk.common.protocol.security.cipher.To2CipherContext;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.AsymmetricKeyExchange;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.DiffieHellmanKeyExchange;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.EcdhKeyExchange;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.KeyExchange;
import org.sdo.iotplatformsdk.common.protocol.types.CipherType;
import org.sdo.iotplatformsdk.common.protocol.types.KeyExchangeType;
import org.sdo.iotplatformsdk.common.protocol.types.MessageType;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;
import org.sdo.iotplatformsdk.common.protocol.types.SdoProtocolException;
import org.sdo.iotplatformsdk.common.protocol.util.Buffers;
import org.sdo.iotplatformsdk.common.protocol.util.Buffers.Eraser;
import org.sdo.iotplatformsdk.common.rest.Message41Store;

public class To2CipherContextFactory {

  private final KeyExchangeDecoder keyExchangeDecoder;
  private final SecureRandom secureRandom;

  public To2CipherContextFactory(final KeyExchangeDecoder keyExchangeDecoder,
      SecureRandom secureRandom) {
    this.keyExchangeDecoder = keyExchangeDecoder;
    this.secureRandom = secureRandom;
  }

  /**
   * Build and return the {@link To2CipherContext} instance using the information from
   * {@link Message41Store}.
   *
   * @param message41Store {@link Message41Store} instance.
   * @param xb             the 'b' parameter that is used to generate session keys.
   * @return               {@link To2CipherContext} instance
   */
  public To2CipherContext build(final Message41Store message41Store, final ByteBuffer xb)
      throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
      NoSuchAlgorithmException, NoSuchPaddingException, ShortBufferException,
      InvalidKeySpecException, InvalidAlgorithmParameterException, IOException,
      NoSuchProviderException {

    final OwnershipProxy proxy = new OwnershipProxyCodec.OwnershipProxyDecoder()
        .decode(CharBuffer.wrap(message41Store.getOwnershipProxy()));
    if (null == proxy) {
      throw new SdoProtocolException(new SdoError(SdoErrorCode.MessageRefused,
          MessageType.ERROR.intValue(), "OwnershipVoucher must not be null"));
    }

    final KeyExchangeType kxType =
        new KeyExchangeTypeCodec().decoder().apply(CharBuffer.wrap(message41Store.getKx()));
    final KeyExchange kx = getKeyExchangeDecoder().getKeyExchangeType(kxType, proxy.getOh().getG());

    if (kx instanceof EcdhKeyExchange) {
      ((EcdhKeyExchange) kx).restoreKeys(message41Store.getKxEcdhPublicKey(),
          message41Store.getKxEcdhPrivateKey(), message41Store.getKxEcdhRandom());
    } else if (kx instanceof DiffieHellmanKeyExchange) {
      ((DiffieHellmanKeyExchange) kx).restoreKeys(message41Store.getKxDhPublicKey(),
          message41Store.getKxDhPrivateKey());
    } else if (kx instanceof AsymmetricKeyExchange) {
      final ByteBuffer xa =
          new ByteArrayCodec().decoder().apply(CharBuffer.wrap(message41Store.getAsymRandom()));
      ((AsymmetricKeyExchange) kx).setA(xa);
    }

    final CipherType cs =
        new CipherTypeCodec().decoder().apply(CharBuffer.wrap(message41Store.getCs()));

    try (Eraser eraser = new Eraser(kx.generateSharedSecret(xb))) {
      // Which SEK/SVK derivation we use depends on the crypto level the device is using,
      // and therefore on its crypto level. We can deduce this by looking at KX.
      CryptoLevel cryptoLevel = null;
      for (CryptoLevel cl : CryptoLevels.all()) {
        if (cl.hasType(kx.getType())) {
          cryptoLevel = cl;
          break;
        }
      }
      if (null == cryptoLevel) {
        throw new RuntimeException("no crypto level match for KX " + kx.getType());
      }
      final SecretKey sek =
          cryptoLevel.getSekDerivationFunction().apply(Buffers.unwrap(eraser.getBuf()));
      final SecretKey svk =
          cryptoLevel.getSvkDerivationFunction().apply(Buffers.unwrap(eraser.getBuf()));

      final To2CipherContext to2CipherContext =
          new To2CipherContext(getSecureRandom(), cs.getMode(), cs.getMacType(), sek, svk);
      return to2CipherContext;
    } catch (Exception e) {
      final SdoError sdoErr =
          new SdoError(SdoErrorCode.MessageRefused, MessageType.ERROR.intValue(), e.getMessage());
      throw new SdoProtocolException(sdoErr);
    } finally {
      xb.flip();
    }
  }

  private KeyExchangeDecoder getKeyExchangeDecoder() {
    return this.keyExchangeDecoder;
  }

  private SecureRandom getSecureRandom() {
    return this.secureRandom;
  }
}

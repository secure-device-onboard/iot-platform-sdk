// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security.keyexchange;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;
import org.sdo.iotplatformsdk.common.protocol.security.AsymKexCodec;
import org.sdo.iotplatformsdk.common.protocol.types.KeyExchangeType;

/**
 * RSA asymmetric key exchange (ASYMKEX).
 */
public abstract class AsymmetricKeyExchange implements KeyExchange {

  private static final String CIPHER = "RSA/NONE/OAEPWithSHA256AndMGF1Padding";
  private final AsymKexCodec cipherFactory;
  private final KeyExchangeType kxType;
  private byte[] myA = new byte[0];
  private byte[] myB = new byte[0];
  private final UUID uuid;

  private AsymmetricKeyExchange(final KeyExchangeType kxType, final AsymKexCodec cipherFactory,
      final UUID uuid) {

    this.cipherFactory = cipherFactory;
    this.kxType = kxType;
    this.uuid = uuid;
  }

  @Override
  public ByteBuffer getMessage() {
    return ByteBuffer.wrap(getXa());
  }

  @Override
  public ByteBuffer generateSharedSecret(ByteBuffer message) {
    setXb(message);
    return ByteBuffer.wrap(generateShSe());
  }

  @Override
  public KeyExchangeType getType() {
    return kxType;
  }

  private byte[] generateShSe() {

    final byte[] a = getA();
    final byte[] b = getB();
    final byte[] shSe = new byte[a.length + b.length];

    final ByteBuffer buf = ByteBuffer.wrap(shSe);
    buf.put(b);
    buf.put(a);

    return shSe;
  }

  private byte[] getA() {
    return Arrays.copyOf(this.myA, this.myA.length);
  }

  public void setA(ByteBuffer a) {
    this.myA = new byte[a.remaining()];
    a.get(this.myA);
  }

  // xA is A, untranslated.
  public byte[] getXa() {
    return getA();
  }

  private UUID getUuid() {
    return uuid;
  }

  // xB is B, enciphered with the owner's key.
  public void setXb(ByteBuffer xb) {
    final ByteBuffer buf = cipherFactory.buildDecipher(xb, getUuid());
    setB(buf);
  }

  private byte[] getB() {
    return Arrays.copyOf(this.myB, this.myB.length);
  }

  void setB(ByteBuffer b) {
    this.myB = new byte[b.remaining()];
    b.get(this.myB);
  }

  public static class AsymKex2048 extends AsymmetricKeyExchange {

    private static final int SECRET_SIZE = 256 / 8;

    /**
     * Constructor.
     *
     * @param cipherFactory {@link AsymKexCodec} instance
     * @param secureRandom {@link SecureRandom} instance
     * @param uuid {@link UUID} device identifier
     */
    public AsymKex2048(final AsymKexCodec cipherFactory, final SecureRandom secureRandom,
        final UUID uuid) {

      super(KeyExchangeType.ASYMKEX, cipherFactory, uuid);

      final byte[] a = new byte[SECRET_SIZE];
      secureRandom.nextBytes(a);
      setA(ByteBuffer.wrap(a));
    }
  }

  public static class AsymKex3072 extends AsymmetricKeyExchange implements KeyExchange {

    private static final int SECRET_SIZE = 768 / 8;

    /**
     * Constructor.
     *
     * @param cipherFactory {@link AsymKexCodec} instance
     * @param secureRandom {@link SecureRandom} instance
     * @param uuid {@link UUID} device identifier
     */
    public AsymKex3072(final AsymKexCodec cipherFactory, final SecureRandom secureRandom,
        final UUID uuid) {

      super(KeyExchangeType.ASYMKEX3072, cipherFactory, uuid);

      final byte[] a = new byte[SECRET_SIZE];
      secureRandom.nextBytes(a);
      setA(ByteBuffer.wrap(a));
    }
  }
}

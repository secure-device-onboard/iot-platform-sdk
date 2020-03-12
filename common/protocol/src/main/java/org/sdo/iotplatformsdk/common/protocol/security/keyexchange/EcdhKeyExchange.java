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

package org.sdo.iotplatformsdk.common.protocol.security.keyexchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.crypto.KeyAgreement;

import org.sdo.iotplatformsdk.common.protocol.types.KeyExchangeType;
import org.sdo.iotplatformsdk.common.protocol.types.Keys;
import org.sdo.iotplatformsdk.common.protocol.util.BigIntegers;

/**
 * Implement the ECDH key exchange, per SDO protocol spec 1.12m 2.5.5.3.
 */
public abstract class EcdhKeyExchange implements KeyExchange {

  // From protocol spec 1.12m, 2.5.5.3
  //
  // The Device and Owner each choose random numbers (Owner: a, Device: b)
  // and encode these numbers into exchanged parameters
  // A = (G x , G y )*a mod p, and
  // B = (G x , G y )*b mod p.
  // A and B are points, and have components (A x , A y ) and (B x , B y ),
  // respectively, with bit lengths same as (G x , G y ).
  //
  // PROGRAMMER'S NOTE:
  //
  // The above describes the generation of ECDSA key pairs,
  // with 'a' & 'b' corresponding to private keys (commonly called 'd')
  // and 'A' & 'B' corresponding to public keys
  // (commonly called 'Q', or 'W' by the JCE javadoc).
  //
  // The Device and Owner each choose a random number (as per table above),
  // to be supplied with their public keys, respectively DeviceRandom, and OwnerRandom.
  //
  // PROGRAMMER'S NOTE:
  //
  // The table referred to is:
  //
  // SDO1.0 & SDO1.1 | Future Crypto
  // -----------------+------------+------------+----------
  // ECC Curve | Randoms | ECC Curve | Randoms
  // ----------------+-----------------+------------+------------+----------
  // ECDH KEX | NIST P-256 | 128 bits | NIST P-384 | 384 bits
  //
  //
  // The Owner sends
  // ByteArray[blen(A x ), A x , blen(A y ), A y , blen(OwnerRandom), OwnerRandom]
  // to the Device as parameter TO2.ProveOPHdr.bo.xA.
  //
  // The Device sends
  // ByteArray[blen(B x ), B x , blen(B y ), B y , blen(DeviceRandom),DeviceRandom]
  // to the Owner as parameter TO2.ProveDevice.bo.xB.
  //
  // The Owner computes shared secret
  // Sh = (B*a mod p), with components (Sh x , Sh y ).
  //
  // The Device computes shared secret
  // Sh = (A*b mod p), with components (Sh x , Sh y ).
  //
  // PROGRAMMER'S NOTE:
  //
  // This text describes a standard ECDH key exchange, with 'a' substituting for the
  // traditional d(a), 'A' for the traditional Q(a), and so on.
  //
  // The shared secret ShSe is formed as:
  // Sh x ||DeviceRandom||OwnerRandom
  // (Note that Sh y is not used to construct ShSe).

  private static final String ECDH = "ECDH";
  private static final String ECDSA = "EC";
  private final String curve;
  private final int kexRandomSize;
  private final SecureRandom secureRandom;
  private final KeyExchangeType type;
  private KeyPair myKeyPair;
  private byte[] myRandom;

  private EcdhKeyExchange(KeyExchangeType type, String curve, int kexRandomSize,
      SecureRandom secureRandom) {

    this.type = type;
    this.curve = curve;
    this.kexRandomSize = kexRandomSize;
    this.secureRandom = secureRandom;
  }

  // Check if the blen() headers add up correctly.
  private boolean verifyBlen(final ByteBuffer buf) {

    while (buf.hasRemaining()) {

      final int blen;
      try {
        blen = blenDecode(buf);

      } catch (BufferUnderflowException ignored) {
        return false;
      }

      if (0 <= blen && blen <= buf.remaining()) {
        buf.position(buf.position() + blen);

      } else {
        return false;

      }
    }

    return true;
  }

  @Override
  public ByteBuffer generateSharedSecret(ByteBuffer message)
      throws InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException,
      NoSuchAlgorithmException {

    if (null == getMyKeyPair() || myRandom.length == 0) {
      throw new RuntimeException("Missing state information for ECDH key exchange.");
    }
    final List<byte[]> values = new ArrayList<>();

    if (!verifyBlen(message.duplicate())) {
      throw new IllegalArgumentException("blen() does not fit buffer");
    }

    while (message.hasRemaining()) {
      final byte[] bytes = new byte[blenDecode(message)];
      message.get(bytes);
      values.add(bytes);
    }

    final byte[] theirX = values.remove(0);
    final byte[] theirY = values.remove(0);
    final byte[] theirRandom = values.remove(0);

    final ECPrivateKey myPrivateKey = (ECPrivateKey) getMyKeyPair().getPrivate();
    final ECPoint w = new ECPoint(new BigInteger(1, theirX), new BigInteger(1, theirY));
    final ECPublicKeySpec keySpec = new ECPublicKeySpec(w, myPrivateKey.getParams());

    final KeyFactory keyFactory = KeyFactory.getInstance(ECDSA);
    final ECPublicKey theirPublicKey = (ECPublicKey) keyFactory.generatePublic(keySpec);

    final KeyAgreement keyAgreement = KeyAgreement.getInstance(ECDH);
    keyAgreement.init(myPrivateKey);
    keyAgreement.doPhase(theirPublicKey, true);
    final byte[] secret = keyAgreement.generateSecret();

    // Assemble the final shared secret (ShSe).
    final ByteBuffer shSe =
        ByteBuffer.allocate(secret.length + getMyRandom().length + theirRandom.length);

    shSe.put(secret);
    shSe.put(theirRandom);
    shSe.put(getMyRandom());
    shSe.flip();

    return shSe;
  }

  @Override
  public ByteBuffer getMessage()
      throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, IOException {

    if (null == getMyKeyPair() || myRandom.length == 0) {
      init();
    }
    final ECPublicKey myPublicKey = (ECPublicKey) getMyKeyPair().getPublic();
    final int keySize = Keys.sizeInBytes(myPublicKey);

    // ...ByteArray[blen(A x ), A x , blen(A y ), A y , blen(random), random]...
    final List<byte[]> elements =
        Arrays.asList(BigIntegers.toByteArray(myPublicKey.getW().getAffineX(), keySize),
            BigIntegers.toByteArray(myPublicKey.getW().getAffineY(), keySize), getMyRandom());

    final ByteArrayOutputStream outBytes = new ByteArrayOutputStream();

    try (WritableByteChannel outChannel = Channels.newChannel(outBytes)) {

      for (byte[] element : elements) {
        outChannel.write(ByteBuffer.wrap(blenEncode(element.length)));
        outChannel.write(ByteBuffer.wrap(element));
      }
    }

    return ByteBuffer.wrap(outBytes.toByteArray());
  }

  @Override
  public KeyExchangeType getType() {
    return type;
  }

  public SecureRandom getSecureRandom() {
    return secureRandom;
  }

  private String getCurve() {
    return curve;
  }

  private int getKexRandomSize() {
    return kexRandomSize;
  }

  // Initialize the key exchange.
  //
  // Installs JCE providers and builds artifacts needed for the key exchange.
  // This is a heavy lift, so it's done as lazily as possible.
  private void init() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {

    final KeyPairGenerator gen;
    gen = KeyPairGenerator.getInstance(ECDSA);
    gen.initialize(new ECGenParameterSpec(getCurve()), getSecureRandom());

    final KeyPair myKeyPair = gen.generateKeyPair();

    final byte[] myRandom = new byte[getKexRandomSize()];
    getSecureRandom().nextBytes(myRandom);

    setMyKeyPair(myKeyPair);
    setMyRandom(myRandom);
  }

  /**
   * Restore the key-pair that is used for the ECDH key exchange.
   *
   * @param ecdhPublicKeyAsString public key as string
   * @param ecdhPrivateKeyAsString private key as string
   * @param ecdhRandomAsString random as string
   *
   * @throws InvalidKeySpecException thrown when such an exception occurs
   * @throws NoSuchAlgorithmException thrown when such an exception occurs
   */
  public void restoreKeys(final String ecdhPublicKeyAsString, final String ecdhPrivateKeyAsString,
      final String ecdhRandomAsString) throws InvalidKeySpecException, NoSuchAlgorithmException {

    final KeyFactory keyFactory = KeyFactory.getInstance(ECDSA);
    final X509EncodedKeySpec publicKeySpec =
        new X509EncodedKeySpec(Base64.getDecoder().decode(ecdhPublicKeyAsString));
    final PKCS8EncodedKeySpec privateKeySpec =
        new PKCS8EncodedKeySpec(Base64.getDecoder().decode(ecdhPrivateKeyAsString));
    final PublicKey ecdhPublicKey = keyFactory.generatePublic(publicKeySpec);
    final PrivateKey ecdhPrivateKey = keyFactory.generatePrivate(privateKeySpec);
    final byte[] ecdhRandom = Base64.getDecoder().decode(ecdhRandomAsString);
    final KeyPair keyPair = new KeyPair(ecdhPublicKey, ecdhPrivateKey);
    setMyKeyPair(keyPair);
    setMyRandom(ecdhRandom);
  }

  public static class P256 extends EcdhKeyExchange {

    private static final String CURVE = "secp256r1";
    private static final int RANDOM_BYTES = 128 / 8;
    private static final KeyExchangeType TYPE = KeyExchangeType.ECDH;

    public P256(SecureRandom secureRandom) {
      super(TYPE, CURVE, RANDOM_BYTES, secureRandom);
    }

  }

  public static class P384 extends EcdhKeyExchange {
    private static final String CURVE = "secp384r1";
    private static final int RANDOM_BYTES = 384 / 8;
    private static final KeyExchangeType TYPE = KeyExchangeType.ECDH384;

    public P384(SecureRandom secureRandom) {
      super(TYPE, CURVE, RANDOM_BYTES, secureRandom);
    }
  }

  private byte[] blenEncode(int len) {
    byte[] blen = new byte[length()];
    ByteBuffer.wrap(blen).asShortBuffer().put((short) (len & 0xffff));
    return blen;
  }

  private int blenDecode(ByteBuffer buf) {
    byte[] blen = new byte[length()];
    buf.get(blen);
    return ByteBuffer.wrap(blen).asShortBuffer().get();
  }

  private int length() {
    // Per SDO 1.12 2.5.5.3 + CR038, blen encodes in two bytes.
    return 2;
  }

  private KeyPair getMyKeyPair() {
    return myKeyPair;
  }

  void setMyKeyPair(KeyPair myKeyPair) {
    this.myKeyPair = myKeyPair;
  }

  private byte[] getMyRandom() {
    return myRandom;
  }

  void setMyRandom(byte[] random) {
    this.myRandom = Arrays.copyOf(random, random.length);
  }

  public String getMyPublicKey() {
    return Base64.getEncoder().encodeToString(getMyKeyPair().getPublic().getEncoded());
  }

  public String getMyPrivateKey() {
    return Base64.getEncoder().encodeToString(getMyKeyPair().getPrivate().getEncoded());
  }

  public String getMyRandomAsString() {
    return Base64.getEncoder().encodeToString(getMyRandom());
  }

}

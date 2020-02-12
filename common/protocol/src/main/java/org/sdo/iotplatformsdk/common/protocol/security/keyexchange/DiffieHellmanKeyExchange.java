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

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;

import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.crypto.params.DHParameters;
import org.sdo.iotplatformsdk.common.protocol.types.KeyExchangeType;
import org.sdo.iotplatformsdk.common.protocol.util.BigIntegers;
import org.sdo.iotplatformsdk.common.protocol.util.Buffers;

public abstract class DiffieHellmanKeyExchange implements KeyExchange {

  /**
   * Performs Diffie-Hellman exchange using RFC 3526 MODP group 14.
   */
  public static class Group14 extends DiffieHellmanKeyExchange {

    public Group14(SecureRandom secureRandom) {
      super(DHStandardGroups.rfc3526_2048, secureRandom);
    }

    @Override
    public KeyExchangeType getType() {
      return KeyExchangeType.DHKEXid14;
    }
  }

  /**
   * Performs Diffie-Hellman exchange using RFC 3526 MODP group 15.
   */
  public static class Group15 extends DiffieHellmanKeyExchange {

    public Group15(SecureRandom secureRandom) {
      super(DHStandardGroups.rfc3526_3072, secureRandom);
    }

    @Override
    public KeyExchangeType getType() {
      return KeyExchangeType.DHKEXid15;
    }
  }

  private static final String DIFFIE_HELLMAN = "DiffieHellman";
  private KeyPair keys;
  private DHParameterSpec parameters;
  private SecureRandom secureRandom;

  private DiffieHellmanKeyExchange(DHParameterSpec parameters, SecureRandom secureRandom) {
    this.setParameters(parameters);
    this.setSecureRandom(secureRandom);
  }

  private DiffieHellmanKeyExchange(DHParameters parameters, SecureRandom secureRandom) {
    this(new DHParameterSpec(parameters.getP(), parameters.getG()), secureRandom);
  }

  @Override
  public ByteBuffer generateSharedSecret(ByteBuffer messageBytes)
      throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
    if (null == getKeys()) {
      throw new RuntimeException("Missing state information for DH key exchange.");
    }
    try {
      final KeyAgreement keyAgreement = KeyAgreement.getInstance(DIFFIE_HELLMAN);
      keyAgreement.init(Objects.requireNonNull(getKeys()).getPrivate(), getParameters(),
          getSecureRandom());

      final KeyFactory keyFactory = KeyFactory.getInstance(DIFFIE_HELLMAN);
      final KeySpec keySpec = new DHPublicKeySpec(new BigInteger(1, Buffers.unwrap(messageBytes)),
          getParameters().getP(), getParameters().getG());
      final Key theirs = keyFactory.generatePublic(keySpec);
      keyAgreement.doPhase(theirs, true);
      final byte[] shared = keyAgreement.generateSecret();
      return ByteBuffer.wrap(shared);

    } catch (GeneralSecurityException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ByteBuffer getMessage()
      throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
    final DHPublicKey key;
    if (null == getKeys()) {
      init();
    }
    try {
      key = (DHPublicKey) Objects.requireNonNull(getKeys()).getPublic();

    } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }

    return ByteBuffer
        .wrap(BigIntegers.toByteArray(key.getY(), getParameters().getP().bitLength() / 8));
  }

  public SecureRandom getSecureRandom() {
    return secureRandom;
  }

  public void setSecureRandom(SecureRandom secureRandom) {
    this.secureRandom = Objects.requireNonNull(secureRandom);
  }

  private KeyPair getKeys() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
    return keys;
  }

  public void setKeys(KeyPair keys) {
    this.keys = keys;
  }

  private DHParameterSpec getParameters() {
    return parameters;
  }

  private void setParameters(DHParameterSpec parameters) {
    this.parameters = Objects.requireNonNull(parameters);
  }

  private void init() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
    final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(DIFFIE_HELLMAN);
    keyPairGenerator.initialize(getParameters(), getSecureRandom());
    final KeyPair keyPair = keyPairGenerator.generateKeyPair();
    setKeys(keyPair);
  }

  /**
   * Restore the key-pair that is used for the Diffie-Hellman key exchange.
   *
   * @param dhPublicKeyAsString public key as string
   * @param dhPrivateKeyAsString private key as string
   *
   * @throws NoSuchAlgorithmException thrown when such an exception occurs
   * @throws InvalidKeySpecException thrown when such an exception occurs
   */
  public void restoreKeys(final String dhPublicKeyAsString, final String dhPrivateKeyAsString)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    final KeyFactory keyFactory = KeyFactory.getInstance(DIFFIE_HELLMAN);
    final X509EncodedKeySpec publicKeySpec =
        new X509EncodedKeySpec(Base64.getDecoder().decode(dhPublicKeyAsString));
    final PKCS8EncodedKeySpec privateKeySpec =
        new PKCS8EncodedKeySpec(Base64.getDecoder().decode(dhPrivateKeyAsString));
    final PublicKey dhPublicKey = keyFactory.generatePublic(publicKeySpec);
    final PrivateKey dhPrivateKey = keyFactory.generatePrivate(privateKeySpec);
    final KeyPair keyPair = new KeyPair(dhPublicKey, dhPrivateKey);
    setKeys(keyPair);
  }

  /**
   * Return the public key used for Diffie-Hellman key-exchange as string.
   *
   * @return the public key
   */
  public String getPublicKey() {
    try {
      return Base64.getEncoder().encodeToString(getKeys().getPublic().getEncoded());
    } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
      return null;
    }
  }

  /**
   * Return the private key used for Diffie-Hellman key-exchange as string.
   *
   * @return the private key
   */
  public String getPrivateKey() {
    try {
      return Base64.getEncoder().encodeToString(getKeys().getPrivate().getEncoded());
    } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
      return null;
    }
  }
}

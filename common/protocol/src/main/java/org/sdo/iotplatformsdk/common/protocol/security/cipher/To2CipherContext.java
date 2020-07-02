// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security.cipher;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import org.bouncycastle.util.Arrays;
import org.sdo.iotplatformsdk.common.protocol.codecs.CipherTextCodec;
import org.sdo.iotplatformsdk.common.protocol.rest.SdoConstants;
import org.sdo.iotplatformsdk.common.protocol.types.CipherBlockMode;
import org.sdo.iotplatformsdk.common.protocol.types.CipherText;
import org.sdo.iotplatformsdk.common.protocol.types.EncryptedMessage;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;
import org.sdo.iotplatformsdk.common.protocol.types.To2CipherHashMac;

/**
 * Generate the needed Cipher & Mac instance to perform the cipher and mac operations, respectively,
 * during SDO TO2.
 */
public class To2CipherContext {

  private final SecureRandom secureRandom;
  private final CipherBlockMode cipherBlockMode;
  private final MacType macType;
  private final SecretKey sek;
  private final SecretKey svk;

  // CTR mode nonce and counter values. irrelevant for CBC modes.
  private byte[] ctrNonce = new byte[SdoConstants.CTR_NONCE_SIZE];
  private long ctrCounter = 0;

  /**
   * Constructor that initializes the instance with the given values.
   *
   * @param secureRandom {@link SecureRandom} instance
   * @param cipherBlockMode {@link CipherBlockMode} instance
   * @param macType {@link MacType} instance
   * @param sek {@link SecretKey} session encryption key
   * @param svk {@link SecretKey} session verification key
   */
  public To2CipherContext(final SecureRandom secureRandom, final CipherBlockMode cipherBlockMode,
      final MacType macType, final SecretKey sek, final SecretKey svk) {
    this.secureRandom = secureRandom;
    this.cipherBlockMode = cipherBlockMode;
    this.macType = macType;
    this.sek = sek;
    this.svk = svk;
  }

  /**
   * Three-part operation that prepares for and performs mac and decryption operations.
   *
   * <p>1. Perform mac on the received encrypted data and compare against the received mac.
   *
   * <p>2. Prepare the IV using the IV from input encryptedMessage.
   *
   * <p>3. Decrypt the cipher text as contained at encryptedMessage.
   *
   * @param encryptedMessage {@link EncryptedMessage} containing cipher text & hmac.
   * @return ByteBuffer containing the decrypted bytes.
   */
  public ByteBuffer read(final EncryptedMessage encryptedMessage) {
    try {
      // prepare for and verify hmac
      final byte[] actualMac = generateMac(encryptedMessage.getCt());
      final To2CipherHashMac expectedMac = encryptedMessage.getHmac();
      if (!expectedMac.getHash().equals(ByteBuffer.wrap(actualMac))) {
        throw new RuntimeException(new DigestException());
      }

      // prepare iv
      final ByteBuffer iv = encryptedMessage.getCt().getIv();
      final To2IvParameterSpec to2IvParameterSpec =
          To2IvParameterSpecFactory.build(getSecureRandom(), getCipherBlockMode(),
              Cipher.DECRYPT_MODE, iv.array(), getCtrCounter());
      final To2CipherFactory to2CipherFactory =
          new To2CipherFactory(getSecureRandom(), getCipherBlockMode(), to2IvParameterSpec);

      // decrypt
      final To2Cipher to2Cipher = to2CipherFactory.build(Cipher.DECRYPT_MODE, getSek());
      final ByteBuffer clearTextBuf = to2Cipher.cipherOperation(encryptedMessage.getCt().getCt());
      return clearTextBuf;
    } catch (IOException | InvalidKeyException | NoSuchProviderException | NoSuchPaddingException
        | InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Four-part operation that prepares for and performs encryption and mac operations.
   *
   * <p>1. Prepare IV.
   *
   * <p>2. Encrypt the clearTextBuf.
   *
   * <p>3. Perform mac on the encrypted data.
   *
   * <p>4. Store the CTR mode nonce and counter.
   *
   * @param clearTextBuf the clear text buffer data to be encrypted
   * @return {@link EncryptedMessage}
   */
  public EncryptedMessage write(final ByteBuffer clearTextBuf) {
    try {
      // prepare IV
      final To2IvParameterSpec to2IvParameterSpec =
          To2IvParameterSpecFactory.build(getSecureRandom(), getCipherBlockMode(),
              Cipher.ENCRYPT_MODE, getCtrNonce(), getCtrCounter());
      final To2CipherFactory to2CipherFactory =
          new To2CipherFactory(getSecureRandom(), getCipherBlockMode(), to2IvParameterSpec);

      // encrypt
      final To2Cipher to2Cipher = to2CipherFactory.build(Cipher.ENCRYPT_MODE, getSek());
      final int clearTextBufSize = clearTextBuf.remaining();
      final ByteBuffer cipherTextBuf = to2Cipher.cipherOperation(clearTextBuf.duplicate());
      final CipherText ct =
          new CipherText(ByteBuffer.wrap(to2Cipher.getCipher().getIV()), cipherTextBuf.duplicate());

      // prepare for and generate hmac
      final byte[] actualMac = generateMac(ct);
      final To2CipherHashMac hashMac = new To2CipherHashMac(ByteBuffer.wrap(actualMac));
      final EncryptedMessage encryptedMessage = new EncryptedMessage(ct, hashMac);

      // increment the counter if CTR mode is in use, persist the current nonce and counter
      // separately.
      if (getCipherBlockMode().equals(CipherBlockMode.CTR)) {
        setCtrCounter(getCtrCounter() + ((clearTextBufSize - 1) / SdoConstants.AES_BLOCK_SIZE) + 1);
        setCtrNonce(
            Arrays.copyOfRange(to2Cipher.getCipher().getIV(), 0, SdoConstants.CTR_NONCE_SIZE));
      }
      return encryptedMessage;
    } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException
        | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Given the {@link CipherText}, generate the mac bytes.
   *
   * @param ct cipher text
   * @return byte array
   * @throws NoSuchAlgorithmException thrown when such an exception occurs due to the use of APIs
   * @throws InvalidKeyException thrown when such an exception occurs due to the use of APIs
   * @throws IOException thrown when such an exception occurs due to the use of APIs
   * @throws NoSuchProviderException thrown when such an exception occurs due to the use of APIs
   */
  private byte[] generateMac(final CipherText ct)
      throws NoSuchAlgorithmException, InvalidKeyException, IOException, NoSuchProviderException {
    final StringWriter ctJson = new StringWriter();
    new CipherTextCodec().encoder().apply(ctJson, ct);
    final Mac mac = new To2CipherMacFactory(getMacType()).build();
    mac.init(getSvk());
    final byte[] actualMac = mac.doFinal(ctJson.toString().getBytes(StandardCharsets.US_ASCII));
    return actualMac;
  }

  private SecureRandom getSecureRandom() {
    return secureRandom;
  }

  private CipherBlockMode getCipherBlockMode() {
    return cipherBlockMode;
  }

  private MacType getMacType() {
    return macType;
  }

  private SecretKey getSek() {
    return sek;
  }

  private SecretKey getSvk() {
    return svk;
  }

  /**
   * Returns the current counter value (last 4-bytes) of the CTR mode. For CBC mode, this is always
   * 0.
   *
   * @return
   */
  public long getCtrCounter() {
    return ctrCounter;
  }

  /**
   * Returns the current nonce value (initial 12-bytes) of the CTR mode. For CBC mode, this is full
   * of 0s.
   *
   * @return
   */
  public byte[] getCtrNonce() {
    return ctrNonce;
  }

  /**
   * Sets the counter value in the IV that will be used to prepare IV for CTR mode of operation.
   *
   * @param ctrCounter the counter value.
   */
  public void setCtrCounter(long ctrCounter) {
    this.ctrCounter = ctrCounter;
  }

  /**
   * Sets the nonce value in the IV that will be used to prepare IV for CTR mode of operation.
   *
   * @param ctrNonce 12-bytes of nonce value.
   */
  public void setCtrNonce(byte[] ctrNonce) {
    assert ctrNonce.length == SdoConstants.CTR_NONCE_SIZE;
    this.ctrNonce = Arrays.copyOf(ctrNonce, SdoConstants.CTR_NONCE_SIZE);
  }
}

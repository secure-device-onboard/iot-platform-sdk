// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security.cipher;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.SecretKey;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.security.CryptoLevel11;
import org.sdo.iotplatformsdk.common.protocol.security.CryptoLevel112;
import org.sdo.iotplatformsdk.common.protocol.security.cipher.To2CipherContext;
import org.sdo.iotplatformsdk.common.protocol.types.CipherBlockMode;
import org.sdo.iotplatformsdk.common.protocol.types.CipherText;
import org.sdo.iotplatformsdk.common.protocol.types.EncryptedMessage;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;
import org.sdo.iotplatformsdk.common.protocol.types.To2CipherHashMac;

class To2CipherContextTest {

  private To2CipherContext to2CipherContext;
  private SecureRandom secureRandom;
  private CipherText cipherText;
  private To2CipherHashMac hmac;
  EncryptedMessage encryptedMessage;
  private SecretKey sek;
  private SecretKey svk;
  byte[] cipherKey;
  byte[] hmacKey;

  @BeforeEach
  void beforeEach() throws IOException {
    secureRandom = new SecureRandom();
    cipherKey = new byte[16];
    hmacKey = new byte[16];
    secureRandom.nextBytes(cipherKey);
    secureRandom.nextBytes(hmacKey);
  }

  @Test
  public void testCipherCbcCryptoLevel1() throws NoSuchAlgorithmException {
    CryptoLevel11 cryptoLevel = new CryptoLevel11();
    sek = cryptoLevel.getSekDerivationFunction().apply(cipherKey);
    svk = cryptoLevel.getSekDerivationFunction().apply(hmacKey);
    to2CipherContext =
        new To2CipherContext(secureRandom, CipherBlockMode.CBC, MacType.HMAC_SHA256, sek, svk);

    ByteBuffer clearTextBuf = StandardCharsets.US_ASCII.encode(
        "{\"key1\":\"value1\",\"key2\":\"value2\",\"key3\":\"poiuytrewqasdfghjklmnbvcxz09876"
            + "54321\"}");
    EncryptedMessage message = to2CipherContext.write(clearTextBuf);

    ByteBuffer decryptedBytes = to2CipherContext.read(message);

    assertEquals(StandardCharsets.US_ASCII.decode(clearTextBuf).toString(),
        StandardCharsets.US_ASCII.decode(decryptedBytes).toString());
  }

  @Test
  public void testCipherCtrCryptoLevel1() throws NoSuchAlgorithmException {
    CryptoLevel11 cryptoLevel = new CryptoLevel11();
    sek = cryptoLevel.getSekDerivationFunction().apply(cipherKey);
    svk = cryptoLevel.getSekDerivationFunction().apply(hmacKey);
    to2CipherContext =
        new To2CipherContext(secureRandom, CipherBlockMode.CTR, MacType.HMAC_SHA256, sek, svk);

    byte[] clearTextBuf1 = StandardCharsets.US_ASCII
        .encode("{\"key1\":\"value1\",\"key2\":\"value2\",\"key3\":\"poiuytrewqasdfghjklmnbvcxz09"
            + "87654321\"}")
        .array();

    // IV where counter starts from 0.
    EncryptedMessage message = to2CipherContext.write(ByteBuffer.wrap(clearTextBuf1));
    cipherText = new CipherText(ByteBuffer.allocate(0), ByteBuffer.wrap(clearTextBuf1));
    encryptedMessage = new EncryptedMessage(cipherText, hmac);
    ByteBuffer decryptedBytes = to2CipherContext.read(message);
    assertEquals(StandardCharsets.US_ASCII.decode(ByteBuffer.wrap(clearTextBuf1)).toString(),
        StandardCharsets.US_ASCII.decode(decryptedBytes).toString());

    byte[] nonce = new byte[12];
    long counter;
    // IV where counter will roll over.
    nonce = Hex.decode("13257d94f866abaa2bce29a9");
    counter = Long.parseLong("FFFFFFF2", 16);

    to2CipherContext.setCtrNonce(nonce);
    to2CipherContext.setCtrCounter(counter);
    message = to2CipherContext.write(ByteBuffer.wrap(clearTextBuf1));

    nonce = to2CipherContext.getCtrNonce();
    counter = to2CipherContext.getCtrCounter();

    cipherText = new CipherText(ByteBuffer.allocate(0), ByteBuffer.wrap(clearTextBuf1));
    encryptedMessage = new EncryptedMessage(cipherText, hmac);
    decryptedBytes = to2CipherContext.read(message);
    assertEquals(StandardCharsets.US_ASCII.decode(ByteBuffer.wrap(clearTextBuf1)).toString(),
        StandardCharsets.US_ASCII.decode(decryptedBytes).toString());

    // IV where counter won't roll over.
    counter = Long.parseLong("FFFFFF32", 16);
    to2CipherContext.setCtrCounter(counter);
    message = to2CipherContext.write(ByteBuffer.wrap(clearTextBuf1));

    cipherText = new CipherText(ByteBuffer.allocate(0), ByteBuffer.wrap(clearTextBuf1));
    encryptedMessage = new EncryptedMessage(cipherText, hmac);
    decryptedBytes = to2CipherContext.read(message);
    assertEquals(StandardCharsets.US_ASCII.decode(ByteBuffer.wrap(clearTextBuf1)).toString(),
        StandardCharsets.US_ASCII.decode(decryptedBytes).toString());
  }

  @Test
  public void testCipherCbcCryptoLevel112() throws NoSuchAlgorithmException {
    CryptoLevel112 cryptoLevel = new CryptoLevel112();
    sek = cryptoLevel.getSekDerivationFunction().apply(cipherKey);
    svk = cryptoLevel.getSekDerivationFunction().apply(hmacKey);
    to2CipherContext =
        new To2CipherContext(secureRandom, CipherBlockMode.CBC, MacType.HMAC_SHA384, sek, svk);

    ByteBuffer clearTextBuf = StandardCharsets.US_ASCII.encode("{\"ct\"}");
    EncryptedMessage message = to2CipherContext.write(clearTextBuf);

    ByteBuffer decryptedBytes = to2CipherContext.read(message);

    assertEquals(StandardCharsets.US_ASCII.decode(clearTextBuf).toString(),
        StandardCharsets.US_ASCII.decode(decryptedBytes).toString());
  }

  @Test
  public void testCipherCtrCryptoLevel112() throws NoSuchAlgorithmException {
    CryptoLevel112 cryptoLevel = new CryptoLevel112();
    sek = cryptoLevel.getSekDerivationFunction().apply(cipherKey);
    svk = cryptoLevel.getSekDerivationFunction().apply(hmacKey);
    to2CipherContext =
        new To2CipherContext(secureRandom, CipherBlockMode.CTR, MacType.HMAC_SHA384, sek, svk);

    byte[] clearTextBuf1 = StandardCharsets.US_ASCII
        .encode("{\"key1\":\"value1\",\"key2\":\"value2\",\"key3\":\"poiuytrewqasdfghjklmnbv"
            + "cxz0987654321\"}")
        .array();

    // IV where counter starts from 0.
    EncryptedMessage message = to2CipherContext.write(ByteBuffer.wrap(clearTextBuf1));

    cipherText = new CipherText(ByteBuffer.allocate(0), ByteBuffer.wrap(clearTextBuf1));
    encryptedMessage = new EncryptedMessage(cipherText, hmac);
    ByteBuffer decryptedBytes = to2CipherContext.read(message);
    assertEquals(StandardCharsets.US_ASCII.decode(ByteBuffer.wrap(clearTextBuf1)).toString(),
        StandardCharsets.US_ASCII.decode(decryptedBytes).toString());

    // IV where counter will roll over.
    byte[] nonce = Hex.decode("13257d94f866abaa2bce29a9");
    long counter = Long.parseLong("FFFFFFF2", 16);

    to2CipherContext.setCtrNonce(nonce);
    to2CipherContext.setCtrCounter(counter);
    message = to2CipherContext.write(ByteBuffer.wrap(clearTextBuf1));

    cipherText = new CipherText(ByteBuffer.allocate(0), ByteBuffer.wrap(clearTextBuf1));
    encryptedMessage = new EncryptedMessage(cipherText, hmac);
    decryptedBytes = to2CipherContext.read(message);
    assertEquals(StandardCharsets.US_ASCII.decode(ByteBuffer.wrap(clearTextBuf1)).toString(),
        StandardCharsets.US_ASCII.decode(decryptedBytes).toString());

    // IV where counter won't roll over.
    counter = Long.parseLong("FFFFFF32", 16);
    to2CipherContext.setCtrCounter(counter);
    message = to2CipherContext.write(ByteBuffer.wrap(clearTextBuf1));

    cipherText = new CipherText(ByteBuffer.allocate(0), ByteBuffer.wrap(clearTextBuf1));
    encryptedMessage = new EncryptedMessage(cipherText, hmac);
    decryptedBytes = to2CipherContext.read(message);
    assertEquals(StandardCharsets.US_ASCII.decode(ByteBuffer.wrap(clearTextBuf1)).toString(),
        StandardCharsets.US_ASCII.decode(decryptedBytes).toString());
  }
}

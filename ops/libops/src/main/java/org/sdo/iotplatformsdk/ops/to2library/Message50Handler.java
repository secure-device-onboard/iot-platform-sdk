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

package org.sdo.iotplatformsdk.ops.to2library;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.sdo.iotplatformsdk.common.protocol.codecs.ByteArrayCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.EncryptedMessageCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.KexParamCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2Done2Codec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2DoneCodec;
import org.sdo.iotplatformsdk.common.protocol.config.OwnershipProxyStorage;
import org.sdo.iotplatformsdk.common.protocol.rest.AuthToken;
import org.sdo.iotplatformsdk.common.protocol.security.cipher.To2CipherContext;
import org.sdo.iotplatformsdk.common.protocol.types.EncryptedMessage;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;
import org.sdo.iotplatformsdk.common.protocol.types.SdoProtocolException;
import org.sdo.iotplatformsdk.common.protocol.types.To2Done;
import org.sdo.iotplatformsdk.common.protocol.types.To2Done2;
import org.sdo.iotplatformsdk.common.rest.DeviceCryptoInfo;
import org.sdo.iotplatformsdk.common.rest.Message41Store;
import org.sdo.iotplatformsdk.common.rest.Message45Store;
import org.sdo.iotplatformsdk.common.rest.Message47Store;
import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class Message50Handler {

  private OwnerEventHandler ownerEventHandler;
  private SessionStorage sessionStorage;
  private OwnershipProxyStorage ownershipProxyStorage;
  private SecureRandom secureRandom;
  private KeyExchangeDecoder keyExchangeDecoder;
  private static final byte[] hmacValueReuse = new byte[] {(byte) 0x3d};

  public Message50Handler() {}

  /**
   * The REST endpoint for message 50, TO2.Done. Verifies the header for appropriate authentication
   * token, session ID and session object On successful authentication of header, the message is
   * decoded Then TO2.Done2 (message 51) is composed and encoded It is then sent to the device.
   */
  @PostMapping("mp/113/msg/50")
  public Callable<ResponseEntity<?>> onPostAsync(final RequestEntity<String> requestEntity) {
    return () -> onPost(requestEntity);
  }

  private ResponseEntity<?> onPost(RequestEntity<String> requestEntity)
      throws GeneralSecurityException, InvalidCipherTextException, IOException {

    getLogger().info(requestEntity.toString());

    final String authorization = requestEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    final UUID sessionId;
    final To2DeviceSessionInfo session;
    final AuthToken authToken;

    try {
      authToken = new AuthToken(authorization);
      sessionId = authToken.getUuid();
      session = getSessionStorage().load(sessionId);

    } catch (IllegalArgumentException | NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // if any instance is corrupted/absent, the session data is unavailable, so terminate the
    // connection.
    if (null != session && (!(session.getMessage41Store() instanceof Message41Store)
        || (null == session.getMessage41Store())
        || !(session.getMessage45Store() instanceof Message45Store)
        || (null == session.getMessage45Store())
        || !(session.getMessage47Store() instanceof Message47Store)
        || (null == session.getMessage47Store())
        || !(session.getDeviceCryptoInfo() instanceof DeviceCryptoInfo)
        || null == session.getDeviceCryptoInfo())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    final String requestBody = null != requestEntity.getBody() ? requestEntity.getBody() : "";

    final ByteBuffer xb =
        new KexParamCodec().decoder().apply(CharBuffer.wrap(session.getMessage45Store().getXb()));
    final To2CipherContext cipherContext =
        new To2CipherContextFactory(getKeyExchangeDecoder(), getSecureRandom())
            .build(session.getMessage41Store(), xb.duplicate());
    final EncryptedMessageCodec encryptedMessageCodec = new EncryptedMessageCodec();
    final EncryptedMessage deviceEncryptedMessage =
        encryptedMessageCodec.decoder().apply(CharBuffer.wrap(requestBody));

    final ByteBuffer decryptedBytes = cipherContext.read(deviceEncryptedMessage);
    final CharBuffer decryptedText = US_ASCII.decode(decryptedBytes);
    getLogger().info(decryptedText.toString());

    final To2Done done = new To2DoneCodec().decoder().apply(decryptedText);

    final Nonce n6 = new Nonce(CharBuffer.wrap(session.getMessage41Store().getN6()));
    if (!Objects.equals(n6, done.getN6())) {
      SdoError err = new SdoError(SdoErrorCode.MessageRefused, done.getType(), "nonce rejected");
      throw new SdoProtocolException(err);
    }

    final Nonce n7 = new Nonce(CharBuffer.wrap(session.getMessage45Store().getN7()));
    final To2Done2 done2 = new To2Done2(n7);
    StringWriter writer = new StringWriter();
    new To2Done2Codec().encoder().apply(writer, done2);
    String responseBody = writer.toString();
    getLogger().info(responseBody);

    // if the CTR nonce is null, it means that the session's IV has been lost/corrupted. For CBC, it
    // should have been all 0s, while for CTR, it should contain the current nonce.
    if (null != session.getDeviceCryptoInfo().getCtrNonce()) {
      final ByteBuffer nonce = new ByteArrayCodec().decoder()
          .apply(CharBuffer.wrap(session.getDeviceCryptoInfo().getCtrNonce()));
      cipherContext.setCtrNonce(nonce.array());
      cipherContext.setCtrCounter(session.getDeviceCryptoInfo().getCtrCounter());
    } else {
      throw new SdoProtocolException(new SdoError(SdoErrorCode.MessageRefused, done.getType(),
          "no cipher initialization vector found"));
    }
    final EncryptedMessage ownerEncryptedMessage =
        cipherContext.write(US_ASCII.encode(responseBody));
    writer = new StringWriter();
    encryptedMessageCodec.encoder().apply(writer, ownerEncryptedMessage);
    responseBody = writer.toString();

    final ResponseEntity<?> responseEntity =
        ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, authorization)
            .contentType(MediaType.APPLICATION_JSON).body(responseBody);

    final OwnershipProxy oldProxy = new OwnershipProxyCodec.OwnershipProxyDecoder()
        .decode(CharBuffer.wrap(session.getMessage41Store().getOwnershipProxy()));
    if (null == oldProxy) {
      throw new SdoProtocolException(new SdoError(SdoErrorCode.MessageRefused, done.getType(),
          "Old OwnershipVoucher must not be null"));
    }

    final OwnershipProxy newProxy = new OwnershipProxyCodec.OwnershipProxyDecoder()
        .decode(CharBuffer.wrap(session.getMessage47Store().getNewOwnershipProxy()));
    if (null == newProxy) {
      throw new SdoProtocolException(new SdoError(SdoErrorCode.MessageRefused, done.getType(),
          "New OwnershipVoucher must not be null"));
    }
    if (isDeviceReuseEnabled(done)) {
      // credential reuse protocol scenario. No change in ownership voucher.
    } else if (!isDeviceResaleEnabled(done)) {
      // if the device does not support resale, discard the received hmac.
      getOwnershipProxyStorage().store(newProxy);
    } else {
      // update the ownership voucher with the new hmac.
      newProxy.setHmac(done.getHmac());
      getOwnershipProxyStorage().store(newProxy);
    }

    getOwnerEventHandler()
        .ifPresent((handler) -> handler.call(new To2EndEvent(oldProxy, newProxy)));

    getSessionStorage().remove(sessionId); // the session is now complete

    getLogger().info(responseEntity.toString());
    return responseEntity;
  }

  private boolean isDeviceReuseEnabled(final To2Done done) {
    return (Arrays.equals(hmacValueReuse, done.getHmac().getHash().array())
        && done.getHmac().getType().equals(MacType.NONE));
  }

  private boolean isDeviceResaleEnabled(final To2Done done) {
    return done.getHmac().getHash().remaining() != ByteBuffer.allocate(0).remaining();
  }

  private Logger getLogger() {
    return LoggerFactory.getLogger(getClass());
  }

  private Optional<OwnerEventHandler> getOwnerEventHandler() {
    return Optional.ofNullable(ownerEventHandler);
  }

  @Autowired
  @SuppressWarnings("unused")
  public void setOwnerEventHandler(OwnerEventHandler ownerEventHandler) {
    this.ownerEventHandler = ownerEventHandler;
  }

  private SessionStorage getSessionStorage() {
    return Objects.requireNonNull(sessionStorage);
  }

  @Autowired
  @SuppressWarnings("unused")
  public void setSessionStorage(SessionStorage sessionStorage) {
    this.sessionStorage = sessionStorage;
  }

  public SecureRandom getSecureRandom() {
    return secureRandom;
  }

  @Autowired
  @SuppressWarnings("unused")
  public void setSecureRandom(SecureRandom secureRandom) {
    this.secureRandom = secureRandom;
  }

  @Autowired
  public void setKeyExchangeDecoder(KeyExchangeDecoder keyExchangeDecoder) {
    this.keyExchangeDecoder = keyExchangeDecoder;
  }

  private KeyExchangeDecoder getKeyExchangeDecoder() {
    return this.keyExchangeDecoder;
  }

  private OwnershipProxyStorage getOwnershipProxyStorage() {
    return Objects.requireNonNull(ownershipProxyStorage);
  }

  @Autowired
  @SuppressWarnings("unused")
  public void setOwnershipProxyStorage(OwnershipProxyStorage ownershipProxyStorage) {
    this.ownershipProxyStorage = ownershipProxyStorage;
  }
}

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
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.sdo.iotplatformsdk.common.protocol.codecs.ByteArrayCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.EncryptedMessageCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.KexParamCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2GetNextOwnerServiceInfoCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2OwnerServiceInfoCodec;
import org.sdo.iotplatformsdk.common.protocol.rest.AuthToken;
import org.sdo.iotplatformsdk.common.protocol.security.cipher.To2CipherContext;
import org.sdo.iotplatformsdk.common.protocol.types.EncryptedMessage;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;
import org.sdo.iotplatformsdk.common.protocol.types.SdoProtocolException;
import org.sdo.iotplatformsdk.common.protocol.types.To2GetNextOwnerServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.To2OwnerServiceInfo;
import org.sdo.iotplatformsdk.common.rest.DeviceCryptoInfo;
import org.sdo.iotplatformsdk.common.rest.Message41Store;
import org.sdo.iotplatformsdk.common.rest.Message45Store;
import org.sdo.iotplatformsdk.common.rest.Message47Store;
import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoModule;
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
public class Message48Handler {

  private SessionStorage sessionStorage;
  private Set<ServiceInfoModule> serviceInfoModules = new HashSet<>();
  private SecureRandom secureRandom;
  private KeyExchangeDecoder keyExchangeDecoder;

  private Logger getLogger() {
    return LoggerFactory.getLogger(getClass());
  }

  private SessionStorage getSessionStorage() {
    return Objects.requireNonNull(sessionStorage);
  }

  @Autowired
  @SuppressWarnings("unused")
  public void setSessionStorage(SessionStorage sessionStorage) {
    this.sessionStorage = sessionStorage;
  }

  private Set<ServiceInfoModule> getServiceInfoModules() {
    return Objects.requireNonNull(serviceInfoModules);
  }

  @Autowired(required = false)
  @SuppressWarnings("unused")
  public void setServiceInfoModules(Set<ServiceInfoModule> serviceInfoModules) {
    this.serviceInfoModules = new HashSet<>(serviceInfoModules);
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

  /**
   * The REST endpoint for message 48, TO2.GetNextOwnerServiceInfo. Verifies the header for
   * appropriate authentication token, session ID and session object On successful authentication of
   * header, the message is decoded Then TO2.OwnerServiceInfo (message 49) is composed and encoded
   * It is then sent to the device.
   */
  @PostMapping("mp/113/msg/48")
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

    final To2DeviceSessionInfo to2DeviceSessionInfo = new To2DeviceSessionInfo();
    final To2GetNextOwnerServiceInfo getNextOwnerServiceInfo =
        new To2GetNextOwnerServiceInfoCodec().decoder().apply(decryptedText);

    final int nn = getNextOwnerServiceInfo.getNn();
    final String marshalledServiceInfo;
    if (!getServiceInfoModules().isEmpty()) {
      try {
        final OwnershipProxy proxy = new OwnershipProxyCodec.OwnershipProxyDecoder()
            .decode(CharBuffer.wrap(session.getMessage41Store().getOwnershipProxy()));
        final OwnerServiceInfoHandler ownerServiceInfoHandler =
            new OwnerServiceInfoHandler(getServiceInfoModules(), proxy.getOh().getG());
        marshalledServiceInfo = ownerServiceInfoHandler.getNextOwnerServiceInfoEntry(nn);
        if (null == marshalledServiceInfo) {
          throw new RuntimeException();
        }
      } catch (Exception e) {
        throw new SdoProtocolException(new SdoError(SdoErrorCode.MessageRefused,
            getNextOwnerServiceInfo.getType(), "too many serviceinfo requests"));
      }
    } else {
      marshalledServiceInfo = "";
    }

    final To2OwnerServiceInfo ownerServiceInfo = new To2OwnerServiceInfo(nn, marshalledServiceInfo);
    StringWriter writer = new StringWriter();
    new To2OwnerServiceInfoCodec().encoder().apply(writer, ownerServiceInfo);
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
      throw new SdoProtocolException(new SdoError(SdoErrorCode.MessageRefused,
          getNextOwnerServiceInfo.getType(), "no cipher initialization vector found"));
    }
    final EncryptedMessage ownerEncryptedMessage =
        cipherContext.write(US_ASCII.encode(responseBody));
    writer = new StringWriter();
    encryptedMessageCodec.encoder().apply(writer, ownerEncryptedMessage);
    responseBody = writer.toString();

    final StringWriter ctrNonceWriter = new StringWriter();
    new ByteArrayCodec().encoder().apply(ctrNonceWriter,
        ByteBuffer.wrap(cipherContext.getCtrNonce()));
    final DeviceCryptoInfo deviceCryptoInfo =
        new DeviceCryptoInfo(ctrNonceWriter.toString(), cipherContext.getCtrCounter());
    to2DeviceSessionInfo.setDeviceCryptoInfo(deviceCryptoInfo);
    getSessionStorage().store(sessionId, to2DeviceSessionInfo);

    ResponseEntity<?> responseEntity =
        ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, authorization)
            .contentType(MediaType.APPLICATION_JSON).body(responseBody);

    getLogger().info(responseEntity.toString());
    return responseEntity;
  }
}

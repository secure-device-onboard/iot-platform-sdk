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
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.sdo.iotplatformsdk.common.protocol.codecs.ByteArrayCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.EncryptedMessageCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.KexParamCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2Done2Codec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2DoneCodec;
import org.sdo.iotplatformsdk.common.protocol.config.OwnershipProxyStorage;
import org.sdo.iotplatformsdk.common.protocol.security.cipher.To2CipherContext;
import org.sdo.iotplatformsdk.common.protocol.types.EncryptedMessage;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;
import org.sdo.iotplatformsdk.common.protocol.types.MessageType;
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

public class Message50Handler {

  private final OwnerEventHandler ownerEventHandler;
  private final SessionStorage sessionStorage;
  private final OwnershipProxyStorage ownershipProxyStorage;
  private final SecureRandom secureRandom;
  private final KeyExchangeDecoder keyExchangeDecoder;
  private static final byte[] hmacValueReuse = new byte[] {(byte) 0x3d};

  /**
   * Constructor.
   */
  public Message50Handler(OwnerEventHandler ownerEventHandler, SessionStorage sessionStorage,
      OwnershipProxyStorage ownershipProxyStorage, SecureRandom secureRandom,
      KeyExchangeDecoder keyExchangeDecoder) {
    this.ownerEventHandler = ownerEventHandler;
    this.sessionStorage = sessionStorage;
    this.ownershipProxyStorage = ownershipProxyStorage;
    this.secureRandom = secureRandom;
    this.keyExchangeDecoder = keyExchangeDecoder;
  }

  /**
   * Performs operations as per Type 50 (TO2.Done) for Transfer Ownership Protocol 2,
   * and returns encoded Type 51 (TO2.Done2) as response.
   *
   * @param request               String request containing Type 50. Errors out otherwise.
   * @param sessionId             Identifier for which requestBody is processed.
   * @return                      String response containing Type 51.
   * @throws SdoProtocolException {@link SdoProtocolException} when an exception is thrown.
   */
  public String onPost(final String request, final String sessionId) throws SdoProtocolException {
    try {
      if (null == request || null == sessionId) {
        throw new IOException("invalid request");
      }
      getLogger().debug("Processing input " + request + "\n for " + sessionId);
      final To2DeviceSessionInfo session;
      session = getSessionStorage().load(sessionId);
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
        throw new IOException("missing session information for " + sessionId);
      }

      final ByteBuffer xb =
          new KexParamCodec().decoder().apply(CharBuffer.wrap(session.getMessage45Store().getXb()));
      final To2CipherContext cipherContext =
          new To2CipherContextFactory(getKeyExchangeDecoder(), getSecureRandom())
              .build(session.getMessage41Store(), xb.duplicate());
      final EncryptedMessageCodec encryptedMessageCodec = new EncryptedMessageCodec();
      final EncryptedMessage deviceEncryptedMessage =
          encryptedMessageCodec.decoder().apply(CharBuffer.wrap(request));

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

      // if the CTR nonce is null, it means that the session's IV has been lost/corrupted.
      // For CBC, it should have been all 0s, while for CTR, it should contain the current nonce.
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

      getLogger().debug("Returning response: " + responseBody + "\n for " + sessionId);
      return responseBody;
    } catch (SdoProtocolException sp) {
      getLogger().debug(sp.getMessage(), sp);
      throw sp;
    } catch (Exception e) {
      getLogger().debug(e.getMessage(), e);
      throw new SdoProtocolException(new SdoError(SdoErrorCode.InternalError,
          MessageType.TO2_GET_NEXT_OWNER_SERVICE_INFO, e.getMessage()), e);
    }
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

  private SessionStorage getSessionStorage() {
    return Objects.requireNonNull(sessionStorage);
  }

  public SecureRandom getSecureRandom() {
    return secureRandom;
  }

  private KeyExchangeDecoder getKeyExchangeDecoder() {
    return this.keyExchangeDecoder;
  }

  private OwnershipProxyStorage getOwnershipProxyStorage() {
    return Objects.requireNonNull(ownershipProxyStorage);
  }
}

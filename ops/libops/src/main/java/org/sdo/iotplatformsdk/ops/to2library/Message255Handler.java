// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.to2library;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Objects;
import java.util.Optional;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.SdoErrorCodec;
import org.sdo.iotplatformsdk.common.protocol.types.MessageType;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;
import org.sdo.iotplatformsdk.common.protocol.types.SdoProtocolException;
import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The REST endpoint for message 255, Error.
 */
public class Message255Handler {

  private final SessionStorage sessionStorage;
  private final OwnerEventHandler ownerEventHandler;

  /**
   * Constructor.
   */
  public Message255Handler(final SessionStorage sessionStorage,
      final OwnerEventHandler ownerEventHandler) {
    this.sessionStorage = Objects.requireNonNull(sessionStorage);
    this.ownerEventHandler = Objects.requireNonNull(ownerEventHandler);
  }

  private SdoError parseError(final String bodyText) {

    final String nonNullBody = Objects.requireNonNullElse(bodyText, "");
    try {
      return new SdoErrorCodec().decoder().apply(CharBuffer.wrap(nonNullBody));
    } catch (Exception ignored) {
      return new SdoError(SdoErrorCode.InternalError, MessageType.ERROR.intValue(), nonNullBody);
    }
  }

  /**
   * Performs operations as per Type 255 for Transfer Ownership Protocol 2.
   *
   * @param requestBody           String request containing the error information.
   * @param sessionId             Identifier for which requestBody is processed.
   * @throws SdoProtocolException {@link SdoProtocolException} when an exception is thrown.
   */
  public void onPost(final String requestBody, final String sessionId) throws SdoProtocolException {
    try {
      if (null == requestBody) {
        throw new IOException("invalid request");
      }
      getLogger().debug("Processing input " + requestBody + "\n for " + sessionId);

      // Is this error for a known session? If we're getting an error
      // for a proxy we've not heard of, deny it.
      //
      final To2DeviceSessionInfo to2Session;

      Object o = getSessionStorage().load(sessionId);

      if (o instanceof To2DeviceSessionInfo) {
        to2Session = (To2DeviceSessionInfo) o;
        // sending an error invalidates the session
        getSessionStorage().remove(sessionId);

      } else {
        throw new RuntimeException();
      }
      final OwnershipProxy proxy = new OwnershipProxyCodec.OwnershipProxyDecoder()
          .decode(CharBuffer.wrap(to2Session.getMessage41Store().getOwnershipProxy()));
      getOwnerEventHandler()
          .ifPresent(handler -> handler.call(new To2ErrorEvent(parseError(requestBody), proxy)));

    } catch (SdoProtocolException sp) {
      getLogger().debug(sp.getMessage(), sp);
      throw sp;
    } catch (Exception e) {
      getLogger().debug(e.getMessage(), e);
      throw new SdoProtocolException(
          new SdoError(SdoErrorCode.InternalError, MessageType.TO2_HELLO_DEVICE, e.getMessage()),
          e);
    }
  }

  private Logger getLogger() {
    return LoggerFactory.getLogger(getClass());
  }

  private Optional<OwnerEventHandler> getOwnerEventHandler() {
    return Optional.ofNullable(ownerEventHandler);
  }

  private SessionStorage getSessionStorage() {
    return sessionStorage;
  }
}

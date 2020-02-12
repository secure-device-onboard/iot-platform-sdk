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

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.SdoErrorCodec;
import org.sdo.iotplatformsdk.common.protocol.rest.AuthToken;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;
import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The REST endpoint for message 255, Error.
 */
@RestController
public class Message255Handler {

  private final SessionStorage sessionStorage;
  private OwnerEventHandler ownerEventHandler = null;

  @Autowired
  public Message255Handler(final SessionStorage sessionStorage) {
    this.sessionStorage = Objects.requireNonNull(sessionStorage);
  }

  private static SdoError parseError(final String bodyText) {

    final String nonNullBody = Objects.requireNonNullElse(bodyText, "");
    try {
      return new SdoErrorCodec().decoder().apply(CharBuffer.wrap(nonNullBody));
    } catch (Exception ignored) {
      return new SdoError(SdoErrorCode.InternalError, MessageType.ERROR.intValue(), nonNullBody);
    }
  }

  /**
   * End-point method.
   *
   * @param requestEntity {@link RequestEntity} instance.
   * @return
   */
  @PostMapping("mp/113/msg/255")
  @SuppressWarnings("unused")
  public Callable<ResponseEntity<?>> onPostAsync(final RequestEntity<String> requestEntity) {
    return () -> onPost(requestEntity);
  }

  private ResponseEntity<?> onPost(final RequestEntity<String> requestEntity) {

    getLogger().info(requestEntity.toString());

    // Is this error for a known session? If we're getting an error
    // for a proxy we've not heard of, deny it.
    //
    final To2DeviceSessionInfo to2Session;
    try {
      final AuthToken authToken =
          new AuthToken(requestEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
      final UUID sessionKey = authToken.getUuid();
      Object o = getSessionStorage().load(sessionKey);

      if (o instanceof To2DeviceSessionInfo) {
        to2Session = (To2DeviceSessionInfo) o;
        // sending an error invalidates the session
        getSessionStorage().remove(sessionKey);

      } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      }

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    final OwnershipProxy proxy;
    try {
      proxy = new OwnershipProxyCodec.OwnershipProxyDecoder()
          .decode(CharBuffer.wrap(to2Session.getMessage41Store().getOwnershipProxy()));
      getOwnerEventHandler().ifPresent(
          handler -> handler.call(new To2ErrorEvent(parseError(requestEntity.getBody()), proxy)));
    } catch (IOException e) {
      // nothing can be done if there's no ownership voucher.
    }

    return ResponseEntity.ok().build();
  }

  private Logger getLogger() {
    return LoggerFactory.getLogger(getClass());
  }

  private Optional<OwnerEventHandler> getOwnerEventHandler() {
    return Optional.ofNullable(ownerEventHandler);
  }

  @Autowired
  @SuppressWarnings("unused")
  public void setOwnerEventHandler(final OwnerEventHandler ownerEventHandler) {
    this.ownerEventHandler = ownerEventHandler;
  }

  private SessionStorage getSessionStorage() {
    return sessionStorage;
  }
}

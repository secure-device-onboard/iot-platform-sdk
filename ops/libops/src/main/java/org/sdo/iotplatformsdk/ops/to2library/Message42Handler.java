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
import java.io.StringWriter;
import java.nio.CharBuffer;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.PublicKeyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.SignatureBlockCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2GetOpNextEntryCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2OpNextEntryCodec;
import org.sdo.iotplatformsdk.common.protocol.rest.AuthToken;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;
import org.sdo.iotplatformsdk.common.protocol.types.SdoProtocolException;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;
import org.sdo.iotplatformsdk.common.protocol.types.To2GetOpNextEntry;
import org.sdo.iotplatformsdk.common.protocol.types.To2OpNextEntry;
import org.sdo.iotplatformsdk.common.rest.Message41Store;
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
public class Message42Handler {

  private SessionStorage sessionStorage;

  /**
   * The REST endpoint for message 42, TO2.GetOPNextEntry. Receives message 42 from device Verifies
   * the header for appropriate authentication token, session ID and session object On successful
   * authentication of header, the message is decoded Then TO2.ProveOPHdr (message 43) is composed
   * and encoded It is then sent to the device
   */
  @PostMapping("mp/113/msg/42")
  public Callable<ResponseEntity<?>> onPostAsync(final RequestEntity<String> requestEntity) {
    return () -> onPost(requestEntity);
  }

  private ResponseEntity<?> onPost(RequestEntity<String> requestEntity) throws IOException {

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
        || (null == session.getMessage41Store()))) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    final String requestBody = null != requestEntity.getBody() ? requestEntity.getBody() : "";
    final To2GetOpNextEntry getOpNextEntry =
        new To2GetOpNextEntryCodec().decoder().apply(CharBuffer.wrap(requestBody));

    final OwnershipProxy proxy = new OwnershipProxyCodec.OwnershipProxyDecoder()
        .decode(CharBuffer.wrap(session.getMessage41Store().getOwnershipProxy()));
    if (null == proxy) {
      throw new SdoProtocolException(new SdoError(SdoErrorCode.MessageRefused,
          getOpNextEntry.getType(), "OwnershipVoucher must not be null"));
    }
    final Integer enn = getOpNextEntry.getEnn();
    final SignatureBlock eni = proxy.getEn().get(enn);

    final To2OpNextEntry opNextEntry = new To2OpNextEntry(enn, eni);
    final StringWriter writer = new StringWriter();
    final PublicKeyCodec.Encoder pkEncoder = new PublicKeyCodec.Encoder(proxy.getOh().getPe());
    final SignatureBlockCodec.Encoder sgEncoder = new SignatureBlockCodec.Encoder(pkEncoder);
    new To2OpNextEntryCodec.Encoder(sgEncoder).encode(writer, opNextEntry);
    final String responseBody = writer.toString();

    final ResponseEntity<?> responseEntity =
        ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, authorization)
            .contentType(MediaType.APPLICATION_JSON).body(responseBody);
    getLogger().info(responseEntity.toString());
    return responseEntity;
  }

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
}

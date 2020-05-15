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
import java.util.Objects;

import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.PublicKeyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.SignatureBlockCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2GetOpNextEntryCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2OpNextEntryCodec;
import org.sdo.iotplatformsdk.common.protocol.types.MessageType;
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

public class Message42Handler {

  private final SessionStorage sessionStorage;

  /**
   * Constructor.
   */
  public Message42Handler(final SessionStorage sessionStorage) {
    this.sessionStorage = sessionStorage;
  }

  /**
   * Performs operations as per Type 42 (TO2.GetOPNextEntry) for Transfer Ownership Protocol 2,
   * and returns encoded Type 43 (TO2.OPNextEntry) as response.
   *
   * @param request               String request containing Type 42. Errors out otherwise.
   * @param sessionId             Identifier for which requestBody is processed.
   * @return                      String response containing Type 43.
   * @throws SdoProtocolException {@link SdoProtocolException} when an exception is thrown.
   */
  public String onPost(final String request, final String sessionId) throws SdoProtocolException {
    try {
      if (null == request || null == sessionId) {
        throw new IOException("invalid request");
      }
      getLogger().debug("Processing input: " + request + "\n for " + sessionId);
      final To2DeviceSessionInfo session;
      session = getSessionStorage().load(sessionId);

      // if any instance is corrupted/absent, the session data is unavailable, so terminate the
      // connection.
      if (null != session && (!(session.getMessage41Store() instanceof Message41Store)
          || (null == session.getMessage41Store()))) {
        throw new IOException("missing session information for " + sessionId);
      }

      final To2GetOpNextEntry getOpNextEntry =
          new To2GetOpNextEntryCodec().decoder().apply(CharBuffer.wrap(request));

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
      getLogger().debug("Returning response: " + responseBody + "\n for " + sessionId);
      return responseBody;
    } catch (SdoProtocolException sp) {
      getLogger().debug(sp.getMessage(), sp);
      throw sp;
    } catch (Exception e) {
      getLogger().debug(e.getMessage(), e);
      throw new SdoProtocolException(new SdoError(SdoErrorCode.InternalError,
          MessageType.TO2_GET_OP_NEXT_ENTRY, e.getMessage()), e);
    }
  }

  private Logger getLogger() {
    return LoggerFactory.getLogger(getClass());
  }

  private SessionStorage getSessionStorage() {
    return Objects.requireNonNull(sessionStorage);
  }
}

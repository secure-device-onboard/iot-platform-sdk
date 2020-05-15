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

package org.sdo.iotplatformsdk.to0scheduler.to0service;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.sdo.iotplatformsdk.common.protocol.security.SignatureService;
import org.sdo.iotplatformsdk.common.protocol.security.SignatureServiceFactory;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;
import org.sdo.iotplatformsdk.common.rest.SignatureResponse;
import org.sdo.iotplatformsdk.to0scheduler.rest.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides implementation of {@link SignatureServiceFactory} to get the {@link SignatureBlock}
 * object.
 *
 */
public class To0SignatureServiceFactoryImpl implements SignatureServiceFactory {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(To0SignatureServiceFactoryImpl.class);

  private final RestClient restClient;

  public To0SignatureServiceFactoryImpl(RestClient restClient) {
    this.restClient = restClient;
  }

  /**
   * Send a request to retrieve the SingatureResponse object. Create the public key from the BASE-64
   * encoded public key. Return the SignatureBlock instance.
   */
  @Override
  public SignatureService build(final UUID... hints) {
    return new SignatureService() {

      @Override
      public Future<SignatureBlock> sign(final String data) {
        return CompletableFuture.supplyAsync(() -> {
          try {
            // if no hints provided, throw exception.
            if (hints.length <= 0) {
              throw new Exception("No uuid provided for signature operation.");
            }
            // return after doing the signature operation for a single uuid.
            // if more than one is provided, ignore the rest.
            for (final UUID uuid : hints) {
              LOGGER.info("Obtaining signature from OCS ");
              SignatureResponse signatureResponse = restClient.signatureOperation(uuid, data);
              if (null == signatureResponse) {
                throw new Exception("Unable to get signature for " + uuid.toString());
              }
              final X509EncodedKeySpec keySpec =
                  new X509EncodedKeySpec(Base64.getDecoder().decode(signatureResponse.getPk()));
              final KeyFactory keyFactory = KeyFactory.getInstance(signatureResponse.getAlg());
              final PublicKey pk = keyFactory.generatePublic(keySpec);
              final ByteBuffer sg =
                  ByteBuffer.wrap(Base64.getDecoder().decode(signatureResponse.getSg()));

              return new SignatureBlock(CharBuffer.wrap(data), pk, sg);
            }
          } catch (Exception e) {
            LOGGER.error(e.getMessage());
            LOGGER.debug(e.getMessage(), e);
            return new SignatureBlock(CharBuffer.wrap(data), null, ByteBuffer.allocate(0));
          }
          return new SignatureBlock(CharBuffer.wrap(data), null, ByteBuffer.allocate(0));
        });
      }
    };
  }
}

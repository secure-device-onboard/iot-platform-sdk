// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.opsimpl;

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
import org.sdo.iotplatformsdk.ops.rest.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides implementation of {@link SignatureServiceFactory} to get the
 * {@link SignatureBlock} object.
 *
 */
public class OpsSignatureServiceFactory implements SignatureServiceFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(OpsSignatureServiceFactory.class);

  private RestClient restClient;

  public OpsSignatureServiceFactory(RestClient restClient) {
    this.restClient = restClient;
  }

  /*
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
              LOGGER.info("Obtaining signature for " + uuid);
              final SignatureResponse signatureResponse = restClient.signatureOperation(uuid, data);
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
            return new SignatureBlock(CharBuffer.wrap(data), null, null);
          }
          return new SignatureBlock(CharBuffer.wrap(data), null, null);
        });
      }
    };
  }

}

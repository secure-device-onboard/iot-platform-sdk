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

package org.sdo.iotplatformsdk.ops.epid;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.DatatypeConverter;

import org.sdo.iotplatformsdk.common.protocol.rest.SdoRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * EpidOnlineMaterial is a class to access the cryptographic material available
 * online though the Epid Online Verification Service.
 */
public class EpidOnlineMaterial {

  private static final Logger mlog = LoggerFactory.getLogger(EpidOnlineMaterial.class);

  final ExecutorService executor;
  final Duration restTimeout = Duration.of(10, ChronoUnit.SECONDS);

  /**
   * Default constructor.
   */
  public EpidOnlineMaterial() {
    executor = Executors.newCachedThreadPool(r -> {
      Thread t = Executors.defaultThreadFactory().newThread(r);
      t.setDaemon(true);
      return t;
    });
  }

  /**
   * Reads the online REST service for the requested materials.
   * Today these materials are all unsigned.
   *
   * @param gid      - the group ID, 4 or 16 bytes in length
   * @param epidType - Which version of Epid
   * @param matId    - Which material requested
   * @return a byte array containing the requested material of zero length if failed
   */
  public byte[] readEpidRestService(byte[] gid, EpidLib.EpidVersion epidType,
      EpidLib.MaterialId matId) throws InterruptedException, IOException, TimeoutException {

    // Build the target from the passed values if they are valid
    String targetFile = "";
    switch (epidType) {
      case EPID_1_1:
        if (gid.length != 4) {
          throw new IllegalArgumentException();
        }
        targetFile += "/epid11/";
        break;

      case EPID_2_0:
        if (gid.length != 16) {
          throw new IllegalArgumentException();
        }
        targetFile += "/v2/epid20/";
        break;

      default:
        throw new RuntimeException("BUG: unexpected switch default");
    }

    String filename;
    switch (matId) {
      case SIGRL:
        filename = "sigrl";
        break;
      case PRIVRL:
        filename = "privrl";
        break;
      case PUBKEY:
        filename = "pubkey";
        break;
      case PUBKEY_CRT_BIN:
        filename = "pubkey.crt.bin";
        break;
      case PUBKEY_CRT:
        filename = "pubkey.crt";
        break;
      default:
        throw new IllegalArgumentException(
            "Incorrect material ID when reading material from Epid rest service: " + matId);
    }
    targetFile += DatatypeConverter.printHexBinary(gid) + '/' + filename;
    mlog.debug("TargetFile: " + targetFile);

    URI uri;
    try {
      uri = new URL("https", EpidSecurityProvider.getEpidOnlineHostUrl(), targetFile).toURI();
    } catch (MalformedURLException | URISyntaxException ex) {
      throw new RuntimeException(ex);
    }

    RequestEntity<String> requestEntity = RequestEntity.method(HttpMethod.GET, uri)
        .accept(MediaType.APPLICATION_OCTET_STREAM).body("");

    RestTemplate template = new SdoRestTemplate(EpidSecurityProvider.getHttpRequestFactory());
    Future<ResponseEntity<byte[]>> future =
        executor.submit(() -> template.exchange(requestEntity, byte[].class));

    final ResponseEntity<byte[]> responseEntity;
    try {
      responseEntity = future.get(restTimeout.toMillis(), TimeUnit.MILLISECONDS);

    } catch (ExecutionException e) {
      if (e.getCause() instanceof ResourceAccessException) {
        throw (ResourceAccessException) e.getCause();

      } else {
        throw new RuntimeException(e);
      }
    }

    if (responseEntity.getStatusCode().is2xxSuccessful()) {
      return responseEntity.hasBody() ? responseEntity.getBody() : new byte[0];

    } else {
      throw new IOException(responseEntity.getStatusCode().getReasonPhrase());
    }
  }
}

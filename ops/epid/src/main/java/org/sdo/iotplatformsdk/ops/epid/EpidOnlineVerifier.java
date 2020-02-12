/*******************************************************************************
 * Copyright 2020 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/

package org.sdo.iotplatformsdk.ops.epid;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;

import org.sdo.iotplatformsdk.common.protocol.rest.SdoRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class EpidOnlineVerifier {
  private static final Logger mlog = LoggerFactory.getLogger(EpidOnlineVerifier.class);

  /**
   * Verify the provided data using the Epid Online Varification service.
   *
   * @param epidVersion - The EpidVersion for the verification
   * @param gid         - the group Id
   * @param msg         - the message to verify against
   * @param signature   - the signature to verify
   * @param lib         - EpidLibRev3 context to retrieve the URi
   * @return kEpidNoErr if good, otherwise kEpidErr
   */
  public int verifyOnline(EpidLib.EpidVersion epidVersion, byte[] gid, byte[] msg, byte[] signature,
      EpidLib lib) {

    String verifierFile = "";
    switch (epidVersion) {
      case EPID_1_0:
      case EPID_1_1:
        verifierFile += "/v1/epid11/proof";
        break;

      case EPID_2_0:
        verifierFile += "/v1/epid20/proof";
        break;

      default:
        return EpidLib.EpidStatus.kEpidErr.getValue();
    }

    URI uri;
    try {
      uri = new URL("https", EpidSecurityProvider.getEpidOnlineHostUrl(), verifierFile).toURI();
    } catch (MalformedURLException | URISyntaxException ex) {
      return EpidLib.EpidStatus.kEpidBadArgErr.getValue();
    }

    // Create the encoded JSON block to send
    String blk = "{" + "\"groupId\":\"" + Base64.getEncoder().encodeToString(gid) + "\""
        + ",\"msg\":\"" + Base64.getEncoder().encodeToString(msg) + "\"" + ",\"epidSignature\":\""
        + Base64.getEncoder().encodeToString(signature) + "\"" + "}";

    RequestEntity<String> requestEntity =
        RequestEntity.post(uri).contentType(APPLICATION_JSON).body(blk);

    RestTemplate template = new SdoRestTemplate(EpidSecurityProvider.getHttpRequestFactory());
    ResponseEntity<String> responseEntity = template.exchange(requestEntity, String.class);

    // Allow the logging of the different responses from the documentation
    switch (responseEntity.getStatusCodeValue()) {
      case 200:
        mlog.info("Online Verification - Successful");
        return EpidLib.EpidStatus.kEpidNoErr.getValue();
      case 400:
        mlog.info("Online Verification - Malformed Request");
        return EpidLib.EpidStatus.kEpidErr.getValue();
      case 403:
        mlog.info("Online Verification - Invalid Signature");
        return EpidLib.EpidStatus.kEpidErr.getValue();
      case 417:
        mlog.info("Online Verification - Outdated SigRl");
        return EpidLib.EpidStatus.kEpidErr.getValue();
      default:
        mlog.info("Online Verification - Unknown Error: " + responseEntity.getStatusCodeValue());
        return EpidLib.EpidStatus.kEpidErr.getValue();
    }
  }

}

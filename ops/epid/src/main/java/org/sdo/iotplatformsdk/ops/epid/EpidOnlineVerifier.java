// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.epid;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Base64;
import org.sdo.iotplatformsdk.common.protocol.config.SecureRandomFactory;
import org.sdo.iotplatformsdk.common.protocol.config.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
      uri = new URL(EpidSecurityProvider.getEpidOnlineHostUrl()).toURI().resolve(verifierFile);
    } catch (MalformedURLException | URISyntaxException ex) {
      return EpidLib.EpidStatus.kEpidBadArgErr.getValue();
    }

    // Create the encoded JSON block to send
    String blk = "{" + "\"groupId\":\"" + Base64.getEncoder().encodeToString(gid) + "\""
        + ",\"msg\":\"" + Base64.getEncoder().encodeToString(msg) + "\"" + ",\"epidSignature\":\""
        + Base64.getEncoder().encodeToString(signature) + "\"" + "}";

    final HttpClient httpClient;
    try {
      httpClient = HttpClient.newBuilder()
          .sslContext(new SslContextFactory(new SecureRandomFactory().getObject()).getObject())
          .build();
    } catch (Exception e) {
      return EpidLib.EpidStatus.kEpidErr.getValue();
    }
    final HttpRequest.Builder httpRequestBuilder =
        HttpRequest.newBuilder().header("Content-Type", "application/json");
    final HttpRequest httpRequest =
        httpRequestBuilder.uri(uri).POST(BodyPublishers.ofString(blk)).build();

    HttpResponse<String> httpResponse;
    try {
      httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      return EpidLib.EpidStatus.kEpidErr.getValue();
    }

    // Allow the logging of the different responses from the documentation
    switch (httpResponse.statusCode()) {
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
        mlog.info("Online Verification - Unknown Error: " + httpResponse.statusCode());
        return EpidLib.EpidStatus.kEpidErr.getValue();
    }
  }

}

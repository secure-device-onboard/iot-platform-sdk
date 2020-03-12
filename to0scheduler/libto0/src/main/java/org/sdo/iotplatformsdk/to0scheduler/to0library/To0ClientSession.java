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

package org.sdo.iotplatformsdk.to0scheduler.to0library;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.nio.CharBuffer;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

import org.sdo.iotplatformsdk.common.protocol.codecs.PublicKeyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.SignatureBlockCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To0AcceptOwnerCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To0HelloAckCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To0HelloCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To0OwnerSignCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To0OwnerSignTo0dCodec.To0dEncoder;
import org.sdo.iotplatformsdk.common.protocol.codecs.To1SdoRedirectCodec;
import org.sdo.iotplatformsdk.common.protocol.config.SimpleWaitSecondsBuilder;
import org.sdo.iotplatformsdk.common.protocol.config.WaitSecondsBuilder;
import org.sdo.iotplatformsdk.common.protocol.rest.SdoRestTemplate;
import org.sdo.iotplatformsdk.common.protocol.security.CryptoLevel;
import org.sdo.iotplatformsdk.common.protocol.security.CryptoLevels;
import org.sdo.iotplatformsdk.common.protocol.security.SignatureServiceFactory;
import org.sdo.iotplatformsdk.common.protocol.types.HashDigest;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;
import org.sdo.iotplatformsdk.common.protocol.types.To0AcceptOwner;
import org.sdo.iotplatformsdk.common.protocol.types.To0Hello;
import org.sdo.iotplatformsdk.common.protocol.types.To0HelloAck;
import org.sdo.iotplatformsdk.common.protocol.types.To0OwnerSign;
import org.sdo.iotplatformsdk.common.protocol.types.To0OwnerSignTo0d;
import org.sdo.iotplatformsdk.common.protocol.types.To1SdoRedirect;
import org.sdo.iotplatformsdk.common.protocol.types.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class To0ClientSession {

  private static final Logger LOG = LoggerFactory.getLogger(To0ClientSession.class);
  private final SignatureServiceFactory signatureServiceFactory;
  private final URI to1dOwnerRedirectPath;
  private ClientHttpRequestFactory requestFactory = null;
  private WaitSecondsBuilder waitSecondsBuilder = new SimpleWaitSecondsBuilder();

  public To0ClientSession(final SignatureServiceFactory signatureServiceFactory,
      URI to1dRedirectPath) {
    this.signatureServiceFactory = signatureServiceFactory;
    this.to1dOwnerRedirectPath = to1dRedirectPath;
  }

  /**
   * Runs the TO2 operation.
   * @param proxy      {@link OwnershipProxy} instance for which TO0 is run.
   * @param uriBuilder {@link UriComponentsBuilder} instance
   * @return           the wait seconds
   */
  public Duration run(OwnershipProxy proxy, UriComponentsBuilder uriBuilder)
      throws IOException, ExecutionException, InterruptedException {

    // Which crypto level is used in this ownership voucher?
    // Our registration should use the same level.
    final CryptoLevel cryptoLevel = CryptoLevels.find(proxy.getHmac().getType())
        .orElseThrow(() -> new IllegalArgumentException("no crypto level for proxy"));

    final To0Hello hello = new To0Hello();
    StringWriter writer = new StringWriter();

    /**
     * Sends TO0Hello - message 20 compose
     */
    new To0HelloCodec().encoder().apply(writer, hello);
    String request = writer.toString();

    /**
     * Sends TO0Hello - message 20 to the server.
     */
    URI uri = uriBuilder.build(Version.VERSION_1_13, To0Hello.ID);
    RequestEntity<String> requestEntity =
        RequestEntity.post(uri).contentType(APPLICATION_JSON).body(request);
    LOG.info(requestEntity.toString());

    RestTemplate template = new SdoRestTemplate(getRequestFactory());
    ResponseEntity<String> responseEntity = template.exchange(requestEntity, String.class);
    LOG.info(responseEntity.toString());

    final String authToken = responseEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    String response = null != responseEntity.getBody() ? responseEntity.getBody() : "";

    /**
     * Decoding the response from the server Receiving message 21 from the server.
     */
    final To0HelloAck helloAck = new To0HelloAckCodec().decoder().apply(CharBuffer.wrap(response));

    /**
     * Composing To0d of message 22 To compose the message, retrieving the nonce (n3) from the
     * server response.
     */
    final To0OwnerSignTo0d to0d =
        new To0OwnerSignTo0d(proxy, getWaitSecondsBuilder().apply(proxy), helloAck.getN3());

    writer = new StringWriter();

    /**
     * Encoding To0d (in message 22 format, to0d is encoded)
     */
    new To0dEncoder().encode(writer, to0d);

    /**
     * Calculating the hash of to0d which is a part of to1d.
     */
    final HashDigest to0dh =
        cryptoLevel.getDigestService().digestOf(US_ASCII.encode(writer.toString()));

    /**
     * Composing to1d.
     */
    final To1dOwnerRedirect to1dOwnerRedirect = new To1dOwnerRedirect(to1dOwnerRedirectPath);
    final To1SdoRedirect redirect = new To1SdoRedirect(to1dOwnerRedirect.getI1(),
        to1dOwnerRedirect.getDns1(), to1dOwnerRedirect.getPort1(), to0dh);

    writer = new StringWriter();
    new To1SdoRedirectCodec().encoder().apply(writer, redirect);
    final SignatureBlock sig =
        signatureServiceFactory.build(proxy.getOh().getG()).sign(writer.toString()).get();
    // to1d.pk must be null, per protocol spec
    final SignatureBlock to1d = new SignatureBlock(sig.getBo(), null, sig.getSg());

    /**
     * Composing message 22 and encoding it.
     */
    final To0OwnerSign ownerSign = new To0OwnerSign(to0d, to1d);
    writer = new StringWriter();
    new To0OwnerSignCodec.To0OwnerSignEncoder(
        new SignatureBlockCodec.Encoder(new PublicKeyCodec.Encoder(proxy.getOh().getPe())))
            .encode(writer, ownerSign);
    request = writer.toString();

    uri = uriBuilder.build(Version.VERSION_1_13, To0OwnerSign.ID);

    /**
     * Sending request to RZ server
     */
    requestEntity = RequestEntity.post(uri).contentType(APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, authToken).body(request);
    LOG.info(requestEntity.toString());

    /**
     * Receiving message 23 from server
     */
    responseEntity = template.exchange(requestEntity, String.class);
    LOG.info(responseEntity.toString());

    response = null != responseEntity.getBody() ? responseEntity.getBody() : "";

    /**
     * Decoding message 23 The New Owner Client can drop the connection after this message is
     * processed. If the new Owner does not receive a Transfer Ownership connection from a Device
     * within waitSeconds seconds, it must repeat Transfer Ownership Protocol 0 and re-register its
     * GUID to address association.
     */
    To0AcceptOwner acceptOwner =
        new To0AcceptOwnerCodec().decoder().apply(CharBuffer.wrap(response));

    /**
     * If the new Owner does not receive a Transfer Ownership connection from a Device within
     * waitSeconds seconds, it must repeat Transfer Ownership Protocol 0 and re-register its GUID to
     * address association.
     */
    return acceptOwner.getWs();
  }

  private ClientHttpRequestFactory getRequestFactory() {
    if (null == requestFactory) {
      throw new IllegalStateException("ClientHttpRequestFactory must not be null");
    }
    return requestFactory;
  }

  @Autowired
  public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
    this.requestFactory = requestFactory;
  }

  private WaitSecondsBuilder getWaitSecondsBuilder() {
    return waitSecondsBuilder;
  }

  @Autowired
  public void setWaitSecondsBuilder(WaitSecondsBuilder waitSecondsBuilder) {
    this.waitSecondsBuilder = waitSecondsBuilder;
  }
}

// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.to0scheduler.to0library;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.CharBuffer;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ssl.SSLContext;
import org.sdo.iotplatformsdk.common.protocol.codecs.PublicKeyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.SignatureBlockCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To0AcceptOwnerCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To0HelloAckCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To0HelloCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To0OwnerSignCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To0OwnerSignTo0dCodec.To0dEncoder;
import org.sdo.iotplatformsdk.common.protocol.codecs.To1SdoRedirectCodec;
import org.sdo.iotplatformsdk.common.protocol.rest.SdoUriComponentsBuilder;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class To0ClientSession {

  private static final Logger LOG = LoggerFactory.getLogger(To0ClientSession.class);
  private final SignatureServiceFactory signatureServiceFactory;
  private final URI to1dOwnerRedirectPath;
  private final SSLContext sslContext;
  private Duration to0WaitSeconds;

  /**
   * Constructor.
   */
  public To0ClientSession(final SignatureServiceFactory signatureServiceFactory,
      URI to1dRedirectPath, SSLContext sslContext) {
    this.signatureServiceFactory = signatureServiceFactory;
    this.to1dOwnerRedirectPath = to1dRedirectPath;
    this.sslContext = sslContext;
  }

  public void setTo0WaitSeconds(Duration to0WaitSeconds) {
    this.to0WaitSeconds = to0WaitSeconds;
  }

  /**
   * Runs the TO2 operation.
   *
   * @param proxy {@link OwnershipProxy} instance for which TO0 is run.
   * @param uri   {@link UriComponentsBuilder} instance
   * @return      the wait seconds
   */
  public Duration run(OwnershipProxy proxy, URI uri)
      throws IOException, ExecutionException, InterruptedException {
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    HttpClient httpClient = HttpClient.newBuilder().sslContext(sslContext)
        .connectTimeout(Duration.ofSeconds(5)).executor(executor).build();
    try {
      // Which crypto level is used in this ownership voucher?
      // Our registration should use the same level.
      final CryptoLevel cryptoLevel = CryptoLevels.find(proxy.getHmac().getType())
          .orElseThrow(() -> new IllegalArgumentException("no crypto level for proxy"));

      final To0Hello hello = new To0Hello();
      StringWriter writer = new StringWriter();

      // Sends TO0Hello - message 20 compose
      new To0HelloCodec().encoder().apply(writer, hello);
      String request = writer.toString();

      // Sends TO0Hello - message 20 to the server.
      final HttpRequest.Builder httpRequestBuilder =
          HttpRequest.newBuilder().header("Content-Type", "application/json");
      HttpRequest httpRequest =
          httpRequestBuilder.uri(uri.resolve(SdoUriComponentsBuilder.path(To0Hello.ID)))
              .POST(BodyPublishers.ofString(request)).build();
      LOG.info("[HTTP Request]: " + httpRequest.method() + " " + httpRequest.uri());

      HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
      if (httpResponse.statusCode() != 200) {
        throw new IOException(httpResponse.toString() + " " + httpResponse.body());
      }

      String response = null != httpResponse.body() ? httpResponse.body() : "";
      LOG.info("[HTTP Response]: " + httpResponse.toString());

      final String authToken = httpResponse.headers().firstValue("Authorization").get();

      // Decoding the response from the server Receiving message 21 from the server.
      final To0HelloAck helloAck =
          new To0HelloAckCodec().decoder().apply(CharBuffer.wrap(response));

      // Composing To0d of message 22 To compose the message, retrieving the nonce (n3) from the
      // server response.
      final To0OwnerSignTo0d to0d = new To0OwnerSignTo0d(proxy, to0WaitSeconds, helloAck.getN3());

      writer = new StringWriter();

      // Encoding To0d (in message 22 format, to0d is encoded)
      new To0dEncoder().encode(writer, to0d);

      // Calculating the hash of to0d which is a part of to1d.
      final HashDigest to0dh =
          cryptoLevel.getDigestService().digestOf(US_ASCII.encode(writer.toString()));

      // Composing to1d.
      final To1dOwnerRedirect to1dOwnerRedirect = new To1dOwnerRedirect(to1dOwnerRedirectPath);
      final To1SdoRedirect redirect = new To1SdoRedirect(to1dOwnerRedirect.getI1(),
          to1dOwnerRedirect.getDns1(), to1dOwnerRedirect.getPort1(), to0dh);

      writer = new StringWriter();
      new To1SdoRedirectCodec().encoder().apply(writer, redirect);
      final SignatureBlock sig =
          signatureServiceFactory.build(proxy.getOh().getG()).sign(writer.toString()).get();
      // to1d.pk must be null, per protocol spec
      final SignatureBlock to1d = new SignatureBlock(sig.getBo(), null, sig.getSg());

      // Composing message 22 and encoding it.
      final To0OwnerSign ownerSign = new To0OwnerSign(to0d, to1d);
      writer = new StringWriter();
      new To0OwnerSignCodec.To0OwnerSignEncoder(
          new SignatureBlockCodec.Encoder(new PublicKeyCodec.Encoder(proxy.getOh().getPe())))
              .encode(writer, ownerSign);
      request = writer.toString();

      // Sending request to RZ server
      httpRequest = httpRequestBuilder.header("Authorization", authToken)
          .uri(uri.resolve(SdoUriComponentsBuilder.path(To0OwnerSign.ID)))
          .POST(BodyPublishers.ofString(request)).build();
      LOG.info(
          "[HTTP Request]: " + httpRequest.method() + " " + httpRequest.uri() + "\n" + request);

      // Receiving message 23 from server
      httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
      if (httpResponse.statusCode() != 200) {
        throw new IOException(httpResponse.toString() + " " + httpResponse.body());
      }
      response = null != httpResponse.body() ? httpResponse.body() : "";
      LOG.info("[HTTP Response]: " + httpResponse.toString());

      // Decoding message 23 The New Owner Client can drop the connection after this message is
      // processed. If the new Owner does not receive a Transfer Ownership connection from a Device
      // within waitSeconds seconds, it must repeat Transfer Ownership Protocol 0 and re-register
      // its
      // GUID to address association.
      final To0AcceptOwner acceptOwner =
          new To0AcceptOwnerCodec().decoder().apply(CharBuffer.wrap(response));

      // If the new Owner does not receive a Transfer Ownership connection from a Device within
      // waitSeconds seconds, it must repeat Transfer Ownership Protocol 0 and re-register its GUID
      // to
      // address association.
      return acceptOwner.getWs();

    } catch (Exception e) {
      throw e;
    } finally {
      // avoiding memory leaks.
      executor.shutdownNow();
      httpClient = null;
    }
  }
}

// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.to0scheduler.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ssl.SSLContext;
import org.sdo.iotplatformsdk.common.rest.DeviceState;
import org.sdo.iotplatformsdk.common.rest.SignatureResponse;
import org.sdo.iotplatformsdk.to0scheduler.to0service.To0PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * This class is responsible for sending the HTTPS request to Owner Companion
 * Service.
 */
public class RestClient {

  private final Logger logger = LoggerFactory.getLogger(RestClient.class);
  private final SSLContext sslContext;
  private final ObjectMapper objectMapper;
  private final Duration httpClientTimeout = Duration.ofSeconds(10);

  public RestClient(SSLContext sslContext, ObjectMapper objectMapper) {
    this.sslContext = sslContext;
    this.objectMapper = objectMapper;
  }

  private ObjectMapper objectMapper() {
    return objectMapper;
  }

  private SSLContext sslContext() {
    return sslContext;
  }

  /**
   * Send a GET request to retrieve the owner voucher for the specified device
   * identifier.
   *
   * @param deviceId the device identifier.
   * @return the owner voucher.
   */
  public String getDeviceVoucher(final String deviceId) {
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    HttpClient httpClient = HttpClient.newBuilder().sslContext(sslContext())
        .connectTimeout(httpClientTimeout).executor(executor).build();
    try {
      final String apiServer = To0PropertiesLoader.getProperty("rest.api.server");
      final String path = To0PropertiesLoader.getProperty("rest.api.voucher.path");
      final String revisedPath = path.replace("{deviceId}", deviceId);
      final URI uri = URI.create(apiServer + revisedPath);

      final HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
          .timeout(Duration.ofSeconds(10));
      final HttpRequest httpRequest = httpRequestBuilder.uri(uri).GET().build();
      final HttpResponse<String> httpResponse =
          httpClient.send(httpRequest, BodyHandlers.ofString());
      if (httpResponse.statusCode() != HttpStatus.OK.value()) {
        throw new IOException(httpResponse.toString() + " " + httpResponse.body());
      }
      return httpResponse.body();
    } catch (Exception e) {
      logger.error(
          "Error occurred while fetching the voucher for " + deviceId + ". " + e.getMessage());
      logger.debug(e.getMessage(), e);
      return null;
    } finally {
      // avoiding memory leaks.
      executor.shutdownNow();
      httpClient = null;
    }
  }

  /**
   * Send a POST request to update the {@link DeviceState} information for the
   * specified device identifier.
   *
   * @param deviceId the device identifier.
   * @param state    device state object.
   */
  public void postDeviceState(final String deviceId, final DeviceState state) {
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    HttpClient httpClient = HttpClient.newBuilder().sslContext(sslContext())
        .connectTimeout(httpClientTimeout).executor(executor).build();
    try {
      final String apiServer = To0PropertiesLoader.getProperty("rest.api.server");
      final String path = To0PropertiesLoader.getProperty("rest.api.device.state.path");
      final String revisedPath = path.replace("{deviceId}", deviceId);
      final URI uri = URI.create(apiServer + revisedPath);

      final String requestBody = objectMapper().writeValueAsString(state);
      final HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      final HttpRequest httpRequest =
          httpRequestBuilder.uri(uri).POST(BodyPublishers.ofString(requestBody)).build();
      final HttpResponse<String> httpResponse =
          httpClient.send(httpRequest, BodyHandlers.ofString());
      if (httpResponse.statusCode() != HttpStatus.OK.value()) {
        throw new IOException(httpResponse.toString() + " " + httpResponse.body());
      }
    } catch (Exception e) {
      logger.error(
          "Error occurred while setting the devices state for " + deviceId + ". " + e.getMessage());
      logger.debug(e.getMessage(), e);
    } finally {
      // avoiding memory leaks.
      executor.shutdownNow();
      httpClient = null;
    }
  }

  /**
   * Send a POST request to get {@link SignatureResponse} object for the specified
   * device identifier {@link UUID} and the data to be signed.
   *
   * @param uuid the device identifier.
   * @param bo   data to be signed.
   * @return
   */
  public SignatureResponse signatureOperation(final UUID uuid, final String bo) {
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    HttpClient httpClient = HttpClient.newBuilder().sslContext(sslContext())
        .connectTimeout(httpClientTimeout).executor(executor).build();
    try {
      final String apiServer = To0PropertiesLoader.getProperty("rest.api.server");
      final String path = To0PropertiesLoader.getProperty("rest.api.signature.path");
      final String revisedPath = path.replace("{deviceId}", uuid.toString());
      final URI uri = URI.create(apiServer + revisedPath);

      final HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
      final HttpRequest httpRequest =
          httpRequestBuilder.uri(uri).POST(BodyPublishers.ofString(bo)).build();
      final HttpResponse<String> httpResponse =
          httpClient.send(httpRequest, BodyHandlers.ofString());
      if (httpResponse.statusCode() != HttpStatus.OK.value()) {
        throw new IOException(httpResponse.toString() + " " + httpResponse.body());
      }
      final SignatureResponse response =
          objectMapper().readValue(httpResponse.body(), SignatureResponse.class);
      return response;

    } catch (Exception e) {
      logger.error("Error occurred while getting the signature for " + uuid.toString() + ". "
          + e.getMessage());
      logger.debug(e.getMessage(), e);
      return null;
    } finally {
      // avoiding memory leaks.
      executor.shutdownNow();
      httpClient = null;
    }
  }

  /**
   * Send a POST request to update the error information about the device
   * identifier, as an object {@link DeviceState}.
   *
   * @param deviceId the device identifier.
   * @param state    device state information containing the error.
   */
  public void postError(final String deviceId, final DeviceState state) {
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    HttpClient httpClient = HttpClient.newBuilder().sslContext(sslContext())
        .connectTimeout(httpClientTimeout).executor(executor).build();
    try {
      final String apiServer = To0PropertiesLoader.getProperty("rest.api.server");
      final String path = To0PropertiesLoader.getProperty("rest.api.error.path");
      final String revisedPath = path.replace("{deviceId}", deviceId);
      final URI uri = URI.create(apiServer + revisedPath);

      final String requestBody = objectMapper().writeValueAsString(state);
      final HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      final HttpRequest httpRequest =
          httpRequestBuilder.uri(uri).POST(BodyPublishers.ofString(requestBody)).build();
      final HttpResponse<String> httpResponse =
          httpClient.send(httpRequest, BodyHandlers.ofString());
      if (httpResponse.statusCode() != HttpStatus.OK.value()) {
        throw new IOException(httpResponse.toString() + " " + httpResponse.body());
      }
    } catch (Exception e) {
      logger.error(
          "Error occurred while setting the devices state for " + deviceId + ". " + e.getMessage());
      logger.debug(e.getMessage(), e);
    } finally {
      // avoiding memory leaks.
      executor.shutdownNow();
      httpClient = null;
    }
  }
}

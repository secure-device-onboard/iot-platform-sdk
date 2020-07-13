// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.sdo.iotplatformsdk.common.rest.DeviceState;
import org.sdo.iotplatformsdk.common.rest.ModuleMessage;
import org.sdo.iotplatformsdk.common.rest.RendezvousInstruction;
import org.sdo.iotplatformsdk.common.rest.SetupInfoResponse;
import org.sdo.iotplatformsdk.common.rest.SignatureResponse;
import org.sdo.iotplatformsdk.common.rest.SviMessage;
import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;
import org.sdo.iotplatformsdk.ops.opsimpl.OpsPropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * This class is responsible for sending the HTTPS request to Owner Companion Service.
 */
public class RestClient {

  private final Logger logger = LoggerFactory.getLogger(RestClient.class);
  private final SSLContext sslContext;
  private HttpComponentsClientHttpRequestFactory requestFactory;

  /**
   * Constructor.
   */
  public RestClient(SSLContext sslContext) {
    this.sslContext = sslContext;
  }

  /**
   * Returns an instance of {@link HttpComponentsClientHttpRequestFactory}.
   */
  private HttpComponentsClientHttpRequestFactory sslContext() {
    if (null != sslContext && null == requestFactory) {
      try {
        final SSLConnectionSocketFactory csf =
            new SSLConnectionSocketFactory(sslContext, new DefaultHostnameVerifier());
        final CloseableHttpClient httpClient =
            HttpClients.custom().setSSLSocketFactory(csf).build();
        requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
      } catch (Exception e) {
        logger.error("Error occurred while creating ssl context. ", e.getMessage());
        logger.debug(e.getMessage(), e);
        return null;
      }
    }
    return requestFactory;
  }

  /**
   * Send a GET request to retrieve the owner voucher for the specified device identifier.
   *
   * @param deviceId the device identifier.
   * @return the owner voucher.
   */
  public String getDeviceVoucher(final UUID deviceId) {
    try {
      final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
      interceptors.add(
          new RestHeaderRequestInterceptor(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE));

      final String apiServer = OpsPropertiesLoader.getProperty("rest.api.server");
      final String path = OpsPropertiesLoader.getProperty("rest.api.voucher.path");
      final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiServer).path(path);

      final RestTemplate restTemplate = new RestTemplate(sslContext());
      restTemplate.setInterceptors(interceptors);
      final String url = builder.buildAndExpand(deviceId.toString()).toString();
      return restTemplate.getForObject(url, String.class);
    } catch (Exception e) {
      logger.error(
          "Error occurred while fetching the voucher for " + deviceId + ". " + e.getMessage());
      logger.debug(e.getMessage(), e);
      return null;
    }
  }

  /**
   * Send a PUT request to store the owner voucher for the specified device identifier.
   */
  public void putDeviceVoucher(final String ownerVoucher) {
    try {
      final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
      interceptors.add(new RestHeaderRequestInterceptor(HttpHeaders.CONTENT_TYPE,
          MediaType.APPLICATION_JSON_VALUE));

      final String apiServer = OpsPropertiesLoader.getProperty("rest.api.server");
      final String path = OpsPropertiesLoader.getProperty("rest.api.voucher.path");
      final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiServer).path(path);

      final RestTemplate restTemplate = new RestTemplate(sslContext());
      restTemplate.setInterceptors(interceptors);
      final String url = builder.buildAndExpand("").toString();
      restTemplate.postForObject(url, ownerVoucher, String.class);
    } catch (Exception e) {
      logger.error("Error occurred while putting the voucher. " + e.getMessage());
      logger.debug(e.getMessage(), e);
    }
  }

  /**
   * Send a POST request to update the error information about the device identifier, as an object
   * {@link DeviceState}.
   *
   * @param deviceId the device identifier.
   * @param state    device state information containing the error.
   */
  public void postDeviceState(final String deviceId, final DeviceState state) {
    try {
      final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
      interceptors.add(new RestHeaderRequestInterceptor(HttpHeaders.CONTENT_TYPE,
          MediaType.APPLICATION_JSON_VALUE));

      final String apiServer = OpsPropertiesLoader.getProperty("rest.api.server");
      final String path = OpsPropertiesLoader.getProperty("rest.api.device.state.path");
      final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiServer).path(path);

      final RestTemplate restTemplate = new RestTemplate(sslContext());
      restTemplate.setInterceptors(interceptors);

      restTemplate.postForObject(builder.buildAndExpand(deviceId).toString(), state,
          DeviceState.class);
    } catch (Exception e) {
      logger.error(
          "Error occurred while setting the devices state for " + deviceId + ". " + e.getMessage());
      logger.debug(e.getMessage(), e);
    }
  }

  /**
   * Send a GET request to retrieve an array of {@link SviMessage} representing the serviceinfo
   * messages for the specified device identifier.
   *
   * @param deviceId the device identifier.
   * @return serviceinfo messages to be fetched later for the device.
   */
  public SviMessage[] getMessage(final String deviceId) {
    try {
      final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
      interceptors.add(
          new RestHeaderRequestInterceptor(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE));

      final String apiServer = OpsPropertiesLoader.getProperty("rest.api.server");
      final String path = OpsPropertiesLoader.getProperty("rest.api.serviceinfo.path");
      final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiServer).path(path);

      final RestTemplate restTemplate = new RestTemplate(sslContext());
      restTemplate.setInterceptors(interceptors);

      final SviMessage[] response = restTemplate
          .getForObject(builder.buildAndExpand(deviceId).toString(), SviMessage[].class);

      return response;
    } catch (Exception e) {
      logger.error("Error occurred while fetching the list of serviceinfo messages for " + deviceId
          + ". " + e.getMessage());
      logger.debug(e.getMessage(), e);
      return new SviMessage[0];
    }
  }

  /**
   * Send a POST request to update the {@link ModuleMessage} containing the device serviceinfo.
   *
   * @param deviceId the device identifier.
   * @param messages device serviceinfo.
   */
  public void postMessage(final String deviceId, final List<ModuleMessage> messages) {
    try {
      final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
      interceptors.add(new RestHeaderRequestInterceptor(HttpHeaders.CONTENT_TYPE,
          MediaType.APPLICATION_JSON_VALUE));

      final String apiServer = OpsPropertiesLoader.getProperty("rest.api.server");
      final String path = OpsPropertiesLoader.getProperty("rest.api.serviceinfo.path");
      final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiServer).path(path);

      final RestTemplate restTemplate = new RestTemplate(sslContext());
      restTemplate.setInterceptors(interceptors);

      restTemplate.postForObject(builder.buildAndExpand(deviceId).toString(), messages,
          ModuleMessage[].class);
    } catch (Exception e) {
      logger.error("Error occurred while setting the list of device serviceinfo for " + deviceId
          + ". " + e.getMessage());
      logger.debug(e.getMessage(), e);
    }
  }

  /**
   * Send a GET request to retrieve an array of {@link ModuleMessage} containing the pre-serviceinfo
   * for the specified device identifier.
   *
   * @param deviceId the device identifier.
   * @return pre-serviceinfo meant for the device.
   */
  public ModuleMessage[] getPsi(final String deviceId) {
    try {
      final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
      interceptors.add(
          new RestHeaderRequestInterceptor(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE));

      final String apiServer = OpsPropertiesLoader.getProperty("rest.api.server");
      final String path = OpsPropertiesLoader.getProperty("rest.api.psi.path");
      final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiServer).path(path);

      final RestTemplate restTemplate = new RestTemplate(sslContext());
      restTemplate.setInterceptors(interceptors);

      final ModuleMessage[] response = restTemplate
          .getForObject(builder.buildAndExpand(deviceId).toString(), ModuleMessage[].class);

      return response;
    } catch (Exception e) {
      logger.error(
          "Error occurred while fetching pre-serviceinfo for " + deviceId + ". " + e.getMessage());
      logger.debug(e.getMessage(), e);
      return new ModuleMessage[0];
    }
  }

  /**
   * Send a GET request to retrieve the serviceinfo content for the specified serviceinfo identifier
   * as per the given range.
   *
   * @param valueId the serviceinfo identifier.
   * @param start   index at which serviceinfo will be read from.
   * @param end     index at ehich serviceinfo will be read to.
   * @return byte array containing the ranged serviceinfo contents.
   */
  public byte[] getValue(final UUID deviceId, final String valueId, final int start,
      final int end) {
    try {
      final String apiServer = OpsPropertiesLoader.getProperty("rest.api.server");
      final String path = OpsPropertiesLoader.getProperty("rest.api.serviceinfo.value.path");
      final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiServer).path(path)
          .queryParam("start", Integer.toString(start)).queryParam("end", Integer.toString(end));
      final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
      interceptors.add(new RestHeaderRequestInterceptor(HttpHeaders.ACCEPT,
          MediaType.APPLICATION_OCTET_STREAM_VALUE));
      final RestTemplate restTemplate = new RestTemplate(sslContext());
      restTemplate.setInterceptors(interceptors);
      final String[] pathParameters = {deviceId.toString(), valueId};
      final String restUrl = builder.buildAndExpand((Object[]) pathParameters).toString();
      final byte[] response = restTemplate.getForObject(restUrl, byte[].class);

      return response;
    } catch (Exception e) {
      logger.error("Error occurred while fetching serviceinfo. " + e.getMessage());
      logger.debug(e.getMessage(), e);
      return null;
    }
  }

  /**
   * Send a POST request to get {@link SignatureResponse} object for the specified device identifier
   * {@link UUID} and the data to be signed.
   *
   * @param uuid the device identifier.
   * @param bo   data to be signed.
   * @return
   */
  public SignatureResponse signatureOperation(final UUID uuid, final String bo) {
    try {

      List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
      interceptors.add(
          new RestHeaderRequestInterceptor(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE));
      final RestTemplate restTemplate = new RestTemplate(sslContext());
      restTemplate.setInterceptors(interceptors);

      final String apiServer = OpsPropertiesLoader.getProperty("rest.api.server");
      final String path = OpsPropertiesLoader.getProperty("rest.api.signature.path");
      final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiServer).path(path);

      final SignatureResponse response = restTemplate
          .postForObject(builder.buildAndExpand(uuid).toString(), bo, SignatureResponse.class);
      return response;

    } catch (Exception e) {
      logger.error("Error occurred while getting the signature for " + uuid.toString() + ". "
          + e.getMessage());
      logger.debug(e.getMessage(), e);
      return null;
    }
  }

  /**
   * Send a POST request to perform cipher operations for the given device identifier.
   *
   * @param xb       'b' parameter
   * @param cipherOp cipher operation
   * @param uuid the device identifier
   * @return
   */
  public byte[] cipherOperations(final byte[] xb, final String cipherOp, final UUID uuid) {
    try {

      final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
      interceptors.add(new RestHeaderRequestInterceptor(HttpHeaders.CONTENT_TYPE,
          MediaType.APPLICATION_OCTET_STREAM_VALUE));
      interceptors.add(new RestHeaderRequestInterceptor(HttpHeaders.ACCEPT,
          MediaType.APPLICATION_OCTET_STREAM_VALUE));
      final RestTemplate restTemplate = new RestTemplate(sslContext());
      restTemplate.setInterceptors(interceptors);

      final String apiServer = OpsPropertiesLoader.getProperty("rest.api.server");
      final String path = OpsPropertiesLoader.getProperty("rest.api.ciphers.path");
      final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiServer).path(path)
          .queryParam("operation", cipherOp);
      final String restUrl = builder.buildAndExpand(uuid.toString()).toString();
      byte[] response = restTemplate.postForObject(restUrl, xb, byte[].class);
      return response;

    } catch (Exception e) {
      logger.error("Error occurred while getting the result of asymmetric key exchange content. "
          + e.getMessage());
      logger.debug(e.getMessage(), e);
      return null;
    }
  }

  /**
   * Send a GET request to retrieve the {@link SetupInfoResponse} containing the new
   * {@link RendezvousInstruction} and device identifier for the specified device identifier.
   *
   * @param deviceId the device identifier.
   * @return
   */
  public SetupInfoResponse getSetupInfo(final String deviceId) {
    try {
      final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
      interceptors.add(
          new RestHeaderRequestInterceptor(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE));

      final String apiServer = OpsPropertiesLoader.getProperty("rest.api.server");
      final String path = OpsPropertiesLoader.getProperty("rest.api.setupinfo.path");
      final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiServer).path(path);

      final RestTemplate restTemplate = new RestTemplate(sslContext());
      restTemplate.setInterceptors(interceptors);

      final SetupInfoResponse response = restTemplate
          .getForObject(builder.buildAndExpand(deviceId).toString(), SetupInfoResponse.class);

      return response;
    } catch (Exception e) {
      logger.error("Error occurred while fetching device setup information for " + deviceId + ". "
          + e.getMessage());
      logger.debug(e.getMessage(), e);
      return new SetupInfoResponse();
    }
  }

  /**
   * Send a POST request to update the error information about the device identifier, as an object
   * {@link DeviceState}.
   *
   * @param deviceId the device identifier.
   * @param state    device state information containing the error.
   */
  public void postError(final String deviceId, final DeviceState state) {
    try {
      final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
      interceptors.add(new RestHeaderRequestInterceptor(HttpHeaders.CONTENT_TYPE,
          MediaType.APPLICATION_JSON_VALUE));

      final String apiServer = OpsPropertiesLoader.getProperty("rest.api.server");
      final String path = OpsPropertiesLoader.getProperty("rest.api.error.path");
      final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiServer).path(path);

      final RestTemplate restTemplate = new RestTemplate(sslContext());
      restTemplate.setInterceptors(interceptors);

      restTemplate.postForObject(builder.buildAndExpand(deviceId).toString(), state,
          DeviceState.class);
    } catch (Exception e) {
      logger.error(
          "Error occurred while setting the devices state for " + deviceId + ". " + e.getMessage());
      logger.debug(e.getMessage(), e);
    }
  }

  /**
   * Send a POST request to store the TO2 device session information for the given device
   * identifier.
   *
   * @param deviceId the device identifier
   * @param session  {@link To2DeviceSessionInfo}
   */
  public void postSessionState(final String deviceId, final To2DeviceSessionInfo session) {
    try {
      final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
      interceptors.add(new RestHeaderRequestInterceptor(HttpHeaders.CONTENT_TYPE,
          MediaType.APPLICATION_JSON_VALUE));

      final String apiServer = OpsPropertiesLoader.getProperty("rest.api.server");
      final String path = OpsPropertiesLoader.getProperty("rest.api.session.path");
      final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiServer).path(path);

      final RestTemplate restTemplate = new RestTemplate(sslContext());
      restTemplate.setInterceptors(interceptors);

      restTemplate.postForObject(builder.buildAndExpand(deviceId).toString(), session,
          To2DeviceSessionInfo.class);
    } catch (Exception e) {
      logger.error("Error occurred while setting the devices session info for " + deviceId + ". "
          + e.getMessage());
      logger.debug(e.getMessage(), e);
    }
  }

  /**
   * Send a GET request to fetch the TO@ device session information for the given device identifier.
   * @param deviceId the device identifier
   * @return         {@link To2DeviceSessionInfo}
   */
  public To2DeviceSessionInfo getSessionState(final String deviceId) {
    try {
      final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
      interceptors.add(
          new RestHeaderRequestInterceptor(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE));

      final String apiServer = OpsPropertiesLoader.getProperty("rest.api.server");
      final String path = OpsPropertiesLoader.getProperty("rest.api.session.path");
      final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiServer).path(path);

      final RestTemplate restTemplate = new RestTemplate(sslContext());
      restTemplate.setInterceptors(interceptors);
      final String url = builder.buildAndExpand(deviceId.toString()).toString();
      return restTemplate.getForObject(url, To2DeviceSessionInfo.class);
    } catch (Exception e) {
      logger.error("Error occurred while fetching the device session info for " + deviceId + ". "
          + e.getMessage());
      logger.debug(e.getMessage(), e);
      return null;
    }
  }

  /**
   * Send a 'DELETE' request to delete the TO2 session information for the given device identifier.
   *
   * @param deviceId the device identifier.
   */
  public void deleteSessionState(final String deviceId) {
    try {
      final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
      interceptors.add(
          new RestHeaderRequestInterceptor(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE));

      final String apiServer = OpsPropertiesLoader.getProperty("rest.api.server");
      final String path = OpsPropertiesLoader.getProperty("rest.api.session.path");
      final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiServer).path(path);

      final RestTemplate restTemplate = new RestTemplate(sslContext());
      restTemplate.setInterceptors(interceptors);
      final String url = builder.buildAndExpand(deviceId.toString()).toString();
      restTemplate.delete(url);
    } catch (Exception e) {
      logger.error("Error occurred while deleting the device session info for " + deviceId + ". "
          + e.getMessage());
      logger.debug(e.getMessage(), e);
    }
  }

  /**
   * Send a GET request to check whether the 'Resale' protocol for the specified device identifier,
   * is supported or not. If an exception occurs, this method returns true.
   *
   * @param deviceId the device identifier.
   * @return boolean value.
   */
  public boolean getOwnerResaleFlag(final String deviceId) {
    try {
      final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
      interceptors.add(
          new RestHeaderRequestInterceptor(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE));

      final String apiServer = OpsPropertiesLoader.getProperty("rest.api.server");
      final String path = OpsPropertiesLoader.getProperty("rest.api.owner.resale.support.path");
      final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiServer).path(path);

      final RestTemplate restTemplate = new RestTemplate(sslContext());
      restTemplate.setInterceptors(interceptors);
      final String url = builder.buildAndExpand(deviceId.toString()).toString();
      return restTemplate.getForObject(url, Boolean.class);
    } catch (Exception e) {
      logger.error(
          "Error occurred while fetching resale flag for " + deviceId + ". " + e.getMessage());
      logger.debug(e.getMessage(), e);
      return true;
    }
  }
}

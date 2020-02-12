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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.sdo.iotplatformsdk.common.rest.DeviceState;
import org.sdo.iotplatformsdk.common.rest.SignatureResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * This class is responsible for sending the HTTPS request to Owner Companion
 * Service.
 */
@Component
public class RestClient {

  private final Logger logger = LoggerFactory.getLogger(RestClient.class);
  private RestUri restUri;

  @Value("${rest.api.server}")
  private String apiServer;

  private RestTemplate template;

  public RestClient() {}

  @Autowired
  public void setRestUri(RestUri restUri) {
    this.restUri = restUri;
  }

  private RestTemplate getRestTemplate() {
    return template;
  }

  @Autowired
  public void setRestTemplate(RestTemplate restTemplate) {
    this.template = restTemplate;
  }

  public void setApiServer(String apiServer) {
    this.apiServer = apiServer;
  }

  protected List<ClientHttpRequestInterceptor> getInspectors() {
    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
    return interceptors;
  }

  /**
   * Send a GET request to retrieve the owner voucher for the specified device
   * identifier.
   *
   * @param deviceId the device identifier.
   * @return the owner voucher.
   */
  public String getDeviceVoucher(final String deviceId) {
    try {
      List<ClientHttpRequestInterceptor> interceptors = getInspectors();
      interceptors.add(new RequestInterceptor("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE));

      String path = restUri.getVoucherUrl_();
      UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiServer).path(path);

      RestTemplate restTemplate = getRestTemplate();
      restTemplate.setInterceptors(interceptors);
      String url = builder.buildAndExpand(deviceId.toString()).toString();
      return restTemplate.getForObject(url, String.class);
    } catch (Exception e) {
      logger.error(
          "Error occurred while fetching the voucher for " + deviceId + ". " + e.getMessage());
      logger.debug(e.getMessage(), e);
      return null;
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
    try {
      final List<ClientHttpRequestInterceptor> interceptors = getInspectors();
      interceptors
          .add(new RequestInterceptor("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE));

      final String path = restUri.getDevStateUrl_();
      final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiServer).path(path);

      final RestTemplate restTemplate = getRestTemplate();
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
   * Send a POST request to get {@link SignatureResponse} object for the specified
   * device identifier {@link UUID} and the data to be signed.
   *
   * @param uuid the device identifier.
   * @param bo   data to be signed.
   * @return
   */
  public SignatureResponse signatureOperation(final UUID uuid, final String bo) {
    try {

      final List<ClientHttpRequestInterceptor> interceptors = getInspectors();
      interceptors
          .add(new RequestInterceptor("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE));
      final RestTemplate restTemplate = getRestTemplate();
      restTemplate.setInterceptors(interceptors);

      final String path = restUri.getSignatureUrl_();
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
   * Send a POST request to update the error information about the device
   * identifier, as an object {@link DeviceState}.
   *
   * @param deviceId the device identifier.
   * @param state    device state information containing the error.
   */
  public void postError(final String deviceId, final DeviceState state) {
    try {
      final List<ClientHttpRequestInterceptor> interceptors = getInspectors();
      interceptors
          .add(new RequestInterceptor("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE));

      final String path = restUri.getErrorUrl_();
      final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiServer).path(path);

      final RestTemplate restTemplate = getRestTemplate();
      restTemplate.setInterceptors(interceptors);

      restTemplate.postForObject(builder.buildAndExpand(deviceId).toString(), state,
          DeviceState.class);
    } catch (Exception e) {
      logger.error(
          "Error occurred while setting the devices state for " + deviceId + ". " + e.getMessage());
      logger.debug(e.getMessage(), e);
    }
  }
}

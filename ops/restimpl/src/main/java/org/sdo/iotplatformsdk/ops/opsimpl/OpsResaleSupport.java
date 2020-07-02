// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.opsimpl;

import org.sdo.iotplatformsdk.ops.rest.RestClient;
import org.sdo.iotplatformsdk.ops.to2library.OwnerResaleSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpsResaleSupport implements OwnerResaleSupport {

  protected static final Logger LOGGER = LoggerFactory.getLogger(OpsResaleSupport.class);
  private final RestClient restClient;

  /**
   * Constructor.
   */
  public OpsResaleSupport(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public boolean ownerResaleSupported(String deviceId) {
    try {
      return restClient.getOwnerResaleFlag(deviceId);
    } catch (Exception e) {
      LOGGER.debug("Error while fetching resale support flag. Defaulting to return true.");
      return true;
    }
  }
}

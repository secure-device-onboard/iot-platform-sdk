// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.to0scheduler.to0service;

import java.io.IOException;
import java.nio.CharBuffer;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.to0scheduler.rest.RestClient;
import org.sdo.iotplatformsdk.to0scheduler.to0library.To0ProxyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class To0ProxystoreImpl implements To0ProxyStore {

  protected static final Logger logger = LoggerFactory.getLogger(To0ProxystoreImpl.class);

  private final RestClient restClient;

  public To0ProxystoreImpl(RestClient restClient) {
    this.restClient = restClient;
  }

  /*
   * Retrieve a string representation of ownership voucher for the specified device identifier.
   * Create the Ownership voucher object from the received content.
   */
  @Override
  public OwnershipProxy getProxy(final String deviceId) throws IOException {
    try {
      logger.info("Loading Ownership voucher for " + deviceId);
      final String voucher = restClient.getDeviceVoucher(deviceId);
      if (null == voucher) {
        logger.warn("Ownership voucher not found with uuid " + deviceId);
        return null;
      }
      final OwnershipProxy proxy =
          new OwnershipProxyCodec.OwnershipProxyDecoder().decode(CharBuffer.wrap(voucher));
      return proxy;
    } catch (Exception e) {
      logger.error("Failed to load Ownership voucher for" + deviceId + " - " + e.getMessage(), e);
      logger.debug(e.getMessage(), e);
      throw new IOException(e.getMessage());
    }
  }

}

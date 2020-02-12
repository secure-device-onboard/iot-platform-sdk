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

import java.io.IOException;
import java.nio.CharBuffer;

import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.to0scheduler.to0library.To0ProxyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class To0ProxystoreImpl implements To0ProxyStore {

  protected static final Logger logger = LoggerFactory.getLogger(To0ProxystoreImpl.class);

  private RestClient restClient;

  @Autowired
  public void setRestClient(RestClient restClient) {
    this.restClient = restClient;
  }

  /*
   * Retrieve a string representation of ownership voucher for the specified device identifier.
   * Create the Ownership voucher object from the received content.
   */
  @Override
  public OwnershipProxy getProxy(final String deviceId) throws IOException {
    try {
      logger.debug("Loading Ownership voucher for " + deviceId);
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

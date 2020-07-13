// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.opsimpl;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.CharBuffer;
import java.util.UUID;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec;
import org.sdo.iotplatformsdk.common.protocol.config.OwnershipProxyStorage;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.ops.rest.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpsProxyStorage implements OwnershipProxyStorage {

  protected static final Logger LOGGER = LoggerFactory.getLogger(OpsProxyStorage.class);

  private final RestClient restClient;

  public OpsProxyStorage(RestClient restClient) {
    this.restClient = restClient;
  }

  /*
   * Send a REST call to retrieve the content of OwnershipVoucher. Creates the OwnerShipVoucher
   * instance from the received contents.
   */
  @Override
  public OwnershipProxy load(final UUID uuid) throws IOException {
    try {
      final String voucher = restClient.getDeviceVoucher(uuid);
      if (null == voucher) {
        LOGGER.warn("Proxy not found with uuid " + uuid.toString());
        return null;
      }

      final OwnershipProxy proxy =
          new OwnershipProxyCodec.OwnershipProxyDecoder().decode(CharBuffer.wrap(voucher));

      if (null != proxy && !proxy.getOh().getG().equals(uuid)) {
        LOGGER.warn("uuid mismatch :" + proxy.getOh().getG());
        return null;
      }
      return proxy;
    } catch (Exception e) {
      LOGGER.error("Failed to load proxy " + uuid.toString() + " - " + e.getMessage(), e);
      LOGGER.debug(e.getMessage(), e);
      throw new IOException(e);
    }
  }

  @Override
  public void store(OwnershipProxy proxy) throws IOException {
    StringWriter opWriter = new StringWriter();
    new OwnershipProxyCodec.OwnershipProxyEncoder().encode(opWriter, proxy);
    restClient.putDeviceVoucher(opWriter.toString());
  }
}

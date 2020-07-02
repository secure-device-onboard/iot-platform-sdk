// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.opsimpl;

import org.sdo.iotplatformsdk.common.protocol.config.ObjectFactory;
import org.sdo.iotplatformsdk.ops.rest.RestClient;
import org.sdo.iotplatformsdk.ops.to2library.OwnerEventHandler;

public class OpsOwnerEventHandlerFactory implements ObjectFactory<OwnerEventHandler> {

  private final RestClient restClient;

  public OpsOwnerEventHandlerFactory(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public OwnerEventHandler getObject() {
    OpsOwnerEventHandler handler = new OpsOwnerEventHandler(restClient);
    return handler;
  }
}

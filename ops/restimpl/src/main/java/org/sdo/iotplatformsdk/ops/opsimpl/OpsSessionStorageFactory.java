// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.opsimpl;

import org.sdo.iotplatformsdk.common.protocol.config.ObjectFactory;
import org.sdo.iotplatformsdk.ops.rest.RestClient;
import org.sdo.iotplatformsdk.ops.to2library.SessionStorage;

public class OpsSessionStorageFactory implements ObjectFactory<SessionStorage> {

  private final RestClient restClient;

  public OpsSessionStorageFactory(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public SessionStorage getObject() {
    return new OpsSessionStorage(restClient);
  }
}

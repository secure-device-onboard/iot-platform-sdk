// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.config;

import java.io.IOException;
import java.util.UUID;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;

public interface OwnershipProxyStorage {

  OwnershipProxy load(UUID uuid) throws IOException;

  void store(OwnershipProxy proxy) throws IOException;
}

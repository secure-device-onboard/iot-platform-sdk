// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.serviceinfo;

import java.util.UUID;
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfo;

/**
 * Consumes received ServiceInfo data.
 *
 * <p>For details on usage, see {@link ServiceInfoSource}.
 *
 * @see ServiceInfoSource
 */
@FunctionalInterface
public interface ServiceInfoMultiSink extends ServiceInfoModule {

  void putServiceInfo(UUID id, ServiceInfo serviceInfo);
}

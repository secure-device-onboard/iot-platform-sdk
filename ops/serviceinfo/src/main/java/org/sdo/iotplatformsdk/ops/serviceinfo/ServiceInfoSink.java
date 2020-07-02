// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.serviceinfo;

import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfoEntry;

/**
 * Consumes received ServiceInfo data.
 *
 * <p>For details on usage, see {@link ServiceInfoSource}.
 *
 * @see ServiceInfoSource
 */
@FunctionalInterface
public interface ServiceInfoSink extends ServiceInfoModule {

  void putServiceInfo(ServiceInfoEntry serviceInfoEntry);
}

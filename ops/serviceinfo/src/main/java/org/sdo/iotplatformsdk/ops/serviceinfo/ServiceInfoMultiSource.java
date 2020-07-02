// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.serviceinfo;

import java.util.List;
import java.util.UUID;
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfoEntry;

/**
 * Supplies an ordered sequence of ServiceInfo key/value pairs for transmission to
 * the remote end of the protocol.
 *
 * <p>For details on usage, see {@link ServiceInfoSource}.
 *
 * @see ServiceInfoSource
 */
@FunctionalInterface
public interface ServiceInfoMultiSource extends ServiceInfoModule {

  List<ServiceInfoEntry> getServiceInfo(UUID id);
}

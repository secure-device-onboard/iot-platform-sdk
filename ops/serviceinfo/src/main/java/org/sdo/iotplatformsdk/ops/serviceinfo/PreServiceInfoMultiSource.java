// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.serviceinfo;

import java.util.List;
import java.util.UUID;
import org.sdo.iotplatformsdk.common.protocol.types.PreServiceInfoEntry;

/**
 * A producer of TO2.GetNextDeviceServiceInfo[0].psi key/value pairs.
 *
 * <p>For details on usage, see {@link ServiceInfoSource}.
 *
 * @see ServiceInfoSource
 */
@FunctionalInterface
public interface PreServiceInfoMultiSource extends ServiceInfoModule {

  List<PreServiceInfoEntry> getPreServiceInfo(UUID id);
}

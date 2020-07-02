// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.to2library;

import java.util.UUID;
import org.sdo.iotplatformsdk.common.protocol.types.RendezvousInfo;

/**
 * Provides the hook for the (g2, r2) to (g3, r3) "TO2.SetupDevice" transformation.
 */
@FunctionalInterface
public interface SetupDeviceService {

  Setup setup(final UUID g2, final RendezvousInfo r2);

  interface Setup {
    UUID g3();

    RendezvousInfo r3();
  }
}

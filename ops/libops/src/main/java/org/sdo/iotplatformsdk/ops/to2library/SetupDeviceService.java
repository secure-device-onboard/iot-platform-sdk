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

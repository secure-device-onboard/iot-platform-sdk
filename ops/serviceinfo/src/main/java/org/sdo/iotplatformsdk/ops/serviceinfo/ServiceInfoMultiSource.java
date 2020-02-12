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

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

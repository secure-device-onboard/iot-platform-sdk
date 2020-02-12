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

import java.io.IOException;
import java.time.Duration;

import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;
import org.sdo.iotplatformsdk.ops.to2library.SessionStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpsSessionStorage implements SessionStorage {

  protected static final Logger LOGGER = LoggerFactory.getLogger(OpsSessionStorage.class);

  private RestClient restClient;

  public OpsSessionStorage(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public To2DeviceSessionInfo load(Object key) throws IOException {
    final To2DeviceSessionInfo session = restClient.getSessionState(key.toString());
    return session;
  }

  @Override
  public void store(Object key, To2DeviceSessionInfo value) throws IOException {
    if (value instanceof To2DeviceSessionInfo) {
      restClient.postSessionState(key.toString(), value);
    }
  }

  @Override
  public void store(Object key, To2DeviceSessionInfo value, Duration timeout) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void remove(Object key) {
    restClient.deleteSessionState(key.toString());
  }

}

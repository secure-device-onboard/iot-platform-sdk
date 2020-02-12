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

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;
import org.springframework.beans.factory.annotation.Autowired;

public class SimpleSessionStorage implements SessionStorage {

  private final Map<Object, Value> map = new ConcurrentHashMap<>();

  private Duration sessionTimeToLive = Duration.ofMinutes(5);

  private Duration getSessionTimeToLive() {
    return Objects.requireNonNull(sessionTimeToLive);
  }

  @Autowired
  @SuppressWarnings("unused")
  public void setSessionTimeToLive(Duration sessionTimeToLive) {
    this.sessionTimeToLive = sessionTimeToLive;
  }

  @Override
  public To2DeviceSessionInfo load(Object key) {
    flush();
    Value value = getMap().get(key);

    if (null != value) {
      value.touch();
    }

    return null != value ? (To2DeviceSessionInfo) value.getValue() : null;
  }

  @Override
  public void store(Object key, To2DeviceSessionInfo value) {
    this.store(key, value, getSessionTimeToLive());
  }

  @Override
  public void store(Object key, To2DeviceSessionInfo value, Duration timeout) {
    Value currentValue = getMap().get(key);
    if (null != currentValue) {
      To2DeviceSessionInfo to2DeviceSessionInfo = (To2DeviceSessionInfo) currentValue.getValue();
      if (null == value.getMessage41Store()) {
        value.setMessage41Store(to2DeviceSessionInfo.getMessage41Store());
      }
      if (null == value.getMessage45Store()) {
        value.setMessage45Store(to2DeviceSessionInfo.getMessage45Store());
      }
      if (null == value.getMessage47Store()) {
        value.setMessage47Store(to2DeviceSessionInfo.getMessage47Store());
      }
      if (null == value.getMessage41Store()) {
        value.setDeviceCryptoInfo(to2DeviceSessionInfo.getDeviceCryptoInfo());
      }
    }
    getMap().put(key, new Value(value, timeout));
    flush();
  }

  @Override
  public void remove(Object key) {
    getMap().remove(key);
  }

  private void flush() {
    getMap().entrySet().removeIf(entry -> entry.getValue().isExpired());
  }

  private Map<Object, Value> getMap() {
    return map;
  }

  class Value {

    private final Duration timeout;
    private final Object value;
    private Instant expiresAt;

    Value(Object value, Duration timeout) {
      this.timeout = timeout;
      this.expiresAt = Instant.now().plus(timeout);
      this.value = value;
    }

    private Instant getExpiresAt() {
      return expiresAt;
    }

    private Duration getTimeout() {
      return timeout;
    }

    private Object getValue() {
      return value;
    }

    private boolean isExpired() {
      return (Instant.now().isAfter(getExpiresAt()));
    }

    private void touch() {
      this.expiresAt = Instant.now().plus(getTimeout());
    }
  }
}

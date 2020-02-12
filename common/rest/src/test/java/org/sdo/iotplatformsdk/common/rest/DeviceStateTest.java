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

package org.sdo.iotplatformsdk.common.rest;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.rest.DeviceState;
import org.sdo.iotplatformsdk.common.rest.ProtocolError;

class DeviceStateTest {

  DeviceState deviceState;
  Optional<String> g3;
  Optional<ProtocolError> to0Error;
  Optional<ProtocolError> to2Error;
  Optional<String> to0Timestamp;
  Optional<String> to2State;
  Optional<String> to2Timestamp;
  Optional<Integer> ws;
  ProtocolError to0pe;
  ProtocolError to2pe;

  @BeforeEach
  void beforeEach() {

    to0pe = new ProtocolError();
    to2pe = new ProtocolError();

    deviceState = new DeviceState();
    g3 = Optional.of("Test");
    to0Error = Optional.of(to0pe);
    to2Error = Optional.of(to2pe);
    to0Timestamp = Optional.of("2019-07-16 00:00:01");
    to2State = Optional.of("Ready");
    to2Timestamp = Optional.of("2019-07-16 12:00:00");;
    ws = Optional.of(100);

  }

  @Test
  void test_Bean() throws IOException {

    deviceState.setG3(g3);
    deviceState.setTo0Error(to0Error);
    deviceState.setTo0Timestamp(to0Timestamp);
    deviceState.setTo0Ws(ws);
    deviceState.setTo2Error(to2Error);
    deviceState.setTo2State(to2State);
    deviceState.setTo2Timestamp(to2Timestamp);

    deviceState.getG3();
    deviceState.getTo0Error();
    deviceState.getTo0Timestamp();
    deviceState.getTo0Ws();
    deviceState.getTo2Error();
    deviceState.getTo2State();
    deviceState.getTo2Timestamp();

  }

}


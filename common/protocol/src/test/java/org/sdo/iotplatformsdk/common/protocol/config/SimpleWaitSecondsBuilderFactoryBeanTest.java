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

package org.sdo.iotplatformsdk.common.protocol.config;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.config.SimpleWaitSecondsBuilderFactoryBean;

class SimpleWaitSecondsBuilderFactoryBeanTest {

  Duration waitSeconds;
  SimpleWaitSecondsBuilderFactoryBean simpleWaitSecondsBuilderFactoryBean;

  @BeforeEach
  void beforeEach() {

    simpleWaitSecondsBuilderFactoryBean = new SimpleWaitSecondsBuilderFactoryBean();
    waitSeconds = Duration.ofMillis(5000);
  }

  @Test
  void test_Bean() throws Exception {

    simpleWaitSecondsBuilderFactoryBean.setWaitSeconds(waitSeconds);

    simpleWaitSecondsBuilderFactoryBean.getObject();
    simpleWaitSecondsBuilderFactoryBean.getObjectType();
    simpleWaitSecondsBuilderFactoryBean.getWaitSeconds();
  }
}

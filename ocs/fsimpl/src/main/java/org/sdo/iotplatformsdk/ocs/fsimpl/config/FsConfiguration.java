/*******************************************************************************
 * Copyright 2020 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/

package org.sdo.iotplatformsdk.ocs.fsimpl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import org.sdo.iotplatformsdk.ocs.fsimpl.fs.FsOcsContractImpl;
import org.sdo.iotplatformsdk.ocs.fsimpl.rest.FsHealthController;
import org.sdo.iotplatformsdk.ocs.fsimpl.rest.OcsRestController;
import org.sdo.iotplatformsdk.ocs.services.OcsRestContract;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring boot configuration class. Specifies the bean definitions that are used throughout the
 * application.
 */
@Configuration
@EnableAutoConfiguration
public class FsConfiguration {

  @Bean
  protected FsHealthController fsHealthController() {
    return new FsHealthController();
  }

  @Bean
  public OcsRestController ocsRestController() {
    return new OcsRestController(ocsRestContract());
  }

  @Bean
  protected OcsRestContract ocsRestContract() {
    final ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    return new FsOcsContractImpl(mapper);
  }
}

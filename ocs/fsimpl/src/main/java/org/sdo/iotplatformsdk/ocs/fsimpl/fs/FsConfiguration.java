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

package org.sdo.iotplatformsdk.ocs.fsimpl.fs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import org.sdo.iotplatformsdk.ocs.services.DataManager;
import org.sdo.iotplatformsdk.ocs.services.OcsRestContract;
import org.sdo.iotplatformsdk.ocs.services.OcsRestController;
import org.springframework.beans.factory.annotation.Value;
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

  // interval at which to0 scheduler will run repeatedly. Default: 60 seconds.
  @Value("${to0.scheduler.interval:60}")
  private int toSchedulerInterval;

  @Bean
  public OcsRestController ocsRestController() {
    return new OcsRestController();
  }

  @Bean
  public DataManager dataManager() {
    return new FsDataManager();
  }

  @Bean
  public OcsRestContract ocsRestContract() {
    return new FsOcsContractImpl(toSchedulerInterval);
  }

  @Bean
  protected ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    return mapper;
  }

  @Bean
  protected FsRestClient fsRestClient() {
    return new FsRestClient();
  }
}

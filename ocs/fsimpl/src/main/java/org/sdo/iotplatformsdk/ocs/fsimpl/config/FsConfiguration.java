// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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

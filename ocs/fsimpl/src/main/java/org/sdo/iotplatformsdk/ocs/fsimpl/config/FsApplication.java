// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ocs.fsimpl.config;

import java.util.Collections;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main spring boot application class.
 */
@SpringBootApplication
@ComponentScan({"org.sdo.iotplatformsdk.ocs.fsimpl.config",
    "org.sdo.iotplatformsdk.ocs.fsimpl.rest"})
public class FsApplication extends SpringBootServletInitializer {

  /**
   * Main method.
   *
   * @param args arguments.
   */
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(FsConfiguration.class);
    app.setBannerMode(Banner.Mode.OFF);
    app.setDefaultProperties(Collections.singletonMap("server.port", "9009"));
    app.run(args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    return builder.sources(FsConfiguration.class);
  }
}

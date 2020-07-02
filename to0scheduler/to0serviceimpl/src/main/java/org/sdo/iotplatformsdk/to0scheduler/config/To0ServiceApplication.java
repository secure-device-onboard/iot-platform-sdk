// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.to0scheduler.config;

import java.util.Collections;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main Spring-boot Application.
 */
@SpringBootApplication
@ComponentScan({"org.sdo.iotplatformsdk.to0scheduler.config",
    "org.sdo.iotplatformsdk.to0scheduler.rest"})
public class To0ServiceApplication extends SpringBootServletInitializer {

  /**
   * Entry function of spring-boot application.
   *
   * @param args List of arguments
   */
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(To0ServiceConfiguration.class);
    app.setBannerMode(Banner.Mode.OFF);
    app.setDefaultProperties(Collections.singletonMap("server.port", "8049"));
    app.run(args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    return builder.sources(To0ServiceConfiguration.class);
  }
}

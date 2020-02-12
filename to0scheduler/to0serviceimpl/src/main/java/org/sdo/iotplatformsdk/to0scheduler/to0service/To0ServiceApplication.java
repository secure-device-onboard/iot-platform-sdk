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

package org.sdo.iotplatformsdk.to0scheduler.to0service;

import java.util.Properties;

import org.sdo.iotplatformsdk.common.protocol.config.SdoSpringProperties;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Main Spring-boot Application.
 */
@SpringBootApplication
public class To0ServiceApplication extends SpringBootServletInitializer {

  /**
   * Entry function of spring-boot application.
   *
   * @param args List of arguments
   */
  public static void main(String[] args) {
    final Properties defaults = new SdoSpringProperties();

    /**
     * Setting Rendezvous server port
     */
    defaults.setProperty(SdoSpringProperties.SERVER_PORT, "8042");

    SpringApplication app = new SpringApplication(To0ServiceConfiguration.class);

    /**
     * Spring Logo will not be printed on app start as Banner Mode is off
     */
    app.setBannerMode(Banner.Mode.OFF);

    /**
     * Initializes application with default properties
     */
    app.setDefaultProperties(defaults);

    /**
     * Run the main class
     */
    app.run(args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    return builder.sources(To0ServiceConfiguration.class);
  }
}

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

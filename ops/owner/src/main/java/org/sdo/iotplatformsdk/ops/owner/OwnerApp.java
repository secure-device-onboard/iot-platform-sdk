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

package org.sdo.iotplatformsdk.ops.owner;

import java.util.Properties;

import org.sdo.iotplatformsdk.common.protocol.config.SdoSpringProperties;
import org.sdo.iotplatformsdk.ops.to2library.OwnerConfiguration;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class OwnerApp extends SpringBootServletInitializer {

  /**
   * Command-line entry point.
   */
  public static void main(String[] args) {
    configureApplication(new SpringApplicationBuilder()).run(args);
  }

  private static SpringApplicationBuilder configureApplication(SpringApplicationBuilder builder) {

    /**
     * Creates a new spring application
     * Sets banner mode and initializes the properties with default values 
    */
    return builder.sources(OwnerConfiguration.class, OwnerApp.class).bannerMode(Banner.Mode.OFF)
        .properties(getDefaultProperties());
  }

  private static Properties getDefaultProperties() {
    Properties defaults = new SdoSpringProperties();
    defaults.setProperty(SdoSpringProperties.SERVER_PORT, "8042");
    return defaults;
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    return configureApplication(builder);
  }
}

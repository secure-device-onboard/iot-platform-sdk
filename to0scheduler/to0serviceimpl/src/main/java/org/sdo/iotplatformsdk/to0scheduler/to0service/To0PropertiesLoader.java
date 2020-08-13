// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.to0scheduler.to0service;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.configuration2.EnvironmentConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for loading the properties from the configuration file, or,
 * from system properties.
 */
public class To0PropertiesLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(To0PropertiesLoader.class);
  private static final String configurationFile = "application.properties";
  private static final ConcurrentHashMap<String, String> propertiesMap = new ConcurrentHashMap<>();
  private static EnvironmentConfiguration environmentConfiguration;
  private static SystemConfiguration systemConfiguration;
  private static FileBasedConfiguration fileBasedConfiguration;

  // initialize the configuration.
  static {
    environmentConfiguration = new EnvironmentConfiguration();
    systemConfiguration = new SystemConfiguration();
    final FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
        new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
            .configure(new Parameters().properties().setFileName(configurationFile));
    try {
      fileBasedConfiguration = builder.getConfiguration();
      // configuration will be null if the configurationFile doesn't exist.
      // throwing exception for logging purposes.
      if (null == fileBasedConfiguration) {
        throw new ConfigurationException();
      }
    } catch (ConfigurationException e) {
      LOGGER.info("Unable to find/load " + configurationFile);
    }
  }

  /**
   * Get the value of the specified property in the following preference order: system property,
   * environment variable, config file. Once a property is loaded, it is serviced from cache.
   *
   * @param property Property to be read
   * @return         String value of property
   */
  public static final String getProperty(final String property) {
    if (null == property || property.isBlank()) {
      return null;
    }
    if (!propertiesMap.containsKey(property)) {
      if (systemConfiguration.containsKey(property)) {
        propertiesMap.put(property,
            systemConfiguration.interpolatedConfiguration().getString(property));
      } else if (environmentConfiguration.containsKey(property)) {
        propertiesMap.put(property,
            environmentConfiguration.interpolatedConfiguration().getString(property));
      } else if (null != fileBasedConfiguration && fileBasedConfiguration.containsKey(property)) {
        propertiesMap.put(property, fileBasedConfiguration.getString(property));
      }
    }
    return (null == propertiesMap.get(property) || propertiesMap.get(property).isBlank()) ? null
        : propertiesMap.get(property);
  }
}

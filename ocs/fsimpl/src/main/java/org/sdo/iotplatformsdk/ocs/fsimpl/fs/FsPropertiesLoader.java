// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ocs.fsimpl.fs;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for loading the properties from the configuration file, or,
 * from system properties.
 */
public class FsPropertiesLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(FsPropertiesLoader.class);
  private static final String configurationFile = "application.properties";
  private static final Properties properties = new Properties();
  private static final ConcurrentHashMap<String, String> propertiesMap = new ConcurrentHashMap<>();

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
    if (!propertiesMap.contains(property)) {
      final String value = null != System.getProperty(property) ? System.getProperty(property)
          : System.getenv(property);
      if (null != value) {
        propertiesMap.putIfAbsent(property, value);
      } else {
        try {
          properties.load(new FileReader(configurationFile));
          if (null != properties.getProperty(property)
              && !properties.getProperty(property).trim().isEmpty()) {
            propertiesMap.putIfAbsent(property, properties.getProperty(property));
          }
        } catch (IOException e) {
          LOGGER.error("Unable to find/load " + configurationFile + ".");
          LOGGER.debug(e.getMessage(), e);
        }
      }
    }
    return propertiesMap.get(property);
  }
}

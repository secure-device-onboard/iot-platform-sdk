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

package org.sdo.iotplatformsdk.common.protocol.config;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.SecureRandomAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecureRandomFactoryBean implements FactoryBean<SecureRandom> {

  private static final Logger LOG = LoggerFactory.getLogger(SecureRandomFactoryBean.class);
  private SdoProperties properties = null;

  @Override
  public SecureRandom getObject() {

    SdoProperties properties = getProperties();
    if (null == properties) {
      throw new FactoryBeanNotInitializedException("properties must not be null");
    }

    for (SecureRandomAlgorithm algo : properties.getSecureRandom()) {

      String algoName = algo.toString().replaceAll("_", "-");
      try {
        SecureRandom secureRandom = SecureRandom.getInstance(algoName);
        LOG.info("using SecureRandom " + secureRandom.getAlgorithm());
        return secureRandom;

      } catch (NoSuchAlgorithmException e) {
        // provider not available? just move on to the next
        LOG.info("SecureRandom " + algoName + " is not available");
      }
    }

    return null;
  }

  @Override
  public Class<?> getObjectType() {
    return SecureRandom.class;
  }

  public SdoProperties getProperties() {
    return properties;
  }

  @Autowired
  public void setProperties(SdoProperties properties) {
    this.properties = properties;
  }
}

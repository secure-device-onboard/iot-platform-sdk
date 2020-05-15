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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecureRandomFactory implements ObjectFactory<SecureRandom> {

  private static final Logger LOG = LoggerFactory.getLogger(SecureRandomFactory.class);
  private final List<String> secureRandomAlgorithms = List.of("NativePRNG", "NativePRNGBlocking",
      "NativePRNGNonBlocking", "PKCS11", "SHA1PRNG", "Windows_PRNG");
  private static SecureRandom secureRandom;

  @Override
  public SecureRandom getObject() {
    if (null == secureRandom) {
      initializeObject();
    }
    return secureRandom;
  }

  private void initializeObject() {
    for (String algo : secureRandomAlgorithms) {
      final String algoName = algo.toString().replaceAll("_", "-");
      try {
        secureRandom = SecureRandom.getInstance(algoName);
        LOG.info("using SecureRandom " + secureRandom.getAlgorithm());
        break;
      } catch (NoSuchAlgorithmException e) {
        // provider not available? just move on to the next
        LOG.debug("SecureRandom " + algoName + " is not available");
      }
    }
  }
}

// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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

// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.security.CryptoLevel112;
import org.sdo.iotplatformsdk.common.protocol.types.DigestType;
import org.sdo.iotplatformsdk.common.protocol.types.KeyExchangeType;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;

class CryptoLevel112Test {

  CryptoLevel112 cryptoLevel;
  PublicKey pk;

  @BeforeEach
  void beforeEach() {

    cryptoLevel = new CryptoLevel112();
    pk = Mockito.mock(PublicKey.class);
  }

  @Test
  void test_Encoder() throws IOException, InvalidKeyException, NoSuchAlgorithmException {

    cryptoLevel.getDigestService();
    cryptoLevel.getKeyExchangeType(pk);
    cryptoLevel.getMacService();
    cryptoLevel.getSekDerivationFunction();
    cryptoLevel.getSvkDerivationFunction();
    cryptoLevel.version();
    cryptoLevel.hasType(DigestType.SHA256);
    cryptoLevel.hasType(KeyExchangeType.ASYMKEX);
    cryptoLevel.hasType(MacType.HMAC_SHA256);


  }

}

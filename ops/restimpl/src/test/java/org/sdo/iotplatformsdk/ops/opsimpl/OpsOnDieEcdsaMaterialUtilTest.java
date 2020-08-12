// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.opsimpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class OpsOnDieEcdsaMaterialUtilTest extends TestCase {

  private OpsOnDieEcdsaMaterialUtil opsOnDieEcdsaMaterialUtil;
  private byte[] crl;

  @Override
  @Before
  public void setUp() throws IOException, CertificateException, CRLException {
    Path testResource = Paths.get("src/test/resources/test-ondie-ecdsa-material");
    opsOnDieEcdsaMaterialUtil =
        new OpsOnDieEcdsaMaterialUtil(testResource.toFile().getAbsolutePath().toString(),
            "https://tsci.intel.com/content/OnDieCA/crls/", false);
    crl = Files.readAllBytes(Paths.get(testResource.toString(), "ATS_00002102_OnDie_CA.crl"));
  }

  @Test
  public void testGetCrl() throws CertificateException {
    assertTrue(Arrays.equals(crl, opsOnDieEcdsaMaterialUtil
        .getCrl("https://pre1-tsci.intel.com/content/OD/crls/ATS_00002102_OnDie_CA.crl")));
  }

}

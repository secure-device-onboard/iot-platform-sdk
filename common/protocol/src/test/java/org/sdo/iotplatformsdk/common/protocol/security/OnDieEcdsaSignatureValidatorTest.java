// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class OnDieEcdsaSignatureValidatorTest {

  private String serialNo = "daltest";
  private String b64TestSignature =
      "ARDiywa9EaMjQZ0dNWO4CbxGEL0vujai1k2rk5D/baL+8xwBsQ4ZF/eL0V/yxtaafl11BJZ7rjnesm"
          + "/H8i6Hq3r8DeObqqGDo88mVnibvb9z3zlYlLahzLkwkhxsoTRRzXIQ6km2Dm6hQX5zmRkUDiFtzadw"
          + "MDfh+dPVQMlf/vNG1j5K";
  private String b64DeviceCert =
      "MIIBszCCATqgAwIBAgIQcYhLQDPbPylyGiZ0lFRLwzAKBggqhkjOPQQDAzAeMRwwGgYDVQQDDBNDU0"
          + "1FIFRHTCBEQUxfSTAxU0RFMB4XDTE5MDEwMTAwMDAwMFoXDTQ5MTIzMTIzNTk1OVowFzEVMBMGA1UE"
          + "AwwMREFMIEludGVsIFRBMHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE044GJ2MiK44UHXubptTvkGefiy"
          + "rKO9ofn5v1yBVJcwpbYYTBjop/W01f7Gv7se7sMin8D1zfoEIQuahlijcsVWlG0CcB6LodLkxQi+IS"
          + "D8MNbObYIt8EGIacVVOgPdSho0QwQjAfBgNVHSMEGDAWgBSuPjAqQWKsFmeOf7U8OWyMbE+tfTAPBg"
          + "NVHRMBAf8EBTADAQEAMA4GA1UdDwEB/wQEAwIDyDAKBggqhkjOPQQDAwNnADBkAjAdss2kczBguN6s"
          + "iidupV+ipN8bCVAYe3eZV7c3i9rhTpHipVdII1/ppdswzl2IXQ0CMHNeOFuvHe64S9m2JRbBXUSdJ7"
          + "iNQwp/4+OdQUmWYs2mB7KqZpmDPGQkq5mDuygBaA==";

  @Mock
  private OnDieEcdsaMaterialUtil onDieEcdsaMaterialUtil;

  private OnDieEcdsaSignatureValidator onDieEcdsaSignatureValidator;

  @BeforeEach
  void beforeEach() {
    onDieEcdsaMaterialUtil = Mockito.mock(OnDieEcdsaMaterialUtil.class);
    Mockito.when(onDieEcdsaMaterialUtil.getCrl(Mockito.anyString())).thenReturn(new byte[0]);
    onDieEcdsaSignatureValidator = new OnDieEcdsaSignatureValidator(onDieEcdsaMaterialUtil);
  }

  @Test
  public void testVerifySignatureValid() throws CertificateException, CertPathBuilderException,
      InvalidAlgorithmParameterException, NoSuchAlgorithmException {
    assertTrue(onDieEcdsaSignatureValidator.verifySignature(serialNo.getBytes(),
        Base64.getDecoder().decode(b64TestSignature), getCertPath()));
  }

  @Test
  public void testVerifySignatureInvalid() throws CertificateException {
    assertFalse(
        onDieEcdsaSignatureValidator.verifySignature(new String(serialNo + "invalid").getBytes(),
            Base64.getDecoder().decode(b64TestSignature), getCertPath()));
  }

  private CertPath getCertPath() throws CertificateException {
    byte[] certBytes = Base64.getDecoder().decode(b64DeviceCert);
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    Certificate cert = certificateFactory.generateCertificate(new ByteArrayInputStream(certBytes));
    List<Certificate> certList = new ArrayList<Certificate>();
    certList.add(cert);
    CertPath certPath = certificateFactory.generateCertPath(certList);
    return certPath;
  }
}

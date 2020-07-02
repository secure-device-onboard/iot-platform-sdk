// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security.pkix;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.PKIXRevocationChecker.Option;
import java.security.cert.TrustAnchor;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.slf4j.LoggerFactory;

/**
 * A wrapper for CertPathValidator.
 *
 * <p>Wraps a CertPathValidator to provide consistent initialization and a simpler interface.
 */
public class SdoCertPathValidator {

  private static final String PKIX = "PKIX";
  private static final String SUN = "SUN";
  private final CertStore certStore;
  private final Set<Option> revocationCheckerOptions;
  private final Set<TrustAnchor> trustAnchors;

  /**
   * Constructor.
   */
  public SdoCertPathValidator(CertStore certStore, // where we expect to find CRLs
      Set<Option> revocationCheckerOptions, Set<TrustAnchor> trustAnchors) {
    this.certStore = certStore;
    this.revocationCheckerOptions = Collections.unmodifiableSet(revocationCheckerOptions);
    this.trustAnchors = Collections.unmodifiableSet(trustAnchors);
  }

  /**
   * Constructor.
   */
  public PKIXCertPathValidatorResult validate(CertPath certPath) throws CertPathValidatorException,
      InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {

    CertPathValidator cpv = CertPathValidator.getInstance(PKIX, SUN);
    PKIXRevocationChecker rc = (PKIXRevocationChecker) cpv.getRevocationChecker();
    rc.setOptions(getRevocationCheckerOptions());
    PKIXParameters certPathParameters = new PKIXParameters(getTrustAnchors());
    certPathParameters.addCertPathChecker(rc);
    certPathParameters.addCertStore(getCertStore());
    certPathParameters.setRevocationEnabled(true);
    PKIXCertPathValidatorResult result;
    try {
      result = (PKIXCertPathValidatorResult) cpv.validate(certPath, certPathParameters);
    } finally {
      List<CertPathValidatorException> softErrs = rc.getSoftFailExceptions();
      LoggerFactory.getLogger(getClass()).info(softErrs.toString());
    }

    return result;
  }

  private CertStore getCertStore() {
    return certStore;
  }

  private Set<Option> getRevocationCheckerOptions() {
    return revocationCheckerOptions;
  }

  private Set<TrustAnchor> getTrustAnchors() {
    return trustAnchors;
  }
}

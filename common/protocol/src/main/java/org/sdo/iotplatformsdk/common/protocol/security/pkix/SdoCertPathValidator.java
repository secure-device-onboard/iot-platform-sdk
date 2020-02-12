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

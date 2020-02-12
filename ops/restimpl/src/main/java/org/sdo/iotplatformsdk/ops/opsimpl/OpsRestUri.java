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

package org.sdo.iotplatformsdk.ops.opsimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * It contains the getters for various paths, Each path represents a resource at the OCS.
 *
 * <p>When combined with the domain, these form end-points where multiple requests will be sent.
 */
public class OpsRestUri {

  @Value("${rest.api.device.state.path}")
  private String devStateUrl;

  @Value("${rest.api.voucher.path}")
  private String voucherUrl;

  @Value("${rest.api.serviceinfo.path}")
  private String serviceInfoUrl;

  @Value("${rest.api.serviceinfo.value.path}")
  private String serviceInfoValueUrl;

  @Value("${rest.api.psi.path}")
  private String psiUrl;

  @Value("${rest.api.error.path}")
  private String errorUrl;

  @Value("${rest.api.setupinfo.path}")
  private String setupInfoUrl;

  @Value("${rest.api.signature.path}")
  private String signatureUrl;

  @Value("${rest.api.ciphers.path}")
  private String asymParamUrl;

  @Value("${rest.api.session.path}")
  private String sessionUrl;

  protected static final Logger logger = LoggerFactory.getLogger(OpsRestUri.class);

  public OpsRestUri() {}

  /**
   * Returns the path to device state resource.
   *
   * @return path to state resource.
   */
  public String getDevStateUrl() {
    return devStateUrl;
  }

  /**
   * Returns the path to device voucher resource.
   *
   * @return path to voucher resource.
   */
  public String getVoucherUrl() {
    return voucherUrl;
  }

  /**
   * Returns the path to the msgs resource.
   *
   * @return path to error resource.
   */
  public String getServiceInfoUrl() {
    return serviceInfoUrl;
  }

  /**
   * Returns the path to values resource.
   *
   * @return path to values resource.
   */
  public String getServiceInfoValueUrl() {
    return serviceInfoValueUrl;
  }

  /**
   * Returns the path to device psi resource.
   *
   * @return path to psi resource.
   */
  public String getPsiUrl() {
    return psiUrl;
  }

  /**
   * Returns the path to device error resource.
   *
   * @return path to error resource.
   */
  public String getErrorUrl() {
    return errorUrl;
  }

  /**
   * Returns the path to setupinfo resource.
   *
   * @return path to setupinfo resource.
   */
  public String getSetupInfoUrl() {
    return setupInfoUrl;
  }

  /**
   * Returns the path to signatures resource.
   *
   * @return path to signatures resource.
   */
  public String getSignatureUrl() {
    return signatureUrl;
  }

  /**
   * Returns the path to cipher operation.
   *
   * @return path to cipher resource.
   */
  public String getAsymParamUrl() {
    return asymParamUrl;
  }

  /**
   * Returns the path to session store operation.
   *
   * @return path to cipher resource.
   */
  public String getSessionUrl() {
    return sessionUrl;
  }
}

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

package org.sdo.iotplatformsdk.to0scheduler.to0service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * It contains the getters for various paths, Each path represents a resource at
 * the OCS. When combined with the domain, these form end-points where multiple
 * requests will be sent.
 */
public class RestUri {

  @Value("${rest.api.device.state.path}")
  private String devStateUrl;

  @Value("${rest.api.voucher.path}")
  private String voucherUrl;

  @Value("${rest.api.error.path}")
  private String errorUrl;

  @Value("${rest.api.signature.path}")
  private String signatureUrl;

  protected static final Logger logger = LoggerFactory.getLogger(RestUri.class);

  public RestUri() {}

  /**
   * Returns the path to device state resource.
   *
   * @return path to state resource.
   */
  public String getDevStateUrl_() {
    return devStateUrl;
  }

  /**
   * Returns the path to device voucher resource.
   *
   * @return path to voucher resource.
   */
  public String getVoucherUrl_() {
    return voucherUrl;
  }

  /**
   * Returns the path to device error resource.
   *
   * @return path to error resource.
   */
  public String getErrorUrl_() {
    return errorUrl;
  }

  /**
   * Returns the path to signatures resource.
   *
   * @return path to signatures resource.
   */
  public String getSignatureUrl_() {
    return signatureUrl;
  }

}

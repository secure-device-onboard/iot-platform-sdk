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

package org.sdo.iotplatformsdk.common.rest;

import java.security.PublicKey;
import java.security.Signature;

/**
 * Class to manage signature response.
 *
 * <p>Represents a particular BASE-64 encoded signature along with the BASE-64 encoded public
 * key and its algorithm name, that could verify the signature.
 */
public class SignatureResponse {

  private String pk;
  private String alg;
  private String sg;

  public SignatureResponse() {}

  /**
   * Returns BASE-64 encoded string representation of {@link PublicKey}.
   *
   * @return public key.
   */
  public String getPk() {
    return pk;
  }

  /**
   * Stores BASE-64 encoded string representation of {@link PublicKey}.
   *
   * @param pk public key.
   */
  public void setPk(String pk) {
    this.pk = pk;
  }

  /**
   * Returns the algorithm name of {@link PublicKey}.
   *
   * @return algorithm.
   */
  public String getAlg() {
    return alg;
  }

  /**
   * Stores the algorithm name of {@link PublicKey}.
   *
   * @param alg algorithm
   */
  public void setAlg(String alg) {
    this.alg = alg;
  }

  /**
   * Returns BASE-64 encoded string representation of {@link Signature}.
   *
   * @return signature.
   */
  public String getSg() {
    return sg;
  }

  /**
   * Stores BASE-64 encoded string representation of {@link Signature}.
   *
   * @param sg signature
   */
  public void setSg(String sg) {
    this.sg = sg;
  }
}

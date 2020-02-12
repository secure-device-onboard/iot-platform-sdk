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

import java.util.List;

/**
 * Class to manage Ownership Voucher entries.
 */
public class OwnerVoucherEntryBodyObject {
  private List<Object> hp;
  private List<Object> hc;
  private List<Object> pk;

  public List<Object> getHp() {
    return hp;
  }

  public void setHp(List<Object> hp) {
    this.hp = hp;
  }

  public List<Object> getHc() {
    return hc;
  }

  public void setHc(List<Object> hc) {
    this.hc = hc;
  }

  public List<Object> getPk() {
    return pk;
  }

  public void setPk(List<Object> pk) {
    this.pk = pk;
  }

}

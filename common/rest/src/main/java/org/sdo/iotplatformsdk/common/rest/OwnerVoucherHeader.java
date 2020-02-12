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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

/**
 * Class to manage Ownership Voucher Header.
 */
public class OwnerVoucherHeader {

  private int pv;
  private int pe;
  private List<Object> rendezvousInfo;
  private String guid;
  private String deviceInfo;
  private List<Object> pk;
  private Optional<List<Object>> hdc;

  public OwnerVoucherHeader() {
    this.hdc = Optional.empty();
  }

  public int getPv() {
    return pv;
  }

  public void setPv(int pv) {
    this.pv = pv;
  }

  public int getPe() {
    return pe;
  }

  public void setPe(int pe) {
    this.pe = pe;
  }

  @JsonProperty("r")
  public List<Object> getR() {
    return rendezvousInfo;
  }

  public void setR(List<Object> r) {
    this.rendezvousInfo = r;
  }

  @JsonProperty("g")
  public String getG() {
    return guid;
  }

  public void setG(String g) {
    this.guid = g;
  }

  @JsonProperty("d")
  public String getD() {
    return deviceInfo;
  }

  public void setD(String d) {
    this.deviceInfo = d;
  }

  public List<Object> getPk() {
    return pk;
  }

  public void setPk(List<Object> pk) {
    this.pk = pk;
  }

  public Optional<List<Object>> getHdc() {
    return hdc;
  }

  public void setHdc(List<Object> hdc) {
    this.hdc = Optional.of(hdc);
  }
}

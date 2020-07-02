// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

import java.util.Optional;

/**
 * Class to manage Rendezvous Instructions. For more information on individual fields, refer to the
 * SDO protocol guide.
 */
public class RendezvousInstruction {

  private Optional<String> only;
  private Optional<String> ip;
  private Optional<Integer> po;
  private Optional<Integer> pow;
  private Optional<String> dn;
  private Optional<String> sch;
  private Optional<String> cch;
  private Optional<Integer> ui;
  private Optional<String> ss;
  private Optional<String> pw;
  private Optional<String> wsp;
  private Optional<String> me;
  private Optional<String> pr;
  private Optional<Integer> delaysec;

  public Optional<String> getOnly() {
    return only;
  }

  public void setOnly(String only) {
    this.only = Optional.of(only);
  }

  public Optional<String> getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = Optional.of(ip);
  }

  public Optional<Integer> getPo() {
    return po;
  }

  public void setPo(int po) {
    this.po = Optional.of(po);
  }

  public Optional<Integer> getPow() {
    return pow;
  }

  public void setPow(int pow) {
    this.pow = Optional.of(pow);
  }

  public Optional<String> getDn() {
    return dn;
  }

  public void setDn(String dn) {
    this.dn = Optional.of(dn);
  }

  public Optional<String> getSch() {
    return sch;
  }

  public void setSch(String sch) {
    this.sch = Optional.of(sch);
  }

  public Optional<String> getCch() {
    return cch;
  }

  public void setCch(String cch) {
    this.cch = Optional.of(cch);
  }

  public Optional<Integer> getUi() {
    return ui;
  }

  public void setUi(int ui) {
    this.ui = Optional.of(ui);
  }

  public Optional<String> getSs() {
    return ss;
  }

  public void setSs(String ss) {
    this.ss = Optional.of(ss);
  }

  public Optional<String> getPw() {
    return pw;
  }

  public void setPw(String pw) {
    this.pw = Optional.of(pw);
  }

  public Optional<String> getWsp() {
    return wsp;
  }

  public void setWsp(String wsp) {
    this.wsp = Optional.of(wsp);
  }

  public Optional<String> getMe() {
    return me;
  }

  public void setMe(String me) {
    this.me = Optional.of(me);
  }

  public Optional<String> getPr() {
    return pr;
  }

  public void setPr(String pr) {
    this.pr = Optional.of(pr);
  }

  public Optional<Integer> getDelaysec() {
    return delaysec;
  }

  public void setDelaySec(int delay) {
    this.delaysec = Optional.of(delay);
  }
}

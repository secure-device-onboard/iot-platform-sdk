// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

public class Message41Store {

  private String n6;
  private String kx;
  private String ownershipProxy;
  private String cs;
  private String kxEcdhPublicKey;
  private String kxEcdhPrivateKey;
  private String kxEcdhRandom;
  private String kxDhPublicKey;
  private String kxDhPrivateKey;
  private String asymRandom;

  public Message41Store() {

  }

  /**
   * Constructor.
   */
  public Message41Store(String n6, String kx, String ownershipProxy, String cs,
      String kxEcdhPublicKey, String kxEcdhPrivateKey, String kxEcdhRandom, String kxDhPublicKey,
      String kxDhPrivateKey, String asymRandom) {
    this.n6 = n6;
    this.kx = kx;
    this.ownershipProxy = ownershipProxy;
    this.cs = cs;
    this.kxEcdhPublicKey = kxEcdhPublicKey;
    this.kxEcdhPrivateKey = kxEcdhPrivateKey;
    this.kxEcdhRandom = kxEcdhRandom;
    this.kxDhPublicKey = kxDhPublicKey;
    this.kxDhPrivateKey = kxDhPrivateKey;
    this.asymRandom = asymRandom;
  }

  public String getN6() {
    return n6;
  }

  public void setN6(String n6) {
    this.n6 = n6;
  }

  public String getKx() {
    return kx;
  }

  public void setKx(String kx) {
    this.kx = kx;
  }

  public String getOwnershipProxy() {
    return ownershipProxy;
  }

  public void setOwnershipProxy(String ownershipProxy) {
    this.ownershipProxy = ownershipProxy;
  }

  public String getCs() {
    return cs;
  }

  public void setCs(String cs) {
    this.cs = cs;
  }

  public String getKxEcdhPublicKey() {
    return kxEcdhPublicKey;
  }

  public void setKxEcdhPublicKey(String kxEcdhPublicKey) {
    this.kxEcdhPublicKey = kxEcdhPublicKey;
  }

  public String getKxEcdhPrivateKey() {
    return kxEcdhPrivateKey;
  }

  public void setKxEcdhPrivateKey(String kxEcdhPrivateKey) {
    this.kxEcdhPrivateKey = kxEcdhPrivateKey;
  }

  public String getKxEcdhRandom() {
    return kxEcdhRandom;
  }

  public void setKxEcdhRandom(String kxEcdhRandom) {
    this.kxEcdhRandom = kxEcdhRandom;
  }

  public String getKxDhPublicKey() {
    return kxDhPublicKey;
  }

  public void setKxDhPublicKey(String kxDhPublicKey) {
    this.kxDhPublicKey = kxDhPublicKey;
  }

  public String getKxDhPrivateKey() {
    return kxDhPrivateKey;
  }

  public void setKxDhPrivateKey(String kxDhPrivateKey) {
    this.kxDhPrivateKey = kxDhPrivateKey;
  }

  public String getAsymRandom() {
    return asymRandom;
  }

  public void setAsymRandom(String asymRandom) {
    this.asymRandom = asymRandom;
  }
}

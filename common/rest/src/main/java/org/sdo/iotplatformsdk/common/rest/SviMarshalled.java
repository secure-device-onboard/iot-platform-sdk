// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

import java.util.Map;

public class SviMarshalled {

  private Map<Integer, String> marshalledServiceInfo;

  public SviMarshalled() {}

  public SviMarshalled(Map<Integer, String> marshalledServiceInfo) {
    this.marshalledServiceInfo = marshalledServiceInfo;
  }

  public Map<Integer, String> getMarshalledServiceInfo() {
    return marshalledServiceInfo;
  }

  public void setMarshalledServiceInfo(Map<Integer, String> marshalledServiceInfo) {
    this.marshalledServiceInfo = marshalledServiceInfo;
  }

}

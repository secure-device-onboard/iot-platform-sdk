// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.rest;

import org.sdo.iotplatformsdk.common.protocol.types.Version;

/**
 * Return the configured SDO REST path.
 */
public abstract class SdoUriComponentsBuilder {

  /**
   * Returns the SDO REST path for the particular message ID.
   *
   * @param id number of Protocol message
   * @return   path as String
   */
  public static String path(Integer id) {
    return "/mp/" + Version.VERSION_1_13 + "/msg/" + id.intValue();
  }
}

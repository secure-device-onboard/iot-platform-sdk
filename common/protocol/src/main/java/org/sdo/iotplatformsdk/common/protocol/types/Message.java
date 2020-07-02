// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

/**
 * The common interface for all SDO protocol messages.
 */
public interface Message {

  /**
   * Gets the message's version.
   *
   * <p>The message's version is the version of the SDO protocol in which it is defined.
   */
  Version getVersion();

  /**
   * Gets the message's type code.
   *
   * <p>All SDO messages have a unique type ID assigned.
   */
  MessageType getType();
}

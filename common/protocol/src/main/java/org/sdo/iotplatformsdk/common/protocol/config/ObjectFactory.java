// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.config;

/**
 * Implement this to return an instance of Type T.
 *
 * @param <T> type of instance to create and return
 */
@FunctionalInterface
public interface ObjectFactory<T> {

  /**
   * Create and return and instance of type T.
   *
   * @return instance of T
   * @throws Exception thrown when any exception occurs
   */
  public T getObject();
}

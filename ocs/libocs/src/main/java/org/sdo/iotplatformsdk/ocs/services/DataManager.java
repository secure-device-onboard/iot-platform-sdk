// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ocs.services;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface to be implemented to store and retrieve the resources.
 *
 */
public interface DataManager {

  /**
   * Returns a {@link DataObject} for the specified key.
   *
   * @param key the resource identifier.
   * @return object containing the resource.
   * @throws IOException when resource is not found, or there is an error while retrieving the
   *                       resource.
   */
  DataObject getObject(String key) throws IOException;

  /**
   * Stores the specified input stream for the given resource identifier.
   *
   * @param key   the resource identifier.
   * @param input input stream of the resource to be stored.
   * @throws IOException when there is an error while storing the resource.
   */
  void putObject(String key, InputStream input) throws IOException;

  /**
   * Removes a {@link DataObject} for the specified key.
   *
   * @param key the resource identifier.
   * @throws IOException when resource is not found, or there is an error while retrieving the
   *                       resource.
   */
  void removeObject(String key) throws IOException;
}

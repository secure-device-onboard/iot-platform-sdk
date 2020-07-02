// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ocs.services;

import java.io.IOException;
import java.io.InputStream;

/**
 * Representation of the resource as an object. The resource may be a file,
 * database entry, or http resource.
 */
public interface DataObject {

  /**
   * Returns the length of the contained resource.
   *
   * @return resource length of the resource.
   * @throws IOException when an error occurs while reading the length.
   */
  long getContentLength() throws IOException;

  /**
   * Returns an {@link InputStream} representing the contents of the resource.
   *
   * @return contents of the resource as stream.
   * @throws IOException when an error occurs while reading the resource.
   */
  InputStream getInputStream() throws IOException;

  /**
   * Returns {@link DataObject} containing the resource. The contents are ranged
   * from the start to the end indexes.
   *
   * @param start index at which data will be read from.
   * @param end   index at which dat will be read to.
   * @return ranged resource object.
   * @throws IOException when an error occurs while reading the resource.
   */
  DataObject withRange(int start, int end) throws IOException;
}

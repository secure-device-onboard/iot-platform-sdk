// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ocs.fsimpl.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.sdo.iotplatformsdk.ocs.services.DataObject;

/**
 * Implements the DataObject for file-system based operations.
 */
public class FsDataObject implements DataObject {

  // resource identified by the file.
  private final File file;

  // file contents.
  private InputStream inStream;

  public FsDataObject(File file) {
    this.file = file;
    inStream = null;
  }

  /*
   * Returns the length of the file.
   */
  @Override
  public long getContentLength() throws IOException {
    return getFile().length();
  }

  /*
   * Returns a stream of file contents.
   */
  @Override
  public InputStream getInputStream() throws IOException {
    if (inStream == null) {
      inStream = newInputStream();
    }
    return inStream;
  }

  protected File getFile() {
    return file;
  }

  protected InputStream newInputStream() throws IOException {
    return new FileInputStream(getFile());
  }

  /*
   * Returns a ranged DataObject.
   */
  @Override
  public DataObject withRange(int start, int end) throws IOException {
    return new FsDataRangeObject(getFile(), start, end);
  }

}

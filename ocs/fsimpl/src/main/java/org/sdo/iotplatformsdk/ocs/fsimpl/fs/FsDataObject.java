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

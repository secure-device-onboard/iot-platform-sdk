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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a ranged FsDataObject.
 *
 */
public class FsDataRangeObject extends FsDataObject {
  private final int start;
  private final int end;

  /**
   * Constructor.
   *
   * @param file  file instance.
   * @param start start index.
   * @param end   end index.
   */
  public FsDataRangeObject(File file, int start, int end) {
    super(file);
    this.start = start;
    this.end = end;
  }

  private int getStart() {
    return start;
  }

  private int getEnd() {
    return end;
  }

  /*
   * Returns the length of the file from start to end index.
   */
  @Override
  public long getContentLength() {
    long left = getFile().length() - getStart();
    return Math.min(left, getEnd() - getStart());
  }

  /*
   * Returns stream based on the specified start and end indexes.
   */
  @Override
  protected InputStream newInputStream() throws IOException {
    try (InputStream stream = new FileInputStream(getFile())) {
      stream.skip(getStart());
      byte[] buff = new byte[((Long) getContentLength()).intValue()];
      stream.read(buff);
      return new ByteArrayInputStream(buff);
    }
  }
}

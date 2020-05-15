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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.sdo.iotplatformsdk.ocs.services.DataManager;
import org.sdo.iotplatformsdk.ocs.services.DataObject;

/**
 * Implements the DataManager interface to store and retrieve files.
 */
public class FsDataManager implements DataManager {

  // path to file system root directory.
  private static final String rootDir = FsPropertiesLoader.getProperty("fs.root.dir");

  /*
   * Reads the specified file and returns the DataObject.
   */
  @Override
  public DataObject getObject(String key) throws IOException {

    File file = new File(rootDir, key);
    if (!file.exists()) {
      throw new FileNotFoundException(file.toString());
    }

    return new FsDataObject(file);
  }

  /*
   * Stores the stream into the file.
   */
  @Override
  public void putObject(String key, InputStream input) throws IOException {
    File file = new File(rootDir, key);

    Files.createDirectories(file.getParentFile().toPath());

    try (OutputStream output = Files.newOutputStream(file.toPath())) {
      int data = input.read();
      while (data != -1) {
        output.write(data);
        data = input.read();
      }
    }
  }

  /*
   * Deletes the file with the given key name.
   */
  @Override
  public void removeObject(String key) throws IOException {
    File file = new File(rootDir, key);
    if (!file.isDirectory()) {
      file.delete();
    } else {
      Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile)
          .forEach(File::delete);
    }
  }

}

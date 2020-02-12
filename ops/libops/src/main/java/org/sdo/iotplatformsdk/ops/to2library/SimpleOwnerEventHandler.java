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

package org.sdo.iotplatformsdk.ops.to2library;

import java.io.IOException;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.Set;

import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec.OwnershipProxyEncoder;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleOwnerEventHandler implements OwnerEventHandler {

  private static final Logger LOG = LoggerFactory.getLogger(SimpleOwnerEventHandler.class);
  private Path outputDir = Paths.get(System.getProperty("java.io.tmpdir"));

  @Override
  public void call(OwnerEvent event) {

    if (event instanceof To2EndEvent) {
      call((To2EndEvent) event);
    } else if (event instanceof To2ErrorEvent) {
      call((To2ErrorEvent) event);
    }
  }

  protected void call(To2ErrorEvent errorEvent) {
    LOG.error("Device TO2 error (" + errorEvent.getOwnershipProxy().getOh().getG() + "): "
        + errorEvent.getError().toString());
  }

  protected void call(To2EndEvent to2EndEvent) {
    Path outDir = getOutputDir();

    OwnershipProxy newProxy = to2EndEvent.getNewOwnershipProxy();

    if (!outDir.toFile().isDirectory()) {
      Set<PosixFilePermission> dirAttributes = new HashSet<>();
      dirAttributes.add(PosixFilePermission.OWNER_WRITE);
      dirAttributes.add(PosixFilePermission.OWNER_READ);
      dirAttributes.add(PosixFilePermission.OWNER_EXECUTE);

      try {
        Files.createDirectories(outDir, PosixFilePermissions.asFileAttribute(dirAttributes));

      } catch (IOException e) {
        LOG.error(e.getMessage());
        LOG.debug(e.getMessage(), e);
      }
    }

    Path path = outDir.resolve(newProxy.getOh().getG() + ".op");

    Set<OpenOption> openOptions = new HashSet<>();

    openOptions.add(StandardOpenOption.CREATE);
    openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
    openOptions.add(StandardOpenOption.WRITE);

    try (FileChannel channel = FileChannel.open(path, openOptions)) {

      try (Writer writer = Channels.newWriter(channel, "US-ASCII")) {
        new OwnershipProxyEncoder().encode(writer, newProxy);
        LOG.info("proxy saved to " + path.normalize().toAbsolutePath());
      }

    } catch (IOException e) {
      LOG.warn("can't write proxy to " + path.normalize().toAbsolutePath() + ": " + e.getMessage());
      LOG.debug(e.getMessage(), e);
    }
  }


  public Path getOutputDir() {
    return outputDir;
  }

  public void setOutputDir(Path outputDir) {
    this.outputDir = outputDir;
  }
}

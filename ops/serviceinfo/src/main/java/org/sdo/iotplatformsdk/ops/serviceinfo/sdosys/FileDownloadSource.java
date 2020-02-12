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

package org.sdo.iotplatformsdk.ops.serviceinfo.sdosys;

import java.nio.file.Path;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfoEntry;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoMultiSource;

/**
 * Source (issuer) sdo_sys:(filedesc|write) instructions.
 *
 * <p>sdo_sys:filedesc sets the filename to be written to on the receiving end.
 *
 * <p>sdo_sys:write contains a block of data to be written to the receiving file.
 *
 * <p>Data is encoded as base64-strings.
 */
public class FileDownloadSource implements ServiceInfoMultiSource {

  private final Path localpath;
  private final Path remotepath;

  private FileDownloadSource(Path local, Path remote) { // use Builder to create instances
    this.localpath = local;
    this.remotepath = remote;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public List<ServiceInfoEntry> getServiceInfo(UUID uuid) {
    final List<ServiceInfoEntry> list = new LinkedList<>();

    final Base64.Encoder b64e = Base64.getEncoder();
    final String b64 = b64e.encodeToString(getRemote().toString().getBytes(SdoSys.CHARSET));
    ServiceInfoEntry entry = new ServiceInfoEntry(SdoSys.KEY_FILEDESC, b64);
    list.add(entry);

    entry = new ServiceInfoEntry(SdoSys.KEY_WRITE, new FileBase64Sequence(getLocal()));
    list.add(entry);

    return list;
  }

  private Path getLocal() {
    return localpath;
  }

  private Path getRemote() {
    return remotepath;
  }

  public static class Builder {

    private Path localname = null;
    private Path remotename = null;

    /**
     * Construct a new builder.
     */
    public FileDownloadSource build() {
      if (null == localname || null == remotename) {
        throw new IllegalStateException();
      }
      return new FileDownloadSource(localname, remotename);
    }

    public Builder local(Path value) {
      localname = value;
      return this;
    }

    public Builder remote(Path value) {
      remotename = value;
      return this;
    }
  }
}

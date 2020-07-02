// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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

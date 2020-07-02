// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.serviceinfo.sdosys;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfoEntry;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoMultiSource;

/**
 * The SDO ServiceInfo System Module, owner-side.
 */
public final class SdoSysModuleOwner implements ServiceInfoMultiSource {

  private final List<ServiceInfoMultiSource> sources = new LinkedList<>();

  @Override
  public List<ServiceInfoEntry> getServiceInfo(UUID uuid) {

    List<ServiceInfoEntry> entries = new LinkedList<>();
    for (ServiceInfoMultiSource source : getSources()) {
      entries.addAll(source.getServiceInfo(uuid));
    }
    return entries;
  }

  /**
   * Add an {@link ExecSource} to the list of sources in this module.
   *
   * <p>Multiple sdo_sys:exec commands can be installed by installing multiple ExecSources.
   *
   * @param source The ExecSource providing the command data.
   */
  public void install(ExecSource source) {
    getSources().add(source);
  }

  /**
   * Add a {@link FileDownloadSource} to the list of sources in this module.
   *
   * <p>Multiple sdo_sys:(filedesc|write) commands can be installed by installing
   * multiple FileDownloadSources.
   *
   * @param source The FileDownloadSource providing the command data.
   */
  public void install(FileDownloadSource source) {
    getSources().add(source);
  }

  private List<ServiceInfoMultiSource> getSources() {
    return sources;
  }
}

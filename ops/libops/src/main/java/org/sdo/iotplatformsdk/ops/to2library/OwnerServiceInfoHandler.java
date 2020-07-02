// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.to2library;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import org.sdo.iotplatformsdk.common.protocol.codecs.ServiceInfoCodec;
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfo;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoMarshaller;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoModule;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoMultiSource;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoSource;

public class OwnerServiceInfoHandler {

  private Set<ServiceInfoModule> serviceInfoModules = new HashSet<>();
  private UUID voucherGuid;

  public OwnerServiceInfoHandler(final Set<ServiceInfoModule> serviceInfoModules, final UUID uuid) {
    this.serviceInfoModules = serviceInfoModules;
    this.voucherGuid = uuid;
  }

  /**
   * Return the total number of sreviceinfo entries.
   */
  public int getOwnerServiceInfoEntryCount() {
    final Iterable<Supplier<ServiceInfo>> serviceInfos = prepareMarshaller();
    int osinn = 0;
    for (final Iterator<Supplier<ServiceInfo>> it = serviceInfos.iterator(); it.hasNext();) {
      it.next().get();
      ++osinn;
    }
    return osinn;
  }

  /**
   * Return the next owner serviceinfo entry as per the 'nn' value.
   */
  public String getNextOwnerServiceInfoEntry(final int nn) throws IOException {

    final Map<Integer, String> marshalledServiceInfo = new HashMap<Integer, String>();
    final Iterable<Supplier<ServiceInfo>> serviceInfos = prepareMarshaller();
    int osinn = 0;
    for (final Iterator<Supplier<ServiceInfo>> it = serviceInfos.iterator(); it.hasNext();) {
      ServiceInfo sv = it.next().get();
      if (osinn == nn) {
        StringWriter serviceInfoWriter = new StringWriter();
        new ServiceInfoCodec().encoder().apply(serviceInfoWriter, sv);
        marshalledServiceInfo.put(osinn, serviceInfoWriter.toString());
        break;
      }
      ++osinn;
    }

    final String nnBasedMarshalledSvi = marshalledServiceInfo.get(nn);
    return nnBasedMarshalledSvi;
  }

  private Iterable<Supplier<ServiceInfo>> prepareMarshaller() {
    final ServiceInfoMarshaller marshaller = new ServiceInfoMarshaller();
    final List<ServiceInfoSource> serviceInfoSources = new ArrayList<>();
    final List<ServiceInfoMultiSource> serviceInfoMultiSources = new ArrayList<>();

    for (Object serviceInfoObject : getServiceInfoModules()) {

      if (serviceInfoObject instanceof ServiceInfoSource) {
        serviceInfoSources.add((ServiceInfoSource) serviceInfoObject);

      } else if (serviceInfoObject instanceof ServiceInfoMultiSource) {
        serviceInfoMultiSources.add((ServiceInfoMultiSource) serviceInfoObject);

      }
    }

    marshaller.setSources(serviceInfoSources);
    marshaller.setMultiSources(serviceInfoMultiSources);
    final Iterable<Supplier<ServiceInfo>> serviceInfos = marshaller.marshal(this.voucherGuid);
    return serviceInfos;
  }

  private Set<ServiceInfoModule> getServiceInfoModules() {
    return Objects.requireNonNull(serviceInfoModules);
  }
}

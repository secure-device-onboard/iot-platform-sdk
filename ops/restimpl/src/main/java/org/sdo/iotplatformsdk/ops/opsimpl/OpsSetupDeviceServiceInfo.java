// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.opsimpl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.sdo.iotplatformsdk.common.protocol.types.RendezvousInfo;
import org.sdo.iotplatformsdk.common.protocol.types.RendezvousInstr;
import org.sdo.iotplatformsdk.common.rest.RendezvousInstruction;
import org.sdo.iotplatformsdk.common.rest.SetupInfoResponse;
import org.sdo.iotplatformsdk.ops.rest.RestClient;
import org.sdo.iotplatformsdk.ops.to2library.SetupDeviceService;

public class OpsSetupDeviceServiceInfo implements SetupDeviceService {

  private final RestClient restClient;

  public OpsSetupDeviceServiceInfo(RestClient restClient) {
    this.restClient = restClient;
  }

  /*
   * Send a request to retrieve the new device identifier and the RendezvousInstruction. Create the
   * RendezvousInfo from the received contents of RendezvousInstruction, and return these two
   * information.
   */
  @Override
  public Setup setup(final UUID g2, final RendezvousInfo r2) {
    final SetupInfoResponse setupInfo = restClient.getSetupInfo(g2.toString());

    final RendezvousInfo info = new RendezvousInfo();

    final List<RendezvousInstruction> r3List = setupInfo.getR3();
    if (null != r3List && r3List.size() > 0) {
      for (RendezvousInstruction ri : setupInfo.getR3()) {

        RendezvousInstr newInstr = new RendezvousInstr();

        if (ri.getIp().isPresent()) {
          try {
            newInstr.setIp(InetAddress.getByName(ri.getIp().get()));
          } catch (UnknownHostException ex) {
            newInstr.setDn(ri.getIp().get());
          }
        }

        if (ri.getDn().isPresent()) {
          newInstr.setDn(ri.getDn().get());
        }

        if (ri.getOnly().isPresent()) {
          newInstr.setOnly(RendezvousInstr.Only.valueOf(ri.getOnly().get()));
        }

        if (ri.getPo().isPresent()) {
          newInstr.setPo(ri.getPo().get());
        }

        if (ri.getPow().isPresent()) {
          newInstr.setPow(ri.getPow().get());
        }

        if (ri.getPr().isPresent()) {
          newInstr.setPr(RendezvousInstr.Protocol.valueOfName(ri.getPr().get()));
        }

        if (ri.getDelaysec().isPresent()) {
          newInstr.setDelay(Duration.ofSeconds(ri.getDelaysec().get()));
        }
        info.add(newInstr);
      }
    } else {
      info.addAll(r2.subList(0, r2.size()));
    }

    return new Setup() {
      @Override
      public UUID g3() {
        return UUID.fromString(setupInfo.getG3());
      }

      @Override
      public RendezvousInfo r3() {
        return info;
      }
    };
  }

}

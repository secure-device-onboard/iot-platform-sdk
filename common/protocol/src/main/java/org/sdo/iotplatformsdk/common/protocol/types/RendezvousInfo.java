// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.util.ArrayList;

/**
 * SDO "RendezvousInfo" type.
 *
 * <p>A rendezvous info block is an ordered list of rendezvous instructions.
 */
@SuppressWarnings("serial")
public class RendezvousInfo extends ArrayList<RendezvousInstr> {

  public RendezvousInfo() {
    super();
  }

  public RendezvousInfo(RendezvousInfo other) {
    super(other);
  }
}

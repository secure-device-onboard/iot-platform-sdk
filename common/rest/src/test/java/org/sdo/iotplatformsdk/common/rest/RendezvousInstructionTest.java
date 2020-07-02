// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.rest.RendezvousInstruction;

class RendezvousInstructionTest {

  RendezvousInstruction rendezvousInstruction;

  @BeforeEach
  void beforeEach() {
    rendezvousInstruction = new RendezvousInstruction();
  }

  @Test
  void test_Bean() throws IOException {
    rendezvousInstruction.setCch("Test");
    rendezvousInstruction.setDelaySec(100);
    rendezvousInstruction.setDn("Test");
    rendezvousInstruction.setIp("127.0.0.1");
    rendezvousInstruction.setMe("Test");
    rendezvousInstruction.setOnly("Test");
    rendezvousInstruction.setPo(100);
    rendezvousInstruction.setPow(100);
    rendezvousInstruction.setPr("Test");
    rendezvousInstruction.setPw("Test");
    rendezvousInstruction.setSch("Test");
    rendezvousInstruction.setSs("Test");
    rendezvousInstruction.setUi(100);
    rendezvousInstruction.setWsp("Test");

    rendezvousInstruction.getCch();
    rendezvousInstruction.getDelaysec();
    rendezvousInstruction.getDn();
    rendezvousInstruction.getIp();
    rendezvousInstruction.getMe();
    rendezvousInstruction.getOnly();
    rendezvousInstruction.getPo();
    rendezvousInstruction.getPow();
    rendezvousInstruction.getPr();
    rendezvousInstruction.getPw();
    rendezvousInstruction.getSch();
    rendezvousInstruction.getSs();
    rendezvousInstruction.getUi();
    rendezvousInstruction.getWsp();

  }

}


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


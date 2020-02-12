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

package org.sdo.iotplatformsdk.common.protocol.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.HashDigest;
import org.sdo.iotplatformsdk.common.protocol.types.KeyEncoding;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxyHeader;
import org.sdo.iotplatformsdk.common.protocol.types.RendezvousInfo;

class OwnershipProxyHeaderTest {

  HashDigest hdc;
  KeyEncoding pe;
  KeyPairGenerator keyGen;
  KeyPair keypair;
  PublicKey publicKey;
  OwnershipProxyHeader ownershipProxyHeader;
  OwnershipProxyHeader ownershipProxyHeader1;
  RendezvousInfo rendezvousInfo;
  String deviceinfo;
  UUID guid;

  @BeforeEach
  void beforeEach() throws NoSuchAlgorithmException {

    deviceinfo = "Test";
    guid = UUID.randomUUID();
    hdc = new HashDigest();
    keyGen = KeyPairGenerator.getInstance("RSA");
    keyGen.initialize(1024);
    keypair = keyGen.genKeyPair();
    ownershipProxyHeader = new OwnershipProxyHeader();
    ownershipProxyHeader1 =
        new OwnershipProxyHeader(pe, rendezvousInfo, guid, deviceinfo, publicKey, hdc);
    pe = KeyEncoding.EPID;
    publicKey = keypair.getPublic();
    rendezvousInfo = new RendezvousInfo();
  }

  @Test
  void test_Bean() {

    ownershipProxyHeader.setD(deviceinfo);
    ownershipProxyHeader.setG(guid);
    ownershipProxyHeader.setHdc(hdc);
    ownershipProxyHeader.setPe(pe);
    ownershipProxyHeader.setPk(publicKey);
    ownershipProxyHeader.setR(rendezvousInfo);

    assertEquals(deviceinfo, ownershipProxyHeader.getD());
    assertEquals(guid, ownershipProxyHeader.getG());
    assertEquals(hdc, ownershipProxyHeader.getHdc());
    assertEquals(pe, ownershipProxyHeader.getPe());
    assertEquals(publicKey, ownershipProxyHeader.getPk());
    assertEquals(rendezvousInfo, ownershipProxyHeader.getR());
    assertEquals(deviceinfo, ownershipProxyHeader.getD());
    ownershipProxyHeader.getPv();
  }

}

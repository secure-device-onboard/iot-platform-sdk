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

package org.sdo.iotplatformsdk.common.protocol.config;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertPath;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.config.OwnershipProxyStorage;
import org.sdo.iotplatformsdk.common.protocol.config.SimpleOwnershipProxyStorageFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.types.HashMac;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxyHeader;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;

class SimpleOwnershipProxyStorageFactoryBeanTest {

  CertPath dc;
  HashMac hmac;
  List<SignatureBlock> en;
  OwnershipProxy ownershipProxy;
  OwnershipProxyHeader oh;
  OwnershipProxyStorage ownershipProxyStorage;
  Path path;
  SimpleOwnershipProxyStorageFactoryBean simpleOwnershipProxyStorageFactoryBean;

  @SuppressWarnings("unchecked")
  @BeforeEach
  void beforeEach() throws IOException {

    dc = Mockito.mock(CertPath.class);
    en = Mockito.mock(List.class);
    hmac = Mockito.mock(HashMac.class);
    oh = new OwnershipProxyHeader();
    ownershipProxy = new OwnershipProxy();
    path = Paths.get("C:\\Users\\dkparikh\\Documents\\Test");
    simpleOwnershipProxyStorageFactoryBean = new SimpleOwnershipProxyStorageFactoryBean();

    ownershipProxy.setDc(dc);
    ownershipProxy.setEn(en);
    ownershipProxy.setHmac(hmac);
    ownershipProxy.setOh(oh);
  }

  @Test
  void test_getObject() throws IOException {

    simpleOwnershipProxyStorageFactoryBean.setProxyDir(path);

    ownershipProxyStorage = simpleOwnershipProxyStorageFactoryBean.getObject();
    simpleOwnershipProxyStorageFactoryBean.getObjectType();
    simpleOwnershipProxyStorageFactoryBean.getProxyDir();
    simpleOwnershipProxyStorageFactoryBean.isSingleton();

    ownershipProxyStorage.store(ownershipProxy);
    ownershipProxyStorage.load(UUID.randomUUID());
  }
}

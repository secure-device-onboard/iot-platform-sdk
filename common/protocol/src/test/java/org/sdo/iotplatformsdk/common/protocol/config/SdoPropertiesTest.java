/*******************************************************************************
 * Copyright 2020 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/

package org.sdo.iotplatformsdk.common.protocol.config;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.cert.CertPathValidatorException.BasicReason;
import java.security.cert.PKIXRevocationChecker.Option;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.Device;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.Di;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.Di.AppStart;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.Epid;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.Manufacturer;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.Owner;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.Pkix;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.Pm;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.Pm.CredMfg;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.Pm.CredOwner;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.Pm.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.ProxyNew;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.SecureRandomAlgorithm;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.To0;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.To0.OwnerSign;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.To0.OwnerSign.To0d;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.To0.OwnerSign.To1d;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.To0.OwnerSign.To1d.Bo;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.To2;
import org.sdo.iotplatformsdk.common.protocol.types.CipherBlockMode;

class SdoPropertiesTest {

  AppStart appStart;
  Bo bo;
  CipherBlockMode cipherBlockMode;
  CredMfg credMfg;
  CredOwner credOwner;
  Device device;
  Di di;
  Duration ws;
  Epid epid;
  InetAddress i1;
  List<SecureRandomAlgorithm> secureRandom;
  Manufacturer manufacturer;
  Owner owner;
  OwnershipProxy ownershipProxy;
  OwnerSign ownerSign;
  Pkix pkix;
  Pm pm;
  ProxyNew proxyNew;
  SdoProperties sdoProperties;
  Set<BasicReason> acceptErrors;
  Set<URI> crls;
  Set<Option> revocationOptions;
  Set<URI> trustAnchors;
  To0 to0;
  To0d to0d;
  To1d to1d;
  To2 to2;
  URI uri;

  @SuppressWarnings("unchecked")
  @BeforeEach
  void beforeEach() throws UnknownHostException {

    acceptErrors = Mockito.mock(Set.class);
    cipherBlockMode = CipherBlockMode.CBC;
    crls = Mockito.mock(Set.class);
    revocationOptions = Mockito.mock(Set.class);
    trustAnchors = Mockito.mock(Set.class);
    secureRandom = new ArrayList<>();
    uri = URI.create("http://www.intel.com");
    ws = Duration.ofMillis(10000);

    appStart = new AppStart();
    bo = new Bo();
    credMfg = new CredMfg();
    credOwner = new CredOwner();
    device = new Device();
    di = new Di();
    epid = new Epid();
    manufacturer = new Manufacturer();
    i1 = InetAddress.getByName("www.intel.com");
    owner = new Owner();
    ownershipProxy = new OwnershipProxy();
    pkix = new Pkix();
    pm = new Pm();
    proxyNew = new ProxyNew();
    sdoProperties = new SdoProperties();
    to0 = new To0();
    to0d = new To0d();
    to1d = new To1d();
    to2 = new To2();

    secureRandom.add(SecureRandomAlgorithm.PKCS11);
    secureRandom.add(SecureRandomAlgorithm.SHA1PRNG);
  }

  @Test
  void test_Bean() throws IOException {

    /**
     * Testing bean functions for AppStart class
     */

    appStart.setM("Test");

    appStart.getM();

    /**
     * Testing bean functions for Bo class
     */

    bo.setDns1("127.0.0.1");
    bo.setI1(i1);
    bo.setPort1(4028);

    bo.getDns1();
    bo.getI1();
    bo.getPort1();

    /**
     * Testing bean functions for CredMfg class
     */

    credMfg.setCu(uri);
    credMfg.setD("Test");

    credMfg.getCu();
    credMfg.getD();

    /**
     * Testing bean functions for CredOwner class
     */

    credOwner.setG(UUID.randomUUID());
    credOwner.setR(null);

    credOwner.getG();
    credOwner.getR();

    /**
     * Testing bean functions for Device class
     */

    device.setCert(uri);
    device.setCredentials(uri);
    device.setKey(uri);
    device.setOutputDir("Test");
    device.setStopAfterDi(true);

    device.getCert();
    device.getCredentials();
    device.getKey();
    device.getOutputDir();
    device.isStopAfterDi();

    /**
     * Testing bean functions for DI class
     */

    di.setUri(uri);

    di.getAppStart();
    di.getUri();

    /**
     * Testing bean functions for EPID class
     */

    epid.setEpidOnlineUrl("http://www.intel.com");
    epid.setTestMode(false);

    epid.getOptions();

    /**
     * Testing bean functions for Manufacturer class
     */

    manufacturer.setOutputDir("Test");

    manufacturer.getOutputDir();

    /**
     * Testing bean functions for Owner class
     */

    owner.setCert(uri);
    owner.setKey(uri);
    owner.setOutputDir("Test");
    owner.setProxyDir("Test");

    owner.getCert();
    owner.getKey();
    owner.getOutputDir();
    owner.getProxyDir();

    /**
     * Testing bean functions for OwnershipProxy class
     */

    ownershipProxy.setDc(uri);

    ownershipProxy.getDc();

    /**
     * Testing bean functions for OwnerSign class
     */

    // ownerSign.getTo0d();
    // ownerSign.getTo1d();

    /**
     * Testing bean functions for Pkix class
     */

    pkix.setAcceptErrors(acceptErrors);
    // pkix.setCrls(crls);
    pkix.setRevocationOptions(revocationOptions);
    // pkix.setTrustAnchors(trustAnchors);

    pkix.getAcceptErrors();
    // pkix.getCrls();
    pkix.getRevocationOptions();
    // pkix.getTrustAnchors();

    /**
     * Testing bean functions for Pm class
     */

    pm.getCredMfg();
    pm.getCredOwner();
    pm.getOwnershipProxy();

    /**
     * Testing bean functions for ProxyNew class
     */

    proxyNew.setOc("Test");
    proxyNew.setOp("Test");

    proxyNew.getOc();
    proxyNew.getOp();

    /**
     * Testing bean functions for SdoProperties class
     */

    sdoProperties.setCryptoLevel("1.1");
    sdoProperties.setSecureRandom(secureRandom);

    sdoProperties.getCryptoLevel();
    sdoProperties.getDevice();
    sdoProperties.getDi();
    sdoProperties.getEpid();
    sdoProperties.getManufacturer();
    sdoProperties.getOwner();
    sdoProperties.getPkix();
    sdoProperties.getPm();
    sdoProperties.getProxyAdd();
    sdoProperties.getProxyNew();
    sdoProperties.getSecureRandom();
    sdoProperties.getTo0();
    sdoProperties.getTo2();

    /**
     * Testing bean functions for To0 class
     */

    to0.getOwnerSign();

    /**
     * Testing bean functions for To0d class
     */

    to0d.setWs(ws);

    to0d.getWs();

    /**
     * Testing bean functions for To1d class
     */

    to1d.getBo();

    /**
     * Testing bean functions for To2 class
     */

    to2.setCipherBlockMode(cipherBlockMode);

    to2.getCipherBlockMode();
  }
}

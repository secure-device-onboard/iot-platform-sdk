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

package org.sdo.iotplatformsdk.ops.opsimpl;

import java.util.List;
import java.util.UUID;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.types.PreServiceInfoEntry;
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfoEntry;
import org.sdo.iotplatformsdk.common.rest.MessageEncoding;
import org.sdo.iotplatformsdk.common.rest.ModuleMessage;
import org.sdo.iotplatformsdk.common.rest.SviMessage;
import org.sdo.iotplatformsdk.common.rest.SviMessageType;
import org.sdo.iotplatformsdk.ops.opsimpl.OpsServiceInfoModule;
import org.sdo.iotplatformsdk.ops.opsimpl.RestClient;

@RunWith(JUnit4.class)
public class OpsServiceInfoModuleTest extends TestCase {

  @Mock
  RestClient restClient;

  OpsServiceInfoModule opsServiceInfoModule;

  UUID uuid;

  @Override
  @Before
  public void setUp() {
    restClient = Mockito.mock(RestClient.class);
    uuid = UUID.fromString("89abdd4e-44cc-4fbb-8765-d8d2a1584df3");
  }

  @Test()
  public void testGetServiceInfo() {
    opsServiceInfoModule = new OpsServiceInfoModule();
    opsServiceInfoModule.setClient(restClient);

    SviMessage[] messages = new SviMessage[0];
    Mockito.when(restClient.getMessage(uuid.toString())).thenReturn(messages);

    List<ServiceInfoEntry> list = opsServiceInfoModule.getServiceInfo(uuid);
    Assert.assertEquals(list.size(), 0);

    messages = new SviMessage[1];

    Mockito.when(restClient.getMessage(uuid.toString())).thenReturn(messages);
    list = opsServiceInfoModule.getServiceInfo(uuid);
    Assert.assertEquals(list.size(), 0);

    SviMessage message = new SviMessage();
    message.setEnc(MessageEncoding.BASE64.toString());
    message.setModule(SviMessageType.EXEC.toString());
    message.setMsg("shFileExec");
    message.setValueId("shFileExecId");
    message.setValueLen(0);
    messages[0] = message;

    Mockito.when(restClient.getMessage(uuid.toString())).thenReturn(messages);
    list = opsServiceInfoModule.getServiceInfo(uuid);
    Assert.assertEquals(list.size(), 1);

  }

  @Test(expected = Exception.class)
  public void testGetServiceInfoEmptyMessage() throws Exception {

    opsServiceInfoModule = new OpsServiceInfoModule();
    opsServiceInfoModule.setClient(restClient);
    SviMessage[] messages = new SviMessage[1];

    SviMessage message = new SviMessage();
    messages[0] = message;
    Mockito.when(restClient.getMessage(uuid.toString())).thenReturn(messages);
    opsServiceInfoModule.getServiceInfo(uuid);
  }

  @Test(expected = RuntimeException.class)
  public void testGetServiceInfoInvalidEnc() throws Exception {

    opsServiceInfoModule = new OpsServiceInfoModule();
    opsServiceInfoModule.setClient(restClient);
    SviMessage[] messages = new SviMessage[1];

    SviMessage message = new SviMessage();
    message.setEnc("invalid_encoding");
    messages[0] = message;
    Mockito.when(restClient.getMessage(uuid.toString())).thenReturn(messages);
    opsServiceInfoModule.getServiceInfo(uuid);
  }

  @Test
  public void testGetPreServiceInfo() {
    opsServiceInfoModule = new OpsServiceInfoModule();
    opsServiceInfoModule.setClient(restClient);

    ModuleMessage[] messages = new ModuleMessage[0];
    Mockito.when(restClient.getPsi(uuid.toString())).thenReturn(messages);
    List<PreServiceInfoEntry> list = opsServiceInfoModule.getPreServiceInfo(uuid);
    Assert.assertEquals(list.size(), 0);

    messages = new ModuleMessage[1];
    ModuleMessage message = null;
    messages[0] = message;
    Mockito.when(restClient.getPsi(uuid.toString())).thenReturn(messages);
    list = opsServiceInfoModule.getPreServiceInfo(uuid);
    Assert.assertEquals(list.size(), 0);

    message = new ModuleMessage();
    messages[0] = message;
    Mockito.when(restClient.getPsi(uuid.toString())).thenReturn(messages);
    list = opsServiceInfoModule.getPreServiceInfo(uuid);
    Assert.assertEquals(list.size(), 1);

    message.setModule(SviMessageType.MODULE.toString());
    message.setMsg("active");
    message.setValue("1");
    messages[0] = message;
    Mockito.when(restClient.getPsi(uuid.toString())).thenReturn(messages);
    list = opsServiceInfoModule.getPreServiceInfo(uuid);
    Assert.assertEquals(list.size(), 1);

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testPutServiceInfo() {
    opsServiceInfoModule = new OpsServiceInfoModule();
    opsServiceInfoModule.setClient(restClient);
    ModuleMessage message = new ModuleMessage();
    message.setModule(SviMessageType.MODULE.toString());
    message.setMsg("active");
    message.setValue("1");
    ModuleMessage[] messages = new ModuleMessage[1];
    messages[0] = message;
    Mockito.doNothing().when(restClient).postMessage(Mockito.anyString(), Mockito.any(List.class));
    ServiceInfoEntry serviceInfoEntry = new ServiceInfoEntry("key", "value");
    ServiceInfo serviceInfo = new ServiceInfo();
    serviceInfo.add(serviceInfoEntry);
    opsServiceInfoModule.putServiceInfo(uuid, serviceInfo);
  }
}

// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.opsimpl;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.ops.rest.RestClient;

@RunWith(JUnit4.class)
public class OpsResaleSupportTest extends TestCase {

  @Mock
  RestClient restClient;
  OpsResaleSupport opsResaleSupport;

  @Override
  @Before
  public void setUp() {
    restClient = Mockito.mock(RestClient.class);
    opsResaleSupport = new OpsResaleSupport(restClient);
  }

  @Test()
  public void testOwnerResaleSupported() {
    Mockito.when(restClient.getOwnerResaleFlag(Mockito.anyString())).thenReturn(false);
    Assert.assertFalse(opsResaleSupport.ownerResaleSupported(Mockito.anyString()));
    Mockito.when(restClient.getOwnerResaleFlag(Mockito.anyString())).thenReturn(true);
    Assert.assertTrue(opsResaleSupport.ownerResaleSupported(Mockito.anyString()));
    Mockito.when(restClient.getOwnerResaleFlag(Mockito.anyString()))
        .thenThrow(new RuntimeException());
    Assert.assertTrue(opsResaleSupport.ownerResaleSupported(Mockito.anyString()));
  }
}

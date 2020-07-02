// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.rest.OwnerVoucherEntryBodyObject;

class OwnerVoucherEntryObjectTest {

  List<Object> hc;
  List<Object> hp;
  List<Object> pk;
  OwnerVoucherEntryBodyObject ownerVoucherEntryBodyObject;

  @SuppressWarnings("unchecked")
  @BeforeEach
  void beforeEach() {
    hc = Mockito.mock(List.class);
    hp = Mockito.mock(List.class);
    pk = Mockito.mock(List.class);
    ownerVoucherEntryBodyObject = new OwnerVoucherEntryBodyObject();
  }

  @Test
  void test_Bean() throws IOException {
    ownerVoucherEntryBodyObject.setHc(hc);;
    ownerVoucherEntryBodyObject.setHp(hp);
    ownerVoucherEntryBodyObject.setPk(pk);

    ownerVoucherEntryBodyObject.getHc();
    ownerVoucherEntryBodyObject.getHp();
    ownerVoucherEntryBodyObject.getPk();
  }

}


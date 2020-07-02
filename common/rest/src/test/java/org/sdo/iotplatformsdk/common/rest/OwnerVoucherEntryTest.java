// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.rest.OwnerVoucherEntry;
import org.sdo.iotplatformsdk.common.rest.OwnerVoucherEntryBodyObject;

class OwnerVoucherEntryTest {

  OwnerVoucherEntryBodyObject bo;
  OwnerVoucherEntry ownerVoucherEntry;
  List<Object> pk;
  List<Object> sg;

  @SuppressWarnings("unchecked")
  @BeforeEach
  void beforeEach() {
    bo = new OwnerVoucherEntryBodyObject();
    ownerVoucherEntry = new OwnerVoucherEntry();
    pk = Mockito.mock(List.class);
    sg = Mockito.mock(List.class);
  }

  @Test
  void test_Bean() throws IOException {
    ownerVoucherEntry.setBo(bo);
    ownerVoucherEntry.setPk(pk);
    ownerVoucherEntry.setSg(sg);

    ownerVoucherEntry.getBo();
    ownerVoucherEntry.getPk();
    ownerVoucherEntry.getSg();
  }

}


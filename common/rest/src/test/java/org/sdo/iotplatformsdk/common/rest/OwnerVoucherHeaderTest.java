// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.rest.OwnerVoucherHeader;

class OwnerVoucherHeaderTest {

  List<Object> hdc;
  List<Object> pk;
  List<Object> rendezvousInfo;
  OwnerVoucherHeader ownerVoucherHeader;

  @SuppressWarnings("unchecked")
  @BeforeEach
  void beforeEach() {
    hdc = Mockito.mock(List.class);
    ownerVoucherHeader = new OwnerVoucherHeader();
    pk = Mockito.mock(List.class);
    rendezvousInfo = Mockito.mock(List.class);
  }

  @Test
  void test_Bean() throws IOException {
    ownerVoucherHeader.setD("Test");
    ownerVoucherHeader.setG("Test");
    ownerVoucherHeader.setHdc(hdc);
    ownerVoucherHeader.setPe(100);
    ownerVoucherHeader.setPk(pk);
    ownerVoucherHeader.setPv(10);
    ownerVoucherHeader.setR(rendezvousInfo);

    ownerVoucherHeader.getD();
    ownerVoucherHeader.getG();
    ownerVoucherHeader.getHdc();
    ownerVoucherHeader.getPe();
    ownerVoucherHeader.getPk();
    ownerVoucherHeader.getPv();
    ownerVoucherHeader.getR();

  }

}

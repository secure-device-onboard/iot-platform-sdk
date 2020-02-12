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


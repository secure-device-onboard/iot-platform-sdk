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

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.HashDigest;

class HashDigestTest {

  HashDigest hashDigest;
  HashDigest hashDigest1;
  HashDigest hashDigest2;

  @BeforeEach
  void beforeEach() throws IOException {

    hashDigest = new HashDigest();
    // hashDigest1 = new HashDigest(CharBuffer.wrap("{}"));
    hashDigest2 = new HashDigest(hashDigest);
  }

  @Test
  void test_Bean() {

    hashDigest.getType();
    hashDigest.hashCode();
    hashDigest.toString();

    hashDigest.equals(hashDigest);
    hashDigest.equals(null);
    hashDigest.equals(hashDigest2);
  }

}

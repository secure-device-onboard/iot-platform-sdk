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

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;

@SuppressWarnings("serial")
public class EpidKey extends EncodedKeySpec implements PublicKey, PrivateKey {

  private static final byte[] EPID2_TEST_GROUP =
      {0x00, 0x00, 0x00, 0x0d, (byte) 0xdd, (byte) 0xdd, (byte) 0xcc, (byte) 0xcc, 0x00, 0x00, 0x00,
          0x00, (byte) 0xee, (byte) 0xee, (byte) 0xee, 0x05};

  public static final EpidKey EPID2_TEST_KEY = new EpidKey(EPID2_TEST_GROUP);

  public EpidKey(byte[] groupId) {
    super(groupId);
  }

  @Override
  public String getAlgorithm() {
    return getType().toString();
  }

  @Override
  public String getFormat() {
    return getType().toString();
  }

  public KeyType getType() {
    return KeyType.EPIDV2_0;
  }
}

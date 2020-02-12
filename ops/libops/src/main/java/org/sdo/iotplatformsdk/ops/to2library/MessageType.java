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

package org.sdo.iotplatformsdk.ops.to2library;

public enum MessageType {

  PM_CRED_OWNER(1),
  PM_CRED_MFG(2),
  PM_OWNERSHIP_PROXY(3),
  PM_PUBLIC_KEY(4),
  PM_SERVICE_INFO(5),
  PM_DEVICE_CREDENTIALS(6),
  DI_APP_START(10),
  DI_SET_CREDENTIALS(11),
  DI_SET_HMAC(12),
  DI_DONE(13),
  TO0_HELLO(20),
  TO0_HELLO_ACK(21),
  TO0_OWNER_SIGN(22),
  TO0_ACCEPT_OWNER(25),
  TO1_HELLO_SDO(30),
  TO1_HELLO_SDO_ACK(31),
  TO1_PROVE_TO_SDO(32),
  TO1_SDO_REDIRECT(33),
  TO2_HELLO_DEVICE(40),
  TO2_PROVE_OP_HDR(41),
  TO2_GET_OP_NEXT_ENTRY(42),
  TO2_OP_NEXT_ENTRY(43),
  TO2_PROVE_DEVICE(44),
  TO2_GET_NEXT_DEVICE_SERVICE_INFO(45),
  TO2_NEXT_DEVICE_SERVICE_INFO(46),
  TO2_SETUP_DEVICE(47),
  TO2_GET_NEXT_OWNER_SERVICE_INFO(48),
  TO2_OWNER_SERVICE_INFO(49),
  TO2_DONE(50),
  TO2_DONE2(51),
  ERROR(255);

  private final int value;

  private MessageType(int value) {
    this.value = value;
  }

  /**
   * Utility method to return {@link MessageType} corresponding to the int value.
   *
   * @param value the int value
   * @return      {@link MessageType}
   */
  public static MessageType valueOfInt(int value) {
    for (MessageType pv : MessageType.values()) {
      if (pv.intValue() == value) {
        return pv;
      }
    }

    throw new IllegalArgumentException(); // no match
  }

  public static MessageType valueOfString(String value) {
    return valueOfInt(Integer.parseInt(value));
  }

  public int intValue() {
    return value;
  }

  @Override
  public String toString() {
    return Integer.toString(intValue());
  }
}

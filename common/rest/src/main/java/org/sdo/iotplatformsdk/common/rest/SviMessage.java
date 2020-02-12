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

/**
 * Class to manage ServiceInfo messages.
 *
 * <p>Each serviceinfo messages contains the module name, the message type, the opaque
 * identifier and length of the serviceinfo and the encoding format that will be used
 * to send the serviceinfo.
 */
public class SviMessage {

  // The name of the module sending the message.
  private String module;

  // The message to be sent
  private String msg;

  // The total length of the value of the message.
  private int valueLen;

  // the intended encoding expected to use when sending the value.
  private String enc;

  // the Key to use when retrieving the value.
  private String valueId;

  public SviMessage() {}

  /**
   * Returns the module name as specified by {@link SviMessageType}.
   *
   * @return module name.
   */
  public String getModule() {
    return module;
  }

  /**
   * Stores the module name as specified by {@link SviMessageType}.
   *
   * @param module module name.
   */
  public void setModule(String module) {
    this.module = module;
  }

  /**
   * Returns the message type as specified in {@link SviMessageType}.
   *
   * <p>One of FILEDESC, WRITE or EXEC.
   *
   * @return message type.
   */
  public String getMsg() {
    return msg;
  }

  /**
   * Stores the message type as specified in {@link SviMessageType}.
   *
   * <p>One of FILEDESC, WRITE or EXEC.
   *
   * @param msg message type.
   */
  public void setMsg(String msg) {
    this.msg = msg;
  }

  /**
   * Returns the length of the serviceinfo resource as specified by valuedId.
   *
   * @return length of the resource.
   */
  public int getValueLen() {
    return valueLen;
  }

  /**
   * Stores the length of the serviceinfo resource as specified by valuedId.
   *
   * @param valueLen length of the serviceinfo resource.
   */
  public void setValueLen(int valueLen) {
    this.valueLen = valueLen;
  }

  /**
   * Returns the encoding type as specified in {@link MessageEncoding}.
   *
   * @return the encoding in which serviceinfo will be sent.
   */
  public String getEnc() {
    return enc;
  }

  /**
   * Stores the encoding type as specified in {@link MessageEncoding}.
   *
   * @param enc the encoding in which serviceinfo will be sent.
   */
  public void setEnc(String enc) {
    this.enc = enc;
  }

  /**
   * Returns the identifier of the serviceinfo resource.
   *
   * @return serviceinfo identifier.
   */
  public String getValueId() {
    return valueId;
  }

  /**
   * Returns the identifier of the serviceinfo resource.
   *
   * @param valueId serviceinfo identifier.
   */
  public void setValueId(String valueId) {
    this.valueId = valueId;
  }

}

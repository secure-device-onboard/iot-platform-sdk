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

public class To0OwnerSign {

  public static final Integer ID = 22;

  private To0OwnerSignTo0d to0d;
  private SignatureBlock to1d;

  public To0OwnerSign(To0OwnerSignTo0d to0d, SignatureBlock to1d) {
    this.to0d = to0d;
    this.to1d = to1d;
  }

  public To0OwnerSignTo0d getTo0d() {
    return to0d;
  }

  public SignatureBlock getTo1d() {
    return to1d;
  }

  public void setTo0d(To0OwnerSignTo0d to0d) {
    this.to0d = to0d;
  }

  public void setTo1d(SignatureBlock to1d) {
    this.to1d = to1d;
  }

}

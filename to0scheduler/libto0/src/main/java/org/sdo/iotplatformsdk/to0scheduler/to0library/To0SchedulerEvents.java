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

package org.sdo.iotplatformsdk.to0scheduler.to0library;

import java.time.Duration;

import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;

public interface To0SchedulerEvents {

  /**
   * Implement this method to do some operation for any @link OwnershipProxy that has
   * completed TO0 protocol successfully.
   *
   * @param proxy       The {@link OwnershipProxy} instance.
   * @param waitSeconds The {@link Duration} instance.
   */
  void onSuccess(OwnershipProxy proxy, Duration waitSeconds);

  /**
   * Implement this method to do some operation for any @link OwnershipProxy that failed
   * TO0 protocol with a @link SdoError.
   *
   * @param proxy          The {@link OwnershipProxy} instance.
   * @param error          The {@link SdoError} instance.
   * @param suggestedDelay The {@link Duration} instance.
   */
  void onFailure(OwnershipProxy proxy, SdoError error, Duration suggestedDelay);
}

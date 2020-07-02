// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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

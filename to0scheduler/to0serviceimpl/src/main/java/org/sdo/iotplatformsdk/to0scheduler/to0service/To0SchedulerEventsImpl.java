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

package org.sdo.iotplatformsdk.to0scheduler.to0service;

import java.time.Duration;
import java.util.Optional;

import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.rest.DeviceState;
import org.sdo.iotplatformsdk.common.rest.Iso8061Timestamp;
import org.sdo.iotplatformsdk.common.rest.ProtocolError;
import org.sdo.iotplatformsdk.to0scheduler.rest.RestClient;
import org.sdo.iotplatformsdk.to0scheduler.to0library.To0SchedulerEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements To0SchedulerEvents to update the device state.
 *
 */
public class To0SchedulerEventsImpl implements To0SchedulerEvents {

  protected static final Logger logger = LoggerFactory.getLogger(To0SchedulerEventsImpl.class);

  private final RestClient restClient;

  public To0SchedulerEventsImpl(RestClient restClient) {
    this.restClient = restClient;
  }

  /*
   * This method updates the registration time-stamp and wait seconds for the input {@link
   * OwnershipProxy} using the {@link Duration} instance to a time in future, after which the proxy
   * is considered to be expired and is available for TO0.
   */
  @Override
  public void onSuccess(final OwnershipProxy proxy, final Duration waitSeconds) {
    try {

      logger.info("TO0 done for the device having uuid " + proxy.getOh().getG().toString());
      final DeviceState deviceState = new DeviceState();
      deviceState.setTo0Ws(Optional.of((int) waitSeconds.getSeconds()));
      deviceState.setTo0Timestamp(Optional.of(Iso8061Timestamp.now()));

      restClient.postDeviceState(proxy.getOh().getG().toString(), deviceState);
    } catch (Exception e) {
      logger.warn("Unable to set device status for the uuid " + proxy.getOh().getG().toString());
      logger.debug(e.getMessage(), e);
    }
  }

  /*
   * Upon SDOError during TO0 protocol execution, this method updates the wait seconds, the
   * time-stamp and error information for the device.
   */
  @Override
  public void onFailure(final OwnershipProxy proxy, final SdoError error,
      final Duration suggestedDelay) {
    try {

      logger.info("TO0 failed for the device having uuid " + proxy.getOh().getG().toString());
      final DeviceState deviceState = new DeviceState();
      deviceState.setTo0Ws(Optional.of((int) suggestedDelay.getSeconds()));
      deviceState.setTo0Timestamp(Optional.of(Iso8061Timestamp.now()));

      final ProtocolError protocolError = new ProtocolError();
      protocolError.setEc(error.getEc().toInteger());
      protocolError.setEm(error.getEm());
      protocolError.setEmsg(error.getEmsg());
      deviceState.setTo0Error(Optional.of(protocolError));

      restClient.postError(proxy.getOh().getG().toString(), deviceState);
    } catch (Exception e) {
      logger.warn("Unable to set device status for the uuid " + proxy.getOh().getG().toString());
      logger.debug(e.getMessage(), e);
    }
  }
}

// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.opsimpl;

import java.util.Optional;
import org.sdo.iotplatformsdk.common.rest.DeviceState;
import org.sdo.iotplatformsdk.common.rest.DeviceStateType;
import org.sdo.iotplatformsdk.common.rest.Iso8061Timestamp;
import org.sdo.iotplatformsdk.common.rest.ProtocolError;
import org.sdo.iotplatformsdk.ops.rest.RestClient;
import org.sdo.iotplatformsdk.ops.to2library.OwnerEvent;
import org.sdo.iotplatformsdk.ops.to2library.OwnerEventHandler;
import org.sdo.iotplatformsdk.ops.to2library.To2BeginEvent;
import org.sdo.iotplatformsdk.ops.to2library.To2EndEvent;
import org.sdo.iotplatformsdk.ops.to2library.To2ErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpsOwnerEventHandler implements OwnerEventHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(OpsOwnerEventHandler.class);

  private final RestClient restClient;

  public OpsOwnerEventHandler(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public void call(OwnerEvent event) {
    if (event instanceof To2BeginEvent) {
      call((To2BeginEvent) event);
    }
    if (event instanceof To2EndEvent) {
      call((To2EndEvent) event);
    }
    if (event instanceof To2ErrorEvent) {
      call((To2ErrorEvent) event);
    }
  }

  /**
   * This method sets the device status to be DeviceStatus.NOW_ONBOARDING.
   *
   * @param to2BeginEvent To2BeginEvent instance.
   */
  protected void call(final To2BeginEvent to2BeginEvent) {
    try {
      LOGGER.info("TO2 started for device with guid "
          + to2BeginEvent.getOwnershipProxy().getOh().getG().toString());

      final DeviceState state = new DeviceState();
      state.setTo2State(Optional.of(DeviceStateType.TO2BEGIN.toString()));
      state.setTo2Timestamp(Optional.of(Iso8061Timestamp.now()));

      restClient.postDeviceState(to2BeginEvent.getOwnershipProxy().getOh().getG().toString(),
          state);
    } catch (Exception e) {
      LOGGER.warn("Unable to set onboarding status for the guid "
          + to2BeginEvent.getOwnershipProxy() != null
              ? to2BeginEvent.getOwnershipProxy().getOh().getG().toString()
              : "");
      LOGGER.debug(e.getMessage(), e);
    }
  }

  /**
   * This method sets the device status to be DeviceStatus.ONBOARDED.
   *
   * @param to2EndEvent To2EndEvent instance.
   */
  protected void call(final To2EndEvent to2EndEvent) {
    try {

      final DeviceState state = new DeviceState();
      state.setTo2State(Optional.of(DeviceStateType.TO2END.toString()));
      state.setTo2Timestamp(Optional.of(Iso8061Timestamp.now()));

      restClient.postDeviceState(to2EndEvent.getOldOwnershipProxy().getOh().getG().toString(),
          state);

      LOGGER.info("TO2 complete for device with guid "
          + to2EndEvent.getOldOwnershipProxy().getOh().getG().toString());

    } catch (Exception e) {
      LOGGER.warn("Unable to set onboarding status for the guid "
          + to2EndEvent.getOldOwnershipProxy() != null
              ? to2EndEvent.getOldOwnershipProxy().getOh().getG().toString()
              : "");
      LOGGER.debug(e.getMessage(), e);
    }
  }

  /**
   * This method sets the device status to be DeviceStatus.FAILED_TO_ONBOARD.
   *
   * @param to2ErrorEvent To2ErrorEvent instance.
   */
  protected void call(final To2ErrorEvent to2ErrorEvent) {
    try {
      LOGGER.warn("TO2 failed for device with guid "
          + to2ErrorEvent.getOwnershipProxy().getOh().getG().toString());

      final DeviceState state = new DeviceState();
      state.setTo2State(Optional.of(DeviceStateType.TO2ERROR.toString()));
      state.setTo2Timestamp(Optional.of(Iso8061Timestamp.now()));

      final ProtocolError error = new ProtocolError();
      error.setEc(to2ErrorEvent.getError().getEc().toInteger());
      error.setEmsg(to2ErrorEvent.getError().getEmsg());
      error.setEm(to2ErrorEvent.getError().getEm());

      state.setTo2Error(Optional.of(error));

      restClient.postError(to2ErrorEvent.getOwnershipProxy().getOh().getG().toString(), state);

    } catch (Exception e) {
      LOGGER.warn("Unable to set onboarding status for the guid "
          + to2ErrorEvent.getOwnershipProxy() != null
              ? to2ErrorEvent.getOwnershipProxy().getOh().getG().toString()
              : "");
      LOGGER.debug(e.getMessage(), e);
    }
  }
}

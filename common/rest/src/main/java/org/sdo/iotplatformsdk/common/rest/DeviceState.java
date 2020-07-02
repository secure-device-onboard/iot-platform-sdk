// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

import java.util.Optional;

/**
 * The class contains the device's state information. The state information includes the time-stamp
 * at which the TO0 and TO2 gets completed, the information about any error that occurred during TO0
 * and TO2, the information about the TO2 stage, the number of seconds from the time-stamp after
 * which the existing TO0 expires, and the new device identifier of the device.
 *
 */
public class DeviceState {

  private Optional<String> to2State;
  private Optional<String> to2Timestamp;
  private Optional<ProtocolError> to2Error;
  private Optional<String> to0Timestamp;
  private Optional<ProtocolError> to0Error;
  private Optional<Integer> to0Ws;
  private Optional<String> g3;

  /**
   * Default constructor for DeviceState.
   */
  public DeviceState() {
    this.to2State = Optional.empty();
    this.to2Timestamp = Optional.empty();
    this.to2Error = Optional.empty();

    this.to0Timestamp = Optional.empty();
    this.to0Error = Optional.empty();
    this.to0Ws = Optional.empty();
    this.g3 = Optional.empty();
  }

  /**
   * Returns the TO2 state of the device, represented by {@link DeviceStateType}.
   *
   * @return the stage at which TO2 is done for the device.
   */
  public Optional<String> getTo2State() {
    return to2State;
  }

  /**
   * Store the TO2 state of the device, represented by {@link DeviceStateType}.
   *
   * @param state TO2 state.
   */
  public void setTo2State(Optional<String> state) {
    this.to2State = state;
  }

  /**
   * Returns the time-stamp at which TO2 is completed. The time-stamp pattern is specified by
   * {@link Iso8061Timestamp}.
   *
   * @return time-stamp specified by Iso8061Timestamp.
   */
  public Optional<String> getTo2Timestamp() {
    return to2Timestamp;
  }

  /**
   * Store the time-stamp at which T02 is completed. The time-stamp pattern is specified by
   * {@link Iso8061Timestamp}.
   *
   * @param timestamp time-stamp specified by Iso8061Timestamp.
   */
  public void setTo2Timestamp(Optional<String> timestamp) {
    this.to2Timestamp = timestamp;
  }

  /**
   * Returns the error information about the TO2 failure, represented by {@link ProtocolError}
   * object.
   *
   * @return TO2 error information.
   */
  public Optional<ProtocolError> getTo2Error() {
    return to2Error;
  }

  /**
   * Store the error information about the TO2 failure, represented by {@link ProtocolError} object.
   *
   * @param error TO2 error information.
   */
  public void setTo2Error(Optional<ProtocolError> error) {
    this.to2Error = error;
  }

  /**
   * Returns the error information about the TO0 failure, represented by {@link ProtocolError}
   * object.
   *
   * @return TO0 error information.
   */
  public Optional<ProtocolError> getTo0Error() {
    return to0Error;
  }

  /**
   * Store the error information about the TO0 failure, represented by {@link ProtocolError} object.
   *
   * @param error TO0 error information.
   */
  public void setTo0Error(Optional<ProtocolError> error) {
    this.to0Error = error;
  }

  /**
   * Returns the time-stamp at which TO0 for the device was either attempted, completed or failed.
   * The time-stamp pattern is specified by {@link Iso8061Timestamp}.
   *
   * @return time-stamp specified by Iso8061Timestamp.
   */
  public Optional<String> getTo0Timestamp() {
    return to0Timestamp;
  }

  /**
   * Store the time-stamp at which TO0 for the device was either attempted, completed or failed. The
   * time-stamp pattern is specified by {@link Iso8061Timestamp}.
   *
   * @param timestamp time-stamp specified by Iso8061Timestamp.
   */
  public void setTo0Timestamp(Optional<String> timestamp) {
    this.to0Timestamp = timestamp;
  }

  /**
   * Returns the number of seconds until which the last TO0 is valid.
   *
   * <p>After this number of seconds from the TO0 time-stamp, TO0 will have to be done again.
   *
   * @return number of TO0 wait seconds.
   */
  public Optional<Integer> getTo0Ws() {
    return to0Ws;
  }

  /**
   * Store the number of seconds until which the last TO0 is valid.
   *
   * <p>After this number of seconds from the TO0 time-stamp, TO0 will have to be done
   * again.
   *
   * @param ws the wait seconds value.
   */
  public void setTo0Ws(Optional<Integer> ws) {
    this.to0Ws = ws;
  }

  /**
   * Returns the device identifier (guid) of the device. Typically returns the current identifier,
   * or, the new identifier to be associated to the device after TO2 is done.
   *
   * @return the device identifier.
   */
  public Optional<String> getG3() {
    return g3;
  }

  /**
   * Store the device identifier (guid) of the device. Typically used to store the current
   * identifier, or, the new identifier to be associated to the device after TO2 is done.
   *
   * @param g3 the device identifier.
   */
  public void setG3(Optional<String> g3) {
    this.g3 = g3;
  }
}

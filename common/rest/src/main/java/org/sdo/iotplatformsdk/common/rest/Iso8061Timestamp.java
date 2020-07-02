// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Helper class for time-stamp management.
 */
public class Iso8061Timestamp {

  /**
   * Returns the current time-stamp as per UTC time-zone.
   *
   * @return UTC time-stamp.
   */
  public static String now() {
    return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now(ZoneOffset.UTC));
  }

  /**
   * Returns the calculated time-stamp by adding the specified number of seconds to the current
   * time. The returned time-stamp is as per UTC time-zone.
   *
   * @param ws number of seconds to be added to the current time.
   * @return UTC time-stamp
   */
  public static String nowPlusSeconds(int ws) {
    return DateTimeFormatter.ISO_LOCAL_DATE_TIME
        .format(LocalDateTime.now(ZoneOffset.UTC).plusSeconds(ws));
  }

  /**
   * Returns an {@link Instant} object by converting the specified string date.
   *
   * @param dateString the date to be converted.
   * @return Instant object of the specified time as per UTC time-zone.
   */
  public static Instant fromString(String dateString) {
    LocalDateTime ldt = LocalDateTime.parse(dateString);
    return ldt.atOffset(ZoneOffset.UTC).toInstant();
  }

  /**
   * Returns an {@link Instant} object by converting the specified string date and adding the
   * specified number of seconds to it.
   *
   * @param dateString the date to be converted and added to.
   * @param ws the number of seconds to be added to input date.
   * @return Instant object of the specified time as per UTC time-zone.
   */
  public static Instant instantPlusSeconds(String dateString, int ws) {
    LocalDateTime ldt = LocalDateTime.parse(dateString);
    return ldt.atOffset(ZoneOffset.UTC).toInstant().plusSeconds(ws);
  }
}

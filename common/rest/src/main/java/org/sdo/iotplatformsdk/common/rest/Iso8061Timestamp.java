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

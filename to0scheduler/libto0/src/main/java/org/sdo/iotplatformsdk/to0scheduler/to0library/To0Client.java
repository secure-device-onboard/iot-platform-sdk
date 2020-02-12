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

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;

import org.sdo.iotplatformsdk.common.protocol.rest.SdoUriComponentsBuilder;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.RendezvousInstr;
import org.sdo.iotplatformsdk.common.protocol.types.RendezvousInstr.Only;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class To0Client {

  private static final Logger LOG = LoggerFactory.getLogger(To0Client.class);

  private OwnershipProxy proxy;
  private ObjectFactory<To0ClientSession> sessionFactory = null;

  public OwnershipProxy getProxy() {
    return proxy;
  }

  public void setProxy(OwnershipProxy proxy) {
    this.proxy = proxy;
  }

  /**
   * Run TO0 once.
   */
  public Void run(BiConsumer<UUID, Duration> callback)
      throws InterruptedException, TimeoutException, URISyntaxException {

    OwnershipProxy proxy = getProxy();
    if (null == proxy) {
      throw new IllegalStateException();
    }

    final DateTimeFormatter dateTimeFormatter =
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault());

    Duration waitSeconds = null;
    long delay = -1;
    final int maxRetries = 3;

    for (int retry = 0; null == waitSeconds && retry < maxRetries; ++retry) {

      for (RendezvousInstr rendezvous : proxy.getOh().getR()) {

        Iterator<URI> it = rendezvous.toUris(Only.owner).iterator();
        while (null == waitSeconds && it.hasNext()) {

          ObjectFactory<To0ClientSession> sessionFactory = getSessionFactory();
          if (null == sessionFactory) {
            throw new IllegalArgumentException("session factory must not be null");
          }

          try {
            waitSeconds =
                getSessionFactory().getObject().run(proxy, new SdoUriComponentsBuilder(it.next()));

          } catch (Exception e) {
            LOG.error(e.getMessage());
            LOG.debug(e.getMessage(), e);
          }
        } // while no waitseconds, more URIs to try

        if (null == waitSeconds && null != rendezvous.getDelay()) {

          delay = rendezvous.getDelay().toSeconds();
          LOG.info("instruction contains delay. Pausing until "
              + dateTimeFormatter.format(Instant.now().plus(delay, ChronoUnit.SECONDS)));
          TimeUnit.SECONDS.sleep(delay);

        } else {
          delay = -1;
        }
      } // foreach rendezvous instruction

      // From the SDO Protocol Specification:
      //
      // If “delaysec” does not appear and the last entry in RendezvousInfo has been
      // processed, a delay of 120s +- random(30) is executed.
      final int delaySec = 120;
      final int jitterSec = 30;

      if (null == waitSeconds && delay < 0) {
        delay = delaySec + ThreadLocalRandom.current().nextInt(-jitterSec, jitterSec);
        LOG.info("All rendezvous instructions exhausted. Pausing until "
            + dateTimeFormatter.format(Instant.now().plus(delay, ChronoUnit.SECONDS)));
        TimeUnit.SECONDS.sleep(delay);
      }
    } // for waitSeconds != null && retries < max

    if (null == waitSeconds) {
      throw new TimeoutException(
          "Retry limit reached for proxy " + proxy.getOh().getG().toString());
    }

    if (null != callback) {
      callback.accept(proxy.getOh().getG(), waitSeconds);
    }

    return null; // return Void so Future<> can be used to track status
  }

  private ObjectFactory<To0ClientSession> getSessionFactory() {
    return sessionFactory;
  }

  @Autowired
  public void setSessionFactory(ObjectFactory<To0ClientSession> sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
}

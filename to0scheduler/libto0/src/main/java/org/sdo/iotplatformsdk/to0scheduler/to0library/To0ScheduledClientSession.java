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
import java.nio.CharBuffer;
import java.time.Duration;
import java.util.Iterator;

import org.sdo.iotplatformsdk.common.protocol.codecs.SdoErrorCodec;
import org.sdo.iotplatformsdk.common.protocol.types.MessageType;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.RendezvousInstr;
import org.sdo.iotplatformsdk.common.protocol.types.RendezvousInstr.Only;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class To0ScheduledClientSession {

  protected static final Logger logger = LoggerFactory.getLogger(To0ScheduledClientSession.class);

  private final OwnershipProxy proxy;
  private final To0ClientSession to0Session;
  private To0SchedulerEvents eventHandler;

  /**
   * Construct a new object.
   *
   * @param proxy      The {@link OwnershipProxy} instance.
   * @param to0Session The {@link To0ClientSession} instance.
   * @param eventHandler  The {@link To0SchedulerEvents} instance.
   */
  public To0ScheduledClientSession(OwnershipProxy proxy, To0ClientSession to0Session,
      To0SchedulerEvents eventHandler) {
    this.proxy = proxy;
    this.to0Session = to0Session;
    this.eventHandler = eventHandler;
  }

  /**
   * Gets the rendezvous server address and initiates TO0 protocol. Once TO0 completes, notifies
   * other threads that To0ClientSession instance is available.
   */
  public To0ClientSession call() {
    final To0ClientSession to0Session = getTo0Session();

    Duration delay = Duration.ZERO;

    try {
      if (to0Session != null) {

        final OwnershipProxy proxy = getProxy();
        if (proxy == null) {
          logger.error("OwnershipVoucher was not found.");
          throw new IllegalStateException();
        }

        for (RendezvousInstr rendezvous : proxy.getOh().getR()) {

          final Iterator<URI> it = rendezvous.toUris(Only.owner).iterator();
          while (delay.equals(Duration.ZERO) && it.hasNext()) {
            final URI uri = it.next();

            if (null != rendezvous.getDelay()) {
              delay = Duration.ofSeconds(Integer.parseInt(rendezvous.getDelay().toString()));
            }

            try {
              final Duration wsDuration = to0Session.run(proxy, uri);
              getEventHandler().onSuccess(getProxy(), wsDuration);
              return to0Session;
            } catch (Exception e) {
              logger.info("To0 session failed for {}", uri.toString());
              logger.error(e.getMessage());
              logger.debug(e.getMessage(), e);
              onTo0Exception(getProxy(), e, delay);
            }
          }
        }
      }
    } catch (Exception e) {
      logger.info("To0 session failed with a generic exception ", e.getMessage());
      logger.debug(e.getMessage(), e);
      onTo0Exception(getProxy(), e, delay);
      return to0Session;
    }
    return to0Session;
  }

  /**
   * This function returns the OwnershipProxy instance.
   *
   * @return OwnershipProxy
   */
  private OwnershipProxy getProxy() {
    return proxy;
  }

  /**
   * This function returns the To0ClientSession instance that runs the TO0 protocol.
   *
   * @return To0ClientSession
   */
  private To0ClientSession getTo0Session() {
    return to0Session;
  }

  /**
   * This method checks for the type of exception that occurred during the execution of TO0 protocol
   * for the input @link OwnershipProxy, and, delegates the exception handling to the respective
   * event handler class.
   *
   * @param proxy     The {@link OwnershipProxy} instance.
   * @param exception The {@link Exception} instance.
   * @param delay     The {@link Duration} instance.
   */
  protected void onTo0Exception(final OwnershipProxy proxy, final Exception exception,
      Duration delay) {

    final String json = exception.getMessage();
    SdoError sdoError;
    try {
      sdoError = new SdoErrorCodec().decoder().apply(CharBuffer.wrap(json));
      // Sdo protocol error. Do not schedule again for some time.
      delay = Duration.ofDays(1);
    } catch (Exception e) {
      sdoError =
          new SdoError(SdoErrorCode.InternalError, MessageType.ERROR, exception.getMessage());
      // Generic error. Can be schedlued again.
      delay = Duration.ofMinutes(5);
    }
    getEventHandler().onFailure(proxy, sdoError, delay);

  }

  protected To0SchedulerEvents getEventHandler() {
    return this.eventHandler;
  }
}

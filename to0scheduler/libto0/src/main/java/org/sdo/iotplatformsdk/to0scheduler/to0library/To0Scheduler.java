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
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import org.sdo.iotplatformsdk.common.protocol.config.ObjectFactory;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class To0Scheduler {

  protected static final Logger logger = LoggerFactory.getLogger(To0Scheduler.class);

  private final ExecutorService taskScheduler;
  private final ObjectFactory<To0ClientSession> sessionFactory;
  private final Queue<To0ClientSession> availableSessions = new LinkedList<To0ClientSession>();

  // max number of TO0ClientSessions permitted.
  private final int sessionPoolSize;

  private final To0SchedulerEvents eventHandler;
  private final To0ProxyStore proxyStore;

  /**
   * Construct a new object.
   */
  public To0Scheduler(ExecutorService taskExecutor, ObjectFactory<To0ClientSession> sessionFactory,
      To0SchedulerEvents handler, To0ProxyStore proxyStore, int to0ClientSessionPoolSize) {
    this.taskScheduler = taskExecutor;
    this.sessionFactory = sessionFactory;
    this.eventHandler = handler;
    this.proxyStore = proxyStore;
    this.sessionPoolSize = to0ClientSessionPoolSize;

    logger.debug("The specified To0 session pool size is " + getSessionPoolSize());
    for (int i = 0; i < getSessionPoolSize(); i++) {
      try {
        getAvailableSessions().add(getSessionFactory().getObject());
      } catch (Exception e) {
        logger.debug(e.getMessage(), e);
      }
    }
  }

  /**
   * This method notifies all threads that a To0ClientSession is available.
   *
   * @param to0ClientSession The {@link To0ClientSession} instance.
   */
  protected void notifySessionAvailable(To0ClientSession to0Session) {
    synchronized (this) {
      getAvailableSessions().offer(to0Session);
      this.notifyAll();
    }
  }

  /**
   * Returns the thread pool instance.
   *
   * @return ThreadPoolExecutor instance.
   */
  protected Executor getExecutorService() {
    return taskScheduler;
  }

  protected ObjectFactory<To0ClientSession> getSessionFactory() {
    return sessionFactory;
  }

  protected To0SchedulerEvents getEventHandler() {
    return eventHandler;
  }

  public To0ProxyStore getProxyStore() {
    return proxyStore;
  }

  protected Queue<To0ClientSession> getAvailableSessions() {
    return availableSessions;
  }

  protected int getSessionPoolSize() {
    return sessionPoolSize;
  }

  /**
   * Schedules TO0 for the received guids if {@link To0ClientSession} instances are available.
   * Return true if one or more devices are scheduled without any exception.
   * If an exception occurs, return false;
   */
  public boolean run(String[] deviceList, Duration waitSeconds) {
    try {
      for (String deviceId : deviceList) {
        synchronized (this) {
          if (getAvailableSessions().isEmpty()) {
            return true;
          }
          final To0ClientSession to0ClientSession = getAvailableSessions().poll();
          to0ClientSession.setTo0WaitSeconds(waitSeconds);
          CompletableFuture
              .supplyAsync(() -> setDeviceForTo0(deviceId, to0ClientSession), getExecutorService())
              .thenAccept(to0Session -> {
                notifySessionAvailable(to0Session);
              });
        }
      }
      return true;
    } catch (Exception e) {
      logger.warn("Error while scheduling the devices for To0. ", e.getMessage());
      logger.error(e.getMessage(), e);
      return false;
    }
  }

  private To0ClientSession setDeviceForTo0(String deviceId, To0ClientSession to0ClientSession) {
    OwnershipProxy proxy;
    try {
      proxy = getProxyStore().getProxy(deviceId);
      logger.info("Register OP: " + proxy.getOh().getG().toString());
    } catch (Exception e) {
      logger.error("Error in fetching the proxy for " + deviceId);
      return null;
    }
    return new To0ScheduledClientSession(proxy, to0ClientSession, getEventHandler()).call();
  }
}

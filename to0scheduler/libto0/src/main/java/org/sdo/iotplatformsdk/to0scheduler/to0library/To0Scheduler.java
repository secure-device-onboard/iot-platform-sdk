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

import java.io.IOException;
import java.time.Duration;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

import org.sdo.iotplatformsdk.common.protocol.config.SimpleWaitSecondsBuilder;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public class To0Scheduler implements ApplicationListener<ContextRefreshedEvent> {

  protected static final Logger logger = LoggerFactory.getLogger(To0Scheduler.class);

  private ThreadPoolTaskScheduler taskScheduler;
  private ObjectFactory<To0ClientSession> sessionFactory;
  private final Queue<To0ClientSession> availableSessions = new LinkedList<To0ClientSession>();

  // max number of TO0ClientSessions permitted. Default: 10.
  @Value("${session.pool.size:10}")
  private int sessionPoolSize;

  private To0SchedulerEvents eventHandler;
  private To0ProxyStore proxyStore;

  /**
   * Construct a new object.
   */
  public To0Scheduler() {}

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

  @Autowired
  public void setTaskScheduler(ThreadPoolTaskScheduler taskExecutor) {
    taskScheduler = taskExecutor;
  }

  /**
   * Returns the scheduler thread pool instance.
   *
   * @return ThreadPoolTaskScheduler instance.
   */
  protected ThreadPoolTaskScheduler getTaskScheduler() {
    return taskScheduler;
  }

  protected ObjectFactory<To0ClientSession> getSessionFactory() {
    return sessionFactory;
  }

  @Autowired
  public void setSessionFactory(ObjectFactory<To0ClientSession> sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Autowired
  public void setEventHandler(To0SchedulerEvents handler) {
    this.eventHandler = handler;
  }

  protected To0SchedulerEvents getEventHandler() {
    return eventHandler;
  }

  @Autowired
  public void setProxyStore(To0ProxyStore proxyStore) {
    this.proxyStore = proxyStore;
  }

  public To0ProxyStore getProxyStore() {
    return proxyStore;
  }

  protected Queue<To0ClientSession> getAvailableSessions() {
    return availableSessions;
  }

  public void setSessionPoolSize(int size) {
    this.sessionPoolSize = size;
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
          SimpleWaitSecondsBuilder wsBuilder = new SimpleWaitSecondsBuilder();
          wsBuilder.setWaitSeconds(waitSeconds);
          To0ClientSession to0ClientSession = getAvailableSessions().poll();
          CompletableFuture
              .supplyAsync(() -> setDeviceForTo0(deviceId, to0ClientSession, wsBuilder),
                  getTaskScheduler())
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

  private To0ClientSession setDeviceForTo0(String deviceId, To0ClientSession to0ClientSession,
      SimpleWaitSecondsBuilder wsBuilder) {
    OwnershipProxy proxy;
    try {
      proxy = getProxyStore().getProxy(deviceId);
      logger.info("Register OP: " + proxy.getOh().getG().toString());
    } catch (IOException ie) {
      logger.info("Error in fetching the proxy for " + deviceId);
      return null;
    }
    return new To0ScheduledClientSession(proxy, to0ClientSession, wsBuilder, getEventHandler())
        .call();
  }

  /**
   * Depending on the session pool size, set the number of available @link To0ClientSession,
   * and schedule.
   */
  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    // setup the session factory
    logger.debug("The specified To0 session pool size is " + getSessionPoolSize());
    logger.debug("The specified thread pool size is " + getTaskScheduler().getPoolSize());
    for (int i = 0; i < getSessionPoolSize(); i++) {
      getAvailableSessions().add(getSessionFactory().getObject());
    }
  }
}

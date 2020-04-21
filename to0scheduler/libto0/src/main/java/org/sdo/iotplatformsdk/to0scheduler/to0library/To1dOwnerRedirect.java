/*******************************************************************************
 * Copyright 2020 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/

package org.sdo.iotplatformsdk.to0scheduler.to0library;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URI;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class loads the information of TO0.OwnerSign.to1d.bo.i1, TO0.OwnerSign.to1d.bo.dns1 and
 * TO0.OwnerSign.to1d.bo.port1 from a separate properties file. This information is sent as a
 * part of TO0.OwnerSign.to1d.bo.
 */
public class To1dOwnerRedirect {

  private static final Logger LOG = LoggerFactory.getLogger(To1dOwnerRedirect.class);

  private static final String DNS = "dns";
  private static final String IP = "ip";
  private static final String PORT = "port";
  private static final int PORT1_DEFAULT = 8042;

  private final URI to1dOwnerRedirectPath;
  private String dns1 = null;
  private InetAddress i1 = null;
  private Integer port1 = null;


  /**
   * Constructor.
   *
   * @param to1dOwnerRedirectPath URI containing the owner redirect information.
   */
  public To1dOwnerRedirect(URI to1dOwnerRedirectPath) {
    this.to1dOwnerRedirectPath = to1dOwnerRedirectPath;
    load();
  }

  public String getDns1() {
    return dns1;
  }

  public InetAddress getI1() {
    return i1;
  }

  public Integer getPort1() {
    return port1;
  }

  /**
   * Read the properties from the given uri and set the values of the fields.
   * If any error occurs, set the default values.
   */
  private void load() {
    // set the defaults first, in case the uri to1dOwnerRedirectPath, is empty.
    loadDefaults();
    // set the values from the file.
    final Properties reader = new Properties();
    if (null != to1dOwnerRedirectPath) {
      try (final InputStream inputStream = new FileInputStream(new File(to1dOwnerRedirectPath))) {
        reader.load(inputStream);
        // a null string is a valid value. This indicates that ip should be used.
        if (reader.containsKey(DNS)) {
          this.dns1 = reader.getProperty(DNS);
        }
        // a null string is not a valid value. default value is mandatory.
        if (reader.containsKey(IP) && !reader.getProperty(IP).isBlank()) {
          this.i1 = InetAddress.getByName(reader.getProperty(IP));
        }
        if (reader.containsKey(PORT) && !reader.getProperty(PORT).isBlank()) {
          this.port1 = Integer.valueOf(reader.getProperty(PORT));
        }
      } catch (IOException e) {
        LOG.warn("Unable to load to1d information. Setting the default values.");
        LOG.debug(e.getMessage(), e);
      }
    }
  }

  private void loadDefaults() {
    // This socket doesn't connect to the outside world,
    // nor does the target IP need to be reachable. By putting a datagram socket
    // into a connect state, we can determine our outgoing network interface.
    try (final DatagramSocket socket = new DatagramSocket()) {
      socket.connect(InetAddress.getByName("8.8.8.8"), 8888);
      this.dns1 = socket.getLocalAddress().getCanonicalHostName();
    } catch (Exception e) {
      this.i1 = InetAddress.getLoopbackAddress();
    }
    this.port1 = PORT1_DEFAULT;
  }
}

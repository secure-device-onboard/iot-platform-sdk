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

package org.sdo.iotplatformsdk.common.protocol.config;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorException.BasicReason;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.PKIXRevocationChecker.Option;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.Pm.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.security.CryptoLevel;
import org.sdo.iotplatformsdk.common.protocol.security.CryptoLevel11;
import org.sdo.iotplatformsdk.common.protocol.security.CryptoLevels;
import org.sdo.iotplatformsdk.common.protocol.types.CipherBlockMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

/**
 * Properties for SDO services.
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config-typesafe-configuration-properties">Type-Safe
 *      Configuration Properties</a>
 */
@ConfigurationProperties(prefix = "org.sdo") // all properties start with this prefix
public class SdoProperties {

  public static final String JAVA_IO_TMPDIR = "java.io.tmpdir";
  public static final String NOT_SET = "NOT SET";

  private final Device device = new Device();
  private final Di di = new Di();
  private final Epid epid = new Epid();
  private final Manufacturer manufacturer = new Manufacturer();
  private final Owner owner = new Owner();
  private final Pkix pkix = new Pkix();
  private final Pm pm = new Pm();
  private final ProxyAdd proxyAdd = new ProxyAdd();
  private final ProxyNew proxyNew = new ProxyNew();
  private final To0 to0 = new To0();
  private final To2 to2 = new To2();
  // Default to highest level crypto
  private String cryptoLevel = new CryptoLevel11().version();
  private List<SecureRandomAlgorithm> secureRandom = Arrays.asList(SecureRandomAlgorithm.NativePRNG,
      SecureRandomAlgorithm.Windows_PRNG, SecureRandomAlgorithm.SHA1PRNG);

  private static URI toAbsolute(URI uri) {
    URI pwd = Paths.get(".").toUri();
    URI resolved = pwd.resolve(uri);
    return resolved.normalize();
  }

  public String getCryptoLevel() {
    return this.cryptoLevel;
  }

  /**
   * Property org.sdo.crypto-level
   *
   * <p>The CryptoLevel version which should be used by SDO.
   */
  public void setCryptoLevel(final String cryptoLevel) {
    for (CryptoLevel cl : CryptoLevels.all()) {
      if (Objects.equals(cl.version(), cryptoLevel)) {
        this.cryptoLevel = cryptoLevel;
        return;
      }
    }

    throw new IllegalArgumentException("no match for " + cryptoLevel);
  }

  /**
   * Property container org.sdo.device.
   *
   * <p>@see Device
   */
  public Device getDevice() {
    return device;
  }

  /**
   * Property container org.sdo.di.
   *
   * <p>@see Di
   */
  public Di getDi() {
    return di;
  }

  /**
   * Property container org.sdo.epid.
   *
   * <p>@see Epid
   */
  public Epid getEpid() {
    return epid;
  }

  /**
   * Property container org.sdo.manufacturer.
   *
   * <p>@see Manufacturer
   */
  public Manufacturer getManufacturer() {
    return manufacturer;
  }

  /**
   * Property container org.sdo.owner.
   *
   * <p>@see Owner
   */
  public Owner getOwner() {
    return owner;
  }

  public Pkix getPkix() {
    return pkix;
  }

  /**
   * Property container org.sdo.pm
   *
   * <p>@see Pm
   */
  public Pm getPm() {
    return pm;
  }

  /**
   * Property container org.sdo.proxyadd
   *
   * <p>@see ProxyAdd
   */
  public ProxyAdd getProxyAdd() {
    return proxyAdd;
  }

  /**
   * Property container org.sdo.proxynew
   *
   * <p>@see ProxyNew
   */
  public ProxyNew getProxyNew() {
    return proxyNew;
  }

  public List<SecureRandomAlgorithm> getSecureRandom() {
    return secureRandom;
  }

  public void setSecureRandom(List<SecureRandomAlgorithm> secureRandom) {
    this.secureRandom = new ArrayList<>(secureRandom);
  }

  /**
   * Property container org.sdo.to0
   *
   * <p>@see To0
   */
  public To0 getTo0() {
    return to0;
  }

  /**
   * Property container org.sdo.to2.
   *
   * <p>@see To2
   */
  public To2 getTo2() {
    return to2;
  }

  public enum SecureRandomAlgorithm {
    NativePRNG,
    NativePRNGBlocking,
    NativePRNGNonBlocking,
    PKCS11,
    SHA1PRNG,
    Windows_PRNG
  }

  /**
   * Device properties.
   *
   * <p>This class exists only as a value object, with no interesting behavior of its own.
   */
  public static class Device {

    private @Nullable URI cert = null;
    private @Nullable URI credentials = null;
    private @Nullable URI key = null;
    private @Nullable Path outputDir = Paths.get(System.getProperty(JAVA_IO_TMPDIR));
    private boolean stopAfterDi = true;

    /**
     * Property org.sdo.device.cert
     *
     * <p>The URI at which the device can locate its public certificate. This certificate must be
     * verifiable by the certificate chain used by the manufacturer when creating proxies.
     *
     * <p>file: URIs may be used for local files.
     *
     * <p>This property is optional. If blank or not set, the device will use an
     * Intel EPID 2 test key.
     *
     * @see OwnershipProxy#getDc()
     */
    @Nullable
    public URI getCert() {
      return cert;
    }

    public void setCert(@Nullable URI cert) {
      this.cert = toAbsolute(cert);
    }

    /**
     * Property org.sdo.device.credentials
     *
     * <p>The URI at which the file can find its credentials.
     *
     * <p>file: URIs may be used for local files.
     *
     * <p>This property is optional. If this property is not set, the device will begin
     * the DI protocol to obtain credentials.
     */
    @Nullable
    public URI getCredentials() {
      return credentials;
    }

    public void setCredentials(@Nullable URI credentials) {
      this.credentials = toAbsolute(credentials);
    }

    /**
     * Property org.sdo.device.key
     *
     * <p>The URI at which the device can find its private key.
     *
     * <p>file: URIs may be used for local files.
     *
     * <p>This property is optional. If this property is not set, the device will use an Intel
     * EPID 2 test key.
     */
    @Nullable
    public URI getKey() {
      return key;
    }

    public void setKey(@Nullable URI key) {
      this.key = toAbsolute(key);
    }

    /**
     * Property org.sdo.device.output-dir
     *
     * <p>The directory in which the device will store generated credentials. If this directory does
     * not exist, it will be created.
     *
     * <p>This property is optional. If this value is empty or not set, no credentials will be
     * stored.
     */
    @Nullable
    public Path getOutputDir() {
      return outputDir;
    }

    public void setOutputDir(@Nullable String outputDir) {
      this.outputDir = Paths.get(outputDir);
    }

    /**
     * Property org.sdo.device.stop-after-di
     *
     * <p>If this flag is set, the device will stop and exit after running DI and storing obtained
     * credentials. If this flag is clear, the device will proceed automatically from DI to TO1.
     *
     * <p>If the device already has credentials and does not need to run DI, this flag will have no
     * effect.
     *
     * <p>This property is optional, and defaults to true.
     */
    public boolean isStopAfterDi() {
      return stopAfterDi;
    }

    public void setStopAfterDi(boolean stopAfterDi) {
      this.stopAfterDi = stopAfterDi;
    }
  }

  /**
   * DI properties.
   *
   * <p>This class exists only as a value object, with no interesting behavior of its own.
   */
  public static class Di {

    public static final String URI_DEFAULT = "http://localhost:8039";

    private AppStart appStart = new AppStart();
    private URI uri = URI.create(URI_DEFAULT);

    /**
     * Property container org.sdo.di.appstart.
     *
     * <p>@see AppStart
     */
    public AppStart getAppStart() {
      return appStart;
    }

    /**
     * Property org.sdo.di.uri
     *
     * <p>The URI at which the device will try to contact a manufacturer when running DI to
     * obtain its initial credentials.
     *
     * <p>This property is optional. If not set, the device will attempt to contact the
     * manufacturer at
     * {@value org.sdo.iotplatformsdk.common.protocol.config.SdoProperties.Di#URI_DEFAULT}.
     */
    public URI getUri() {
      return uri;
    }

    public void setUri(URI uri) {
      this.uri = toAbsolute(uri);
    }

    /**
     * DI.AppStart properties.
     *
     * <p>This class exists only as a value object, with no interesting behavior of its own.
     */
    public static class AppStart {

      private String mstring = NOT_SET;

      /**
       * Property org.sdo.di.appstart.m
       *
       * <p>The value of the protocol field DI.AppStart.m.
       *
       * <p>This property is optional. If not set, it defaults to
       * {@value org.sdo.iotplatformsdk.common.protocol.config.SdoProperties#NOT_SET}.
       */
      public String getM() {
        return mstring;
      }

      public void setM(String m) {
        this.mstring = m;
      }
    }
  }

  /**
   * Epid properties.
   *
   * <p>This class exists only as a value object, with no interesting behavior of its own.
   */
  public static class Epid {

    private EpidOptionBean options = new EpidOptionBean();

    public EpidOptionBean getOptions() {
      return options;
    }

    public void setEpidOnlineUrl(String value) {
      this.options.setEpidOnlineUrl(value);
    }

    public void setTestMode(boolean value) {
      this.options.setTestMode(value);
    }
  }

  /**
   * Manufacturer properties.
   *
   * <p>This class exists only as a value object, with no interesting behavior of its own.
   */
  public static class Manufacturer {

    private Path outputDir = Paths.get(System.getProperty(JAVA_IO_TMPDIR));

    /**
     * Property org.sdo.manufacturer.output-dir.
     *
     * <p>The directory to which the manufacturer will write generated proxies.
     *
     * <p>This property is optional. If not set, the manufacturer will store generated proxies
     * in the location identified by Java system property
     * {@value org.sdo.iotplatformsdk.common.protocol.config.SdoProperties#JAVA_IO_TMPDIR}.
     */
    public Path getOutputDir() {
      return outputDir;
    }

    public void setOutputDir(@Nullable String outputDir) {
      this.outputDir = Paths.get(outputDir);
    }
  }

  /**
   * Owner properties.
   *
   * <p>This class exists only as a value object, with no interesting behavior of its own.
   */
  public static class Owner {

    private @Nullable URI cert = null;
    private @Nullable URI key = null;
    private @Nullable Path outputDir = Paths.get(System.getProperty(JAVA_IO_TMPDIR));
    private Path proxyDir = Paths.get(System.getProperty(JAVA_IO_TMPDIR));

    /**
     * Property org.sdo.owner.cert.
     *
     * <p>The URI at which the owner can find its public certificate.
     *
     * <p>file: URIs may be used for local files.
     */
    @Nullable
    public URI getCert() {
      return cert;
    }

    public void setCert(@Nullable URI cert) {
      this.cert = toAbsolute(cert);
    }

    /**
     * Property org.sdo.owner.key.
     *
     * <p>The URI at which the owner can find its private key.
     *
     * <p>file: URIs may be used for local files.
     */
    @Nullable
    public URI getKey() {
      return key;
    }

    public void setKey(@Nullable URI key) {
      this.key = toAbsolute(key);
    }

    /**
     * Property org.sdo.owner.output-dir.
     *
     * <p>The directory in which the owner will store generated proxies.
     *
     * <p>This property is optional. If not set, the owner will store proxies in the directory
     * indicated by Java system property
     * {@value org.sdo.iotplatformsdk.common.protocol.config.SdoProperties#JAVA_IO_TMPDIR}.
     */
    @Nullable
    public Path getOutputDir() {
      return outputDir;
    }

    public void setOutputDir(@Nullable String outputDir) {
      this.outputDir = Paths.get(outputDir);
    }

    /**
     * Property org.sdo.owner.proxy-dir.
     *
     * <p>The directory which the owner will watch for ownership proxies.
     *
     * <p>If the owner finds a proxy in this directory, it will begin TO0 in order to register
     * itself as responsible for that proxy. The owner is able to detect changes in this
     * directory during runtime.
     *
     * <p>This property is optional. If not set, the owner will watch the directory indicated
     * by Java system property
     * {@value org.sdo.iotplatformsdk.common.protocol.config.SdoProperties#JAVA_IO_TMPDIR}.
     */
    public Path getProxyDir() {
      return proxyDir;
    }

    public void setProxyDir(@Nullable String proxyDir) {
      this.proxyDir = Paths.get(proxyDir);
    }
  }

  public static class Pkix {

    private Set<CertPathValidatorException.BasicReason> acceptErrors = Collections.emptySet();
    private Set<URI> crls = null;
    private Set<PKIXRevocationChecker.Option> revocationOptions =
        EnumSet.noneOf(PKIXRevocationChecker.Option.class);
    private Set<URI> trustAnchors = null;

    public Set<BasicReason> getAcceptErrors() {
      return acceptErrors;
    }

    public void setAcceptErrors(Set<BasicReason> acceptErrors) {
      this.acceptErrors = acceptErrors;
    }

    @Nullable
    public Set<URI> getCrls() {
      return crls;
    }

    /**
     * Set the certificate revocation list.
     */
    public void setCrls(Set<URI> crls) {

      if (null != crls) {
        this.crls = crls.stream().map(SdoProperties::toAbsolute).collect(Collectors.toSet());

      } else {
        this.crls = null;
      }
    }

    public Set<Option> getRevocationOptions() {
      return revocationOptions;
    }

    public void setRevocationOptions(Set<Option> revocationOptions) {
      this.revocationOptions = revocationOptions;
    }

    public Set<URI> getTrustAnchors() {
      return trustAnchors;
    }

    /**
     * Set the trust-anchors.
     */
    public void setTrustAnchors(Set<URI> trustAnchors) {
      if (null != trustAnchors) {
        this.trustAnchors =
            trustAnchors.stream().map(SdoProperties::toAbsolute).collect(Collectors.toSet());

      } else {
        this.trustAnchors = null;
      }
    }
  }

  /**
   * PM properties.
   *
   * <p>This class exists only as a value object, with no interesting behavior of its own.
   */
  public static class Pm {

    private final CredMfg credMfg = new CredMfg();
    private final CredOwner credOwner = new CredOwner();
    private final OwnershipProxy ownershipProxy = new OwnershipProxy();

    /**
     * Property container org.sdo.pm.credmfg.
     *
     * <p>@see CredMfg
     */
    public CredMfg getCredMfg() {
      return credMfg;
    }

    /**
     * Property container org.sdo.pm.credowner.
     *
     * <p>@see CredOwner
     */
    public CredOwner getCredOwner() {
      return credOwner;
    }

    /**
     * Property container org.sdo.pm.ownershipproxy.
     *
     * <p>@see OwnershipProxy
     */
    public OwnershipProxy getOwnershipProxy() {
      return ownershipProxy;
    }

    /**
     * PM.CredMfg properties.
     *
     * <p>This class exists only as a value object, with no interesting behavior of its own.
     */
    public static class CredMfg {

      private @Nullable URI cu = null;
      private String deviceinfo = NOT_SET;

      /**
       * Property org.sdo.pm.credmfg.cu.
       *
       * <p>The URI at which the manufacturer's public certificate may be found.
       *
       * <p>file: URIs may be used for local files.
       */
      @Nullable
      public URI getCu() {
        return cu;
      }

      public void setCu(@Nullable URI cu) {
        this.cu = toAbsolute(cu);
      }

      /**
       * Property org.sdo.pm.credmfg.d.
       *
       * <p>The value to be used for protocol field PM.CredMfg.d.
       *
       * <p>This property is optional. If not set, it will default to
       * {@value org.sdo.iotplatformsdk.common.protocol.config.SdoProperties#NOT_SET}.
       */
      public String getD() {
        return deviceinfo;
      }

      public void setD(String d) {
        this.deviceinfo = d;
      }
    }

    /**
     * PM.CredOwner properties.
     *
     * <p>This class exists only as a value object, with no interesting behavior of its own.
     */
    public static class CredOwner {

      public static final String R_DEFAULT =
          "http://localhost:8040?only=owner,http://localhost:8041?only=dev";

      private @Nullable UUID guid = null;
      private List<URI> rendezvousinfo =
          Arrays.stream(R_DEFAULT.split(",")).map(URI::create).collect(Collectors.toList());

      /**
       * Property org.sdo.pm.credowner.g.
       *
       * <p>The value to use in protocol field PM.CredOwner.g.
       *
       * <p>This property is optional. If not set, a random UUID will be generated.
       */
      @Nullable
      public UUID getG() {
        return guid;
      }

      public void setG(@Nullable UUID g) {
        this.guid = g;
      }

      /**
       * Property org.sdo.pm.credowner.r.
       *
       * <p>The value to use in protocol field PM.CredOwner.r.
       *
       * <p>This value is expressed as a list of URLs. RendezvousInfo variables may be set via URL
       * parameters.
       *
       * <p>This property is optional, and defaults to {@value R_DEFAULT}.
       */
      public List<URI> getR() {
        return rendezvousinfo;
      }

      public void setR(List<URI> r) {
        this.rendezvousinfo = r;
      }
    }

    /**
     * PM.OwnershipProxy properties.
     *
     * <p>This class exists only as a value object, with no interesting behavior of its own.
     */
    public static class OwnershipProxy {

      private @Nullable URI dc = null;

      /**
       * Property org.sdo.pm.ownershipproxy.dc.
       *
       * <p>The URI from which to read the device's certificate chain. The manufacturer add this
       * certificate chain to generated ownership proxies.
       *
       * <p>This property is mandatory if a non-EPID device key is used. This property is ignored if
       * EPID keys are used on the device.
       *
       * <p>file: URIs may be used for local files.
       */
      @Nullable
      public URI getDc() {
        return dc;
      }

      public void setDc(@Nullable URI dc) {
        this.dc = toAbsolute(dc);
      }
    }
  }

  /**
   * Property container org.sdo.proxyadd.
   */
  public static class ProxyAdd {

    private @Nullable Path in = null;
    private @Nullable Path out = null;

    /**
     * Property org.sdo.proxyadd.in.
     *
     * <p>The name of the file proxyadd should read as the input proxy.
     *
     * <p>This property is optional. If blank or not set, the proxy will be read from standard in.
     */
    @Nullable
    public Path getIn() {
      return in;
    }

    public void setIn(@Nullable String in) {
      this.in = Paths.get(in);
    }

    /**
     * Property org.sdo.proxyadd.out.
     *
     * <p>The name of the file proxyadd should write as the output proxy.
     *
     * <p>This property is optional. If blank or not set, the proxy will be written to
     * standard out.
     */
    @Nullable
    public Path getOut() {
      return out;
    }

    public void setOut(@Nullable String out) {
      this.out = Paths.get(out);
    }
  }

  /**
   * Property container org.sdo.proxynew.
   */
  public static class ProxyNew {

    private @Nullable Path oc = null;
    private @Nullable Path op = null;

    /**
     * Property org.sdo.proxynew.oc.
     *
     * <p>The filename to use for generated device credential, 'oc', files.
     *
     * <p>This property is optional. If blank or not set, the oc will be written to
     * $(org.sdo.pm.credowner.g).oc.
     */
    @Nullable
    public Path getOc() {
      return oc;
    }

    public void setOc(@Nullable String oc) {
      this.oc = Paths.get(oc);
    }

    /**
     * Property org.sdo.proxynew.op.
     *
     * <p>The filename to use for generated ownership voucher, 'op', files.
     *
     * <p>This property is optional. If blank or not set, the op will be written to
     * $(org.sdo.pm.credowner.g).op.
     */
    @Nullable
    public Path getOp() {
      return op;
    }

    public void setOp(@Nullable String op) {
      this.op = Paths.get(op);
    }
  }

  /**
   * TO0 properties.
   *
   * <p>This class exists only as a value object, with no interesting behavior of its own.
   */
  public static class To0 {

    private final OwnerSign ownerSign = new OwnerSign();

    /**
     * Property container org.sdo.to0.ownersign.
     *
     * <p>@see OwnerSign
     */
    public OwnerSign getOwnerSign() {
      return ownerSign;
    }

    /**
     * TO0.OwnerSign properties.
     *
     * <p>This class exists only as a value object, with no interesting behavior of its own.
     */
    public static class OwnerSign {

      private final To0d to0d = new To0d();
      private final To1d to1d = new To1d();

      /**
       * Property container org.sdo.to0.ownersign.to0d.
       *
       * <p>@see To0d
       */
      public To0d getTo0d() {
        return to0d;
      }

      /**
       * Property container org.sdo.to0.ownersign.to1d.
       *
       * <p>@see To1d
       */
      public To1d getTo1d() {
        return to1d;
      }

      /**
       * TO0.OwnerSign.to0d properties.
       *
       * <p>This class exists only as a value object, with no interesting behavior of its own.
       */
      public static class To0d {

        private Duration ws = Duration.ofHours(1);

        /**
         * Property org.sdo.to0.ownersign.to0d.ws.
         *
         * <p>The duration for protocol field TO0.OwnerSign.To0d.ws.
         *
         * <p>This property accepts formats supported by spring boot as described <a href=
         * "https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-
         * external-config-conversion-duration">here</a>.
         *
         * <p>This property is optional, and defaults to one hour.
         */
        public Duration getWs() {
          return ws;
        }

        public void setWs(Duration ws) {
          this.ws = ws;
        }
      }

      /**
       * TO0.OwnerSign.to1d properties.
       *
       * <p>This class exists only as a value object, with no interesting behavior of its own.
       */
      public static class To1d {

        private final Bo bo = new Bo();

        /**
         * Property container org.sdo.TO0.OwnerSign.to1d.bo.
         *
         * <p>@see Bo
         */
        public Bo getBo() {
          return bo;
        }

        /**
         * TO0.OwnerSign.to1d.bo properties.
         *
         * <p>This class exists only as a value object, with no interesting behavior of its own.
         */
        public static class Bo {

          public static final int PORT1_DEFAULT = 8042;

          private @Nullable String dns1 = null;
          private @Nullable InetAddress i1 = null;
          private Integer port1 = PORT1_DEFAULT;

          /**
           * Try to determine the network interface.
           */
          public Bo() {
            // This odd-looking socket doesn't actually connect to the outside world,
            // nor does the target IP need to be reachable. By putting a datagram socket
            // into a connect state, we can determine our outgoing network interface.
            try (final DatagramSocket socket = new DatagramSocket()) {
              socket.connect(InetAddress.getByName("8.8.8.8"), 8888);
              this.dns1 = socket.getLocalAddress().getCanonicalHostName();

            } catch (Exception e) {
              this.i1 = InetAddress.getLoopbackAddress();
            }
          }

          /**
           * Property org.sdo.to0.ownersign.to1d.bo.dns1.
           *
           * <p>The value to use in protocol field TO0.OwnerSign.To1d.bo.dns1.
           *
           * <p>This property is optional, and defaults to the local host's DNS name, if available.
           */
          @Nullable
          public String getDns1() {
            return dns1;
          }

          public void setDns1(@Nullable String dns1) {
            this.dns1 = dns1;
          }

          /**
           * Property org.sdo.to0.ownersign.to1d.bo.i1.
           *
           * <p>The value to use in protocol field TO0.OwnerSign.To1d.bo.i1.
           *
           * <p>This property is optional, and defaults to the loopback IP if the local DNS name
           * is not available.
           */
          @Nullable
          public InetAddress getI1() {
            return i1;
          }

          public void setI1(@Nullable InetAddress i1) {
            this.i1 = i1;
          }

          /**
           * Property org.sdo.to0.ownersign.to1d.bo.port1.
           *
           * <p>The value to use in protocol field TO0.OwnerSign.to1d.bo.port1.
           *
           * <p>This property is optional, and defaults to {@value PORT1_DEFAULT}.
           */
          public Integer getPort1() {
            return port1;
          }

          public void setPort1(Integer port1) {
            this.port1 = port1;
          }
        }
      }
    }
  }

  /**
   * TO2 properties.
   */
  public static class To2 {

    private CipherBlockMode cipherBlockMode = CipherBlockMode.CTR;

    public CipherBlockMode getCipherBlockMode() {
      return cipherBlockMode;
    }

    public void setCipherBlockMode(CipherBlockMode cipherBlockMode) {
      this.cipherBlockMode = cipherBlockMode;
    }
  }
}

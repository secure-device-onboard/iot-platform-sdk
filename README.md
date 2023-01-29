***Note:***  The support for this product will be discontinued from Mar 31, 2023. We recommend users to switch to FDO components (https://github.com/secure-device-onboard/pri-fidoiot).

# About

The Secure Device Onboard (SDO) IoT Platform SDK is the SDO Owner protocol implementation. It is
divided into three different components: Owner Protocol Service (OPS), TO0 Scheduler (TO0Scheduler),
and Owner Companion Service (OCS). OCS manages customer-specific artifacts (OwnershipVoucher files,
ServiceInfo packages and others), and uses the service provided by TO0Scheduler to register
OwnershipVoucher files with the Rendezvous server through the Transfer of Ownership, step 0 (TO0)
protocol. OPS implements the Transfer of Ownership, step 2 (TO2) protocol and interacts with the OCS
to retrieve customer-specific artifacts.

# Getting Started with the SDO IoT Platform SDK

The following are the system constraints for the SDO IoT Platform SDK:
- Operating System: Ubuntu* 20.04
- Java* Development Kit 11
- Apache Maven* 3.5.4 (Optional) software for building the SDO IoT Platform SDK from source
- Java IDE (Optional) for convenience in modifying the source code (especially for customizing OCS).
- OpenSSL (Optional) for creating owner keys and key-store
- Docker* 19.03.15
- Docker compose 1.21.2
- Haveged

# Directory Structure

Details about the directory structure for iot-platform-sdk in the source and binary package are
described as follows.

The following notations are used in this document:

* `<sdo-iot-platform-sdk-root>`: iot-platform-sdk/

## Source Code Release
The SDO IoT Platform SDK source code has the following folders:

* `ocs`: A sample file-based implementation of the Owner Companion Service.

* `ops`: Core implementation of TO2 protocol.

* `to0scheduler`: Core implementation of TO0 protocol.

* `common`: Common beans and utilities that are imported by the other modules.

Example configuration files for the preceding three modules are placed in the following folders:

* `<ocs-config-dir>` : <sdo-iot-platform-sdk-root>/demo/ocs/config
* `<ops-config-dir>` : <sdo-iot-platform-sdk-root>/demo/ops/config
* `<to0scheduler-config-dir>` : <sdo-iot-platform-sdk-root>/demo/to0scheduler/config

After building the source code, the target binaries are found in the target
folder within the `ocs`, `ops`, and `to0scheduler` folders.

* `<ocs-bin-dir>`: <sdo-iot-platform-sdk-root>/demo/ocs/
* `<ops-bin-dir>`: <sdo-iot-platform-sdk-root>/demo/ops/
* `<to0scheduler-bin-dir>`: <sdo-iot-platform-sdk-root>/demo/to0scheduler/

# Building the SDO IoT Platform SDK

Artifacts are built and managed in the development environment via [Apache Maven](http://maven.apache.org/).

## Building WARs
```
mvn clean install
```

# Running the SDO IoT Platform SDK Demo

The SDO IoT Platform SDK demo can either be run manually, by running the binaries,
or it can be run using Docker scripts. To run each component in separate
machines, replace the keystore and truststore files. The certificates inside
these files must match the machine's IP/DNS where the component is running.
The Protocol Reference Implementation (PRI), containing Rendezvous and Device
can be used to run the demo. For more information on setting up PRI, follow the
product's README.

!!! NOTE 
    The IOT Platform SDK demo is provided solely to demonstrate interoperation of the IoT Platform SDK components (ocs, ops, and to0scheduler) with the Rendezvous Service and Device. _This demo is not recommended for use in any production capacity._  Appropriate security measures with respect to key-store management and configuration management should be considered while performing production deployment of any Secure Device Onboard component.

The following instructions follow these notations:

* `<sdo-pri-root>`: Base location of the PRI source code
* `<pri-rendezvous-dir>`: Base location used to run rendezvous, typically `<sdo-pri-root>/demo/rendezvous`
* `<pri-device-dir>`: Base location used to run device, typically `<sdo-pri-root>/demo/device`

## Updating the Proxy Information (Optional)

Update the proxy information in _JAVA_OPTIONS as
`_JAVA_OPTIONS=-Dhttp.proxyHost=http_proxy_host -Dhttp.proxyPort=http_proxy_port -Dhttps.proxyHost=https_proxy_host -Dhttps.proxyPort=https_proxy_port`, where
* `http_proxy_host`: Represents the http proxy hostname. Typically, it is an IP address or domain name in the proxy URL.
* `http_proxy_port`: Represents the http proxy port. Typically, it is the port number in the proxy URL.
* `https_proxy_host`: Represents the https proxy hostname. Typically, it is an IP address or domain name in the proxy URL.
* `https_proxy_port`: Represents the https proxy port. Typically, it is the port number in the proxy URL.
Specify the combination of the hostname and port information together for either http, https or both.
If no proxy needs to be specified, do not specify the flags in _JAVA_OPTIONS.

## Configuring Ondie ECDSA Properties

If supporting OnDie ECDSA devices, set `ORG_SDO_OPS_ONDIE_ECDSA_MATERIAL_PATH` property `<sdo-iot-platform-sdk-root>/demo/ops/ops.env`. The cachedir is used to store CRL and cert files and
can be populated by running the provided script (`<sdo-iot-platform-sdk-root>/scripts/onDieCache.py`) to load the directory with the files
from the cloud.

To run the onDieCache.py script, python3 must be installed.
Invoke as follows:
```
python3 onDieCache.py --cachedir ONDIE_PATH
```
where ONDIE_PATH = target location to store CRL and cert files. Typically, this will match the value of `ORG_SDO_OPS_ONDIE_ECDSA_MATERIAL_PATH`.
However, it is also possible to run the script on a different host and then copy the downloaded files into `ORG_SDO_OPS_ONDIE_ECDSA_MATERIAL_PATH`.

## Running the SDO IoT Platform SDK Demo Using the Docker Scripts

To run the SDO IoT Platform SDK demo using the Docker scripts, update the proxy information and
execute the rendezvous service and device as per the steps mentioned below.

Update the proxy information (if any) as per the steps outlined previously.

Open a new terminal window and start the rendezvous service:
```
$ cd <pri-rendezvous-dir>
$ ./rendezvous
```

Open a new terminal window and start the SDO IoT Platform SDK services:
```
$ cd <sdo-iot-platform-sdk-root>/demo
$ docker-compose up --build
```

Open a new terminal window and start the device service:
```
$ cd <pri-device-dir>
$ ./device
```

!!! NOTE 
    To re-trigger TO0 for a device for which TO0 was previously done and is currently active, please delete the file 'state.json' located at \<ocs-config-dir\>/db/v1/devices/\<deviceID\>/.

## Configuring Properties in SDO IoT Platform SDK

The properties for the services running inside the docker containers, can be configured at:
* `ocs.env`: Contains the configuration properties for OCS. The properties specified in this file, are used to set the corresponding Java properties for the OCS service, by the script 'run-ocs' at \<sdo-iot-platform-sdk-root\>/demo/ocs/.
* `ops.env`: Contains the configuration properties for OPS. The properties specified in this file, are used to set the corresponding Java properties for the OPS service, by the script 'run-ops' at \<sdo-iot-platform-sdk-root\>/demo/ops/.
* `to0scheduler.env`: Contains the configuration properties for To0Scheduler. The properties specified in this file, are used to set the corresponding Java properties for the To0Scheduler service, by the script 'run-to0scheduler' at \<sdo-iot-platform-sdk-root\>/demo/to0scheduler/.

Each Java property mentioned in column 'Property name' is directly mapped to the entry in column 'Corresponding property in Docker .env', in the table below.

| Property name                           | Applicable to          | Description                                | Value type           | Corresponding property in Docker .env |
|-----------------------------------------|------------------------|--------------------------------------------|----------------------|-----------------------------------|
| fs.root.dir                             | OCS                    | Root directory of the file-system database. | URI                     | FS_ROOT_DIR         |
| fs.owner.keystore                       | OCS                    | Path to the owner's keystore that contains multiple owner key-pairs. Any new owner entry must be added as 'PrivateKeyEntry' in the keystore. | URI                     | FS_OWNER_KEYSTORE           |
| fs.owner.keystore-password              | OCS                    | Password of the owner keystore as specified by the property fs.owner.keystore. Every key-pair within the keystore, that are stored as 'PrivateKeyEntry', MUST have the exact same password. | String                     | FS_OWNER_KEYSTORE_PASSWORD         |
| fs.devices.dir                          | OCS                    | Path where all the device information are stored, i.e the <device-guid> directories, relative to fs.root.dir. | URI                     | FS_DEVICES_DIR         |
| fs.values.dir                           | OCS                    | Path where service-info files are stored, i.e the 'values' directory, relative to fs.root.dir. | URI | FS_VALUES_DIR               |
| to0.rest.api                            | OCS                    | REST endpoint that points to the API hosted by To0Scheduler. | URL                     | TO0_REST_API          |
| to0.waitseconds                         | OCS                    | The suggested number of seconds, as sent by the To0Scheduler, until which TO0 is valid in Rendezvous. | Number                     | TO0_WAITSECONDS          |
| to0.scheduler.interval                  | OCS                    | The interval in seconds, at which this service makes call to the url specified by the property to0.rest.api, to schedule devices for TO0. | Number                     | TO0_SCHEDULER_INTERVAL            |
| to2.credential-reuse.enabled            | OCS                    | The flag that enables or disables the SDO Credential-Reuse condition. If true, the same GUID and Rendezvous Instructions are sent to the Owner (OPS). If false, new GUID and new/same Rendezvous information is sent.| Boolean                     | TO2_CREDENTIAL_REUSE_ENABLED       |
| to2.owner-resale.enabled                | OCS                    | The flag that determines whether the Resale protocol is supported by the Owner (OPS). If true, resale protocol is supported as per the specification. | Boolean                     | TO2_OWNER_RESALE_ENABLED          |
| thread.pool.size                        | OPS, To0Scheduler      | Total number of threads the TaskExecutor is initalized with. | Number                        | THREAD_POOL_SIZE         |
| org.sdo.epid.epid-online-url            | OPS                    | The URL of the Epid Verification Service. If this property is set, the online verification of EPID keys is performed by making a request to this URL. | URL                    | ORG_SDO_EPID_EPID_ONLINE_URL         |
| org.sdo.epid.test-mode                  | OPS                    | The test-mode parameter indicates whether we should be using EPID development (sandbox) service for verification of the EPID key. Only the sandbox service allows usage of test keys.| Boolean                        | ORG_SDO_EPID_TEST_MODE           |
| org.sdo.ops.ondie-ecdsa-material-path   | OPS                    | Path to the directpry that contains ondie ECDSA CRL files. | URI                        | ORG_SDO_OPS_ONDIE_ECDSA_MATERIAL_PATH        |
| org.sdo.ops.ondie-ecdsa-material-urls   | OPS                    | List of URLs seperated by 'comma (,)' as delimiter. The Ondie ECDSA CRL files will be fetched from the URLs listed.                          | List of URLs             | ORG_SDO_OPS_ONDIE_ECDSA_MATERIAL_URLS          |
| org.sdo.ops.ondie-ecdsa-material-update | OPS                    | If the value is set to 'true', the ondie ECDSA CRL files will be downloaded from the URLs listed in org.sdo.ops.ondie-ecdsa-material-urls and saved at org.sdo.ops.ondie-ecdsa-material-path.|  Boolean                       | ORG_SDO_OPS_ONDIE_ECDSA_MATERIAL_UPDATE           |
| rest.api.server                         | OPS, To0Scheduler      | The domain name and port at which OCS is running. | URL                        | REST_API_SERVER         |
| rest.api.device.state.path              | OPS, To0Scheduler      | REST endpoint path at OCS for device state operations. | URI                        | REST_API_DEVICE_STATE_PATH         |
| rest.api.voucher.path                   | OPS, To0Scheduler      | REST endpoint path at OCS for device voucher operations. | URI                        | REST_API_VOUCHER_PATH           |
| rest.api.error.path                     | OPS, To0Scheduler      | REST endpoint path at OCS for protocol error operations. | URI                        | REST_API_ERROR_PATH           |
| rest.api.signature.path                 | OPS, To0Scheduler      | REST endpoint path at OCS for signature operations. | URI                    | REST_API_SIGNATURE_PATH          |
| rest.api.session.path                   | OPS                    | REST endpoint path at OCS for session info operations. | URI                 | REST_API_SESSION_PATH           |
| rest.api.owner.resale.support.path      | OPS                    | REST endpoint path at OCS to fetch owner resale support flag per device. | URI               | REST_API_OWNER_RESALE_SUPPORT_PATH             |
| rest.api.setupinfo.path                 | OPS                    | REST endpoint path at OCS to get device setup information | URI                      | REST_API_SETUPINFO_PATH         |
| rest.api.serviceinfo.path               | OPS                    | REST endpoint path at OCS to fetch service-info list. |  URI                       | REST_API_SERVICEINFO_PATH            |
| rest.api.serviceinfo.value.path         | OPS                    | REST endpoint path at OCS to fetch service-info. | URI                        | REST_API_SERVICEINFO_VALUE_PATH           |
| rest.api.psi.path                       | OPS                    | REST endpoint path at OCS to get pre-service info. | URI                        | REST_API_PSI_PATH          |
| client.ssl.key-store-type               | OPS                    | The keystore-type. | String                     | CLIENT_SSL_KEYSTORE_TYPE             |
| client.ssl.trust-store-type             | OPS                    | The truststore-type. | String                    | CLIENT_SSL_TRUSTSTORE_TYPE          |
| client.ssl.key-store                    | OPS                    | Path to the keystore. | URI                    | CLIENT_SSL_KEYSTORE             |
| client.ssl.trust-store                  | OPS                    | Path to the truststore. | URI                     | CLIENT_SSL_TRUSTSTORE        |
| client.ssl.key-store-password           | OPS                    | The keystore password. | String                     | CLIENT_SSL_KEYSTORE_PASSWORD       |
| client.ssl.trust-store-password         | OPS                    | The truststore password. | String                     | CLIENT_SSL_TRUSTSTORE_PASSWORD        |
| session.pool.size                       | To0Scheduler           | The number of TO0 sessions that can be scheduled at a given time, subject to the availability of threads. | Number                | SESSION_POOL_SIZE          |
| org.sdo.to0.ownersign.to0d.ws           | To0Scheduler           | The default suggested number of seconds, as sent by the To0Scheduler, until which TO0 is valid in Rendezvous. This is overridden by 'to0.waitseconds' property of OCS as sent in the request. | java.time.Duration                | ORG_SDO_TO0_OWNERSIGN_TO0D_WS          |
| org.sdo.to0.ownersign.to1d.bo           | To0Scheduler           | Path to the file that contains Owner redirect information, i.e dns, ip address and port exposed by the owner.| URI                  | ORG_SDO_TO0_OWNERSIGN_TO1D_BO            |
| org.sdo.to0.tls.test-mode               | To0Scheduler           | Boolean property that determines whether the certificates as presented by the Rendezvous, is verified (false) or not (true) during TLS handshake. | Boolean                  | ORG_SDO_TO0_TLS_TEST_MODE           |
| server.port                      | OCS, OPS, To0Scheduler | The port where server is listening at. | Number                     | SERVER_PORT          |
| logging.config                   | OCS, OPS, To0Scheduler | Path of logback configuration file. | URI                     | LOGGING_CONFIG         |
| application.version              | OCS, OPS, To0Scheduler | Application version used for health check request. | Number                     | APPLICATION_VERSION          |
| server.ssl.key-store-type        | OCS, OPS, To0Scheduler | The keystore type. | String                     | SERVER_SSL_KEYSTORE_TYPE         | SERVER_SSL_TRUSTSTORE_TYPE          |
| server.ssl.trust-store-type      | OCS, OPS, To0Scheduler | The truststore-type. | String                     | SERVER_SSL_TRUSTSTORE_TYPE           |
| server.ssl.key-store             | OCS, OPS, To0Scheduler | Path to keystore file. The certificate inside keystore must have a certificate whose CN or SAN entries allows hostname verification to succeed. For example, the sample keystore works when the other services are running in the same machine, since the certificate has DNS as localhost. | URI                     | SERVER_SSL_KEYSTORE               |
| server.ssl.trust-store           | OCS, OPS, To0Scheduler | Path to truststore file. It Must contain the certifcates capable of verifying the certificates present in the incoming requests.| URI                     | SERVER_SSL_TRUSTSTORE         |
| server.ssl.key-store-password    | OCS, OPS, To0Scheduler | The keystore password | String                     | SERVER_SSL_KEYSTORE_PASSWORD         |
| server.ssl.trust-store-password  | OCS, OPS, To0Scheduler | The truststore password | String                     | SERVER_SSL_TRUSTSTORE_PASSWORD        |
| server.ssl.client-auth           | OCS, OPS, To0Scheduler | The TLS auth setter. For mTLS to work, it should be set to 'need', and 'warn' other-wise. | String                     | SERVER_SSL_CLIENT_AUTH              |
| server.ssl.ciphers               | OCS, OPS, To0Scheduler | The list of cipher suites that the server will be accepted for TLS handshake. It is a subset of cipher suites supported by the TLS version.  | String                     | SERVER_SSL_CIPHERS         |
| server.ssl.enabled-protocols     | OCS, OPS, To0Scheduler | The TLS version. Recommended to be set to TLSv1.3.|String                     | SERVER_SSL_ENABLED_PROTOCOLS        |

For more details on configuration and setup, refer to https://secure-device-onboard.github.io/docs/latest/iot-platform-sdk/running-the-demo/.

# About

The Secure Device Onboard (SDO) IoT Platform SDK is the SDO Owner protocol implementation. It is
divided into three different components: Owner Protocol Service (OPS), TO0 Scheduler (TO0Scheduler)
and Owner Companion Service (OCS). OCS manages customer-specific artifacts (OwnershipVoucher files,
ServiceInfo packages etc.), and uses the service provided by TO0Scheduler to register
OwnershipVoucher files with the Rendezvous server through the Transfer of Ownership, step 0 (TO0)
protocol. OPS implements the Transfer of Ownership, step 2 (TO2) protocol and interacts with the OCS
to retrieve customer-specific artifacts.

# Getting started with the SDO IoT Platform SDK

The following are the system constraints for the SDO IoT Platform SDK:
- Operating System: Ubuntu* 18.04
- Java* Development Kit 11.0.4 (Java* Development Kit 11.0.5 or later are not supported)
- Apache Maven* 3.5.0 (Optional) software for building the SDO IoT Platform SDK from source
- Java IDE (Optional) for convenience in modifying the source code (especially for customizing OCS).
- OpenSSL (Optional) for creating owner keys and key-store
- Docker 18.09
- Docker compose 1.21.2
- Haveged


# Directory structure

Details about the directory structure for iot-platform-sdk in the source and binary package are
described as follows.

The following notations are used in this document:

* `<sdo-iot-platform-sdk_root>`: iot-platform-sdk/

## Source Code Release
The SDO IoT Platform SDK source code has the following folders:

* `ocs`: A sample file-based implementation of the Owner Companion Service.

* `ops`: Core implementation of TO2 protocol.

* `to0scheduler`: Core implementation of TO0 protocol.

* `common`: Common beans and utilities that are imported by the other modules.

Configuration files for the preceding three modules are placed in the following folders:

* `<ocs_config_dir>` : <sdo-iot-platform-sdk_root>/docker/ocs/config
* `<ops_config_dir>` : <sdo-iot-platform-sdk_root>/docker/ops/config
* `<to0scheduler_config_dir>` : <sdo-iot-platform-sdk_root>/docker/to0scheduler/config

While building the source code, the target binaries are found in the target
folder within the `ocs`, `ops`, and `to0scheduler` folders.

* `<ocs_dir>`: <sdo-iot-platform-sdk_root>/ocs/fsimpl/target
* `<ops_dir>`: <sdo-iot-platform-sdk_root>/ops/restimpl/target
* `<to0scheduler_dir>`: <sdo-iot-platform-sdk_root>/to0scheduler/to0serviceimpl/target

## Source Binary Release
The binary files of different components are stored within <sdo_bin_dir>. Each subfolder
within <sdo_bin_dir> contains the binary files and required artifacts to run a particular component.

# Building the SDO IoT Platform SDK

Artifacts are built and managed in the development environment via [Apache Maven](http://maven.apache.org/).

## Building WARs
```
mvn clean install
```

# Running the SDO IoT Platform SDK demo

The SDO IoT Platform SDK demo can either be run manually, by running the binaries,
or it can be run using Docker scripts. To run each component in separate
machines, replace the keystore and truststore files. The certificates inside
these files must match the machine's IP/DNS where the component is running.

The following instructions follow these notations:

* `<sdo_bin_dir>`: Base location of the SDO release binaries.
* `<cri_bin_dir>`: Base location of the cri release binaries, typically `<sdo_bin_dir>/cri/`
* `<rendezvous_dir>`: Base location used to run rendezvous, typically `<sdo_bin_dir>/demo/rendezvous`
* `<device_dir>`: Base location used to run device, typically `<sdo_bin_dir>/demo/device`

## Updating the proxy information (Optional)

Update the proxy information specified using the below mentioned properties:
* `http_proxy_host`: Represents the http proxy hostname. Typically, it is an IP address or domain name in the proxy URL.
* `http_proxy_port`: Represents the http proxy port. Typically, it is the port number in the proxy URL.
* `https_proxy_host`: Represents the https proxy hostname. Typically, it is an IP address or domain name in the proxy URL.
* `https_proxy_port`: Represents the https proxy port. Typically, it is the port number in the proxy URL.
Specify the combination of the hostname and port information together for either http, https or both.
If no proxy needs to be specified, leave the fields blank.

These properties are present in the following script files:
* <ops_config_dir>/run-ops
* <ocs_config_dir>/run-ocs
* <to0scheduler_config_dir>/run-to0scheduler

## Running the SDO IoT Platform SDK demo manually

Update the proxy information (if any) as per the steps outlined previously.

Open a new terminal window and start the rendezvous service:
```
$ cd <rendezvous_dir>
$ ./rendezvous
```

Open a new terminal window and start the to0scheduler service:
```
$ cd <to0scheduler_config_dir>
$ ./run-to0scheduler
```

Open a new terminal window and start the ocs service:
```
$ cd <ocs_config_dir>
$ ./run-ocs
```

Open a new terminal window and start the ops service:
```
$ cd <ops_config_dir>
$ ./run-ops
```

Open a new terminal window and start the device service:
```
$ cd <device_dir>
$ ./device
```

## Running the SDO IoT Platform SDK demo using the Docker scripts

To run the SDO IoT Platform SDK demo using the Docker scripts, update the proxy information and
execute the rendezvous service and device as per the steps mentioned above.

Update the proxy information (if any) as per the steps outlined previously.

Open a new terminal window and start the rendezvous service:
```
$ cd <rendezvous_dir>
$ ./rendezvous
```

Open a new terminal window and start the SDO IoT Platform SDK services:
```
$ cd <sdo-iot-platform-sdk_root>/docker
$ sudo docker-compose up
```

Open a new terminal window and start the device service:
```
$ cd <device_dir>
$ ./device
```

NOTE: To re-trigger TO0 for a device for which TO0 was previously done and is currently active,
please delete the file 'state.json' located at <ocs_config_dir>/db/v1/devices/<deviceID>/.
For more details, refer to the README file provided in <demo_root>.

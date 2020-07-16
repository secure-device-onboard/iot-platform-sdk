# Copyright 2020 Intel Corporation
# SPDX-License-Identifier: Apache 2.0

FROM ubuntu:18.04
ARG _JAVA_OPTIONS
ENV _JAVA_OPTIONS=${_JAVA_OPTIONS}
RUN apt-get update && apt-get install -y libssl-dev
RUN apt-get install -y openjdk-11-jdk
RUN apt-get install -y haveged
# Create a user 'sdouser'. If the user name is updated, please update the same in docker-compose.yaml.
RUN useradd -ms /bin/bash sdouser
USER sdouser
WORKDIR /home/sdouser
RUN mkdir ./ocs
RUN mkdir ./ocs/config/
COPY ocs/config/logback-spring.xml ./ocs/config
COPY ocs/config/ocs-keystore.p12 ./ocs/config
COPY ocs/config/ocs-truststore ./ocs/config
COPY ocs/config/run-ocs ./ocs/config/run-ocs
COPY ocs/ocsfs-*.war ./ocs
EXPOSE 9009
WORKDIR /home/sdouser/ocs/config/
CMD ["chmod", "+x", "run-ocs"]
CMD ["bash", "run-ocs"]

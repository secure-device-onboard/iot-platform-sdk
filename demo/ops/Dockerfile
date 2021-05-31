# Copyright 2020 Intel Corporation
# SPDX-License-Identifier: Apache 2.0

FROM ubuntu:20.04
ENV DEBIAN_FRONTEND=noninteractive
ARG _JAVA_OPTIONS
ENV _JAVA_OPTIONS=${_JAVA_OPTIONS}
RUN apt-get -y update && apt-get -y upgrade && apt-get install -y libssl-dev
RUN apt-get install -y openjdk-11-jdk
RUN apt-get install -y haveged
# Create a user 'sdouser'.
RUN useradd -ms /bin/bash sdouser
USER sdouser
WORKDIR /home/sdouser/
RUN mkdir ./ops
RUN mkdir ./ops/config
COPY ops/config/logback-spring.xml ./ops/config
COPY ops/config/ops-keystore.p12 ./ops/config
COPY ops/config/ops-truststore ./ops/config
COPY ops/config/run-ops ./ops/config/run-ops
COPY ops/ops-*.war ./ops
EXPOSE 8042
WORKDIR /home/sdouser/ops/config/
CMD ["chmod", "+x", "run-ops"]
CMD ["bash", "run-ops"]

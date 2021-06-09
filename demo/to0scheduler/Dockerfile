# Copyright 2020 Intel Corporation
# SPDX-License-Identifier: Apache 2.0

FROM ubuntu:20.04
ENV DEBIAN_FRONTEND=noninteractive
ARG _JAVA_OPTIONS
ENV _JAVA_OPTIONS=${_JAVA_OPTIONS}
RUN apt-get -y update && apt-get -y upgrade && apt-get install -y libssl-dev
RUN apt-get install -y openjdk-11-jdk
RUN apt-get install -y haveged
# Create a user 'sdouser'. If the user name is updated, please update the same in docker-compose.yaml.
RUN useradd -ms /bin/bash sdouser
USER sdouser
WORKDIR /home/sdouser/
RUN mkdir ./to0scheduler
RUN mkdir ./to0scheduler/config/
COPY to0scheduler/config/logback-spring.xml ./to0scheduler/config
# This is an example implementation using simplified credentials. This must be changed while performing production deployment.
COPY to0scheduler/config/to0service-keystore.p12 ./to0scheduler/config
# This is an example implementation using simplified credentials. This must be changed while performing production deployment.
COPY to0scheduler/config/to0service-truststore ./to0scheduler/config
COPY to0scheduler/config/run-to0scheduler ./to0scheduler/config/run-to0scheduler
COPY to0scheduler/to0scheduler-*.war ./to0scheduler
EXPOSE 8049
WORKDIR /home/sdouser/to0scheduler/config/
CMD ["chmod", "+x", "run-to0scheduler"]
CMD ["bash", "run-to0scheduler"]

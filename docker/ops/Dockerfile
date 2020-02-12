# Copyright 2020 Intel Corporation
# SPDX-License-Identifier: Apache 2.0

FROM ubuntu:18.04
RUN apt-get update
RUN apt-get install -y libssl-dev
RUN apt-get install -y wget
RUN wget https://download.java.net/java/ga/jdk11/openjdk-11_linux-x64_bin.tar.gz
RUN sha256sum openjdk-11_linux-x64_bin.tar.gz
RUN tar xzvf openjdk-11_linux-x64_bin.tar.gz
RUN mkdir /usr/lib/jvm
RUN mv jdk-11 /usr/lib/jvm/openjdk-11-manual-installation/
RUN update-alternatives --install /usr/bin/java java /usr/lib/jvm/openjdk-11-manual-installation/bin/java 1
RUN update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/openjdk-11-manual-installation/bin/javac 1
RUN apt-get install -y haveged
COPY ops /ops
EXPOSE 8042
WORKDIR /ops/config/
CMD ["chmod", "+x", "run-ops"]
CMD ["./run-ops"]

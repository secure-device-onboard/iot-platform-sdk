FROM ubuntu:bionic

RUN apt update && \
    apt install -y git  \
    openjdk-11-jdk-headless \
    maven 

# Create a user 'sdouser'. If the user name is updated, please update the same in docker-compose.yaml.
RUN useradd -ms /bin/bash sdouser
RUN mkdir -p /home/sdouser/iot-platform-sdk/ ; chown -R sdouser:sdouser /home/sdouser/iot-platform-sdk/
USER sdouser

WORKDIR /home/sdouser/iot-platform-sdk/
ENTRYPOINT ./build/build.sh

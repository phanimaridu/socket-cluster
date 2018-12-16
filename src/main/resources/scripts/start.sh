#!/bin/bash
BASE_DIR=/home/bin/socket-cluster
java -jar -Dudps.serverConfigFilePath=${BASE_DIR}/server-config.json  ${BASE_DIR}/app.jar 2>&1 > ${BASE_DIR}/server.log &
echo $! > ${BASE_DIR}/pid
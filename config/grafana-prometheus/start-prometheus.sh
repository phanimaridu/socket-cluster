#!/bin/bash
BASE_DIR=/home/vm-user/playground/install/prometheus
${BASE_DIR}/prometheus --config.file=${BASE_DIR}/prometheus.yaml --web.enable-admin-api --web.listen-address=:3036 2>&1 >> ${BASE_DIR}/console.log &
echo $! > ${BASE_DIR}/pid

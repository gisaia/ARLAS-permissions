#!/bin/bash
set -o errexit -o pipefail

function clean_docker {
  ./scripts/docker-clean.sh
  echo "===> clean maven repository"
	docker run --rm \
		-w /opt/maven \
		-v $PWD:/opt/maven \
		-v $HOME/.m2:/root/.m2 \
		maven:3.8.4-openjdk-17 \
		mvn clean
}

function clean_exit {
  ARG=$?
  echo "===> Exit status = ${ARG}"
  echo "===> arlas-permissions-server logs"
  docker logs arlas-permissions-server
  clean_docker
  exit $ARG
}
trap clean_exit EXIT

# GO TO PROJECT PATH
SCRIPT_PATH=`cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd`
cd ${SCRIPT_PATH}/../..

# CHECK ALV2 DISCLAIMER
if [ $(find ./*/src -name "*.java" -exec grep -L Licensed {} \; | wc -l) -gt 0 ]; then
    echo "[ERROR] ALv2 disclaimer is missing in the following files :"
    find ./*/src -name "*.java" -exec grep -L Licensed {} \;
    exit -1
fi

function start_stack() {
  mkdir -p /tmp/auth
  ./scripts/docker-clean.sh
  ./scripts/docker-run.sh --build
}

function test_permissions_server() {
    export ARLAS_AUTH_ENABLED=false
    start_stack
    docker run --rm \
        -w /opt/maven \
        -v $PWD:/opt/maven \
        -v $HOME/.m2:/root/.m2 \
        -e ARLAS_SERVER_HOST="arlas-permissions-server" \
        -e ARLAS_SERVER_PREFIX="arlas_permissions_server" \
        -e ARLAS_SERVER_APP_PATH=${ARLAS_SERVER_APP_PATH} \
        -e ARLAS_SERVER_STORAGE="/tmp" \
        --net arlas_default \
        maven:3.8.4-openjdk-17 \
        mvn -Dit.test=PermissionsIT verify -DskipTests=false -DfailIfNoTests=false
}

test_permissions_server
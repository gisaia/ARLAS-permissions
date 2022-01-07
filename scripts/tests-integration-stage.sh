#!/bin/bash
set -e

function clean_docker {
  ./scripts/docker-clean.sh
  echo "===> clean maven repository"
	docker run --rm \
		-w /opt/maven \
		-v $PWD:/opt/maven \
		-v $HOME/.m2:/root/.m2 \
		maven:3.8.2-openjdk-17 \
		mvn clean
}

function clean_exit {
  ARG=$?
	echo "===> Exit stage ${STAGE} = ${ARG}"
  clean_docker
  exit $ARG
}
trap clean_exit EXIT

usage(){
	echo "Usage: ./tests-integration-stage.sh --stage=REST"
	exit 1
}

for i in "$@"
do
case $i in
    --stage=*)
    STAGE="${i#*=}"
    shift # past argument=value
    ;;
    *)
            # unknown option
    ;;
esac
done

# GO TO PROJECT PATH
SCRIPT_PATH=`cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd`
cd ${SCRIPT_PATH}/..

# CHECK ALV2 DISCLAIMER
if [ $(find ./*/src -name "*.java" -exec grep -L Licensed {} \; | wc -l) -gt 0 ]; then
    echo "[ERROR] ALv2 disclaimer is missing in the following files :"
    find ./*/src -name "*.java" -exec grep -L Licensed {} \;
    exit -1
fi

if [ -z ${STAGE+x} ]; then usage; else echo "Tests stage : ${STAGE}"; fi

function start_stack() {
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
        maven:3.8.2-openjdk-17 \
        mvn -Dit.test=PermissionsIT verify -DskipTests=false -DfailIfNoTests=false
}


function test_doc() {
    ./mkDocs.sh
}

if [ ! -z ${DOCKER_USERNAME+x} ] && [ ! -z ${DOCKER_PASSWORD+x} ]
then
  echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
fi

if [ "$STAGE" == "REST" ]; then test_permissions_server; fi
if [ "$STAGE" == "DOC" ]; then test_doc; fi


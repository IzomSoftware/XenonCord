#!/bin/sh

set -eu

CWD=$(pwd)

# if [ -n "${1:+x}" ]; then
# 	if [ "$1" = "clean" ]; then
# 		echo "Cleaning mbedtls"
# 		(cd mbedtls && git reset --hard && git clean -fdx && cd framework && git reset --hard && git clean -fdx)
# 		echo "Cleaning zlib"
# 		(cd zlib && git reset --hard && git clean -fdx)
# 	fi
# fi

(cd $CWD/native/ && ./build-native.sh)
(cd $CWD && mvn clean install)
(cd $CWD/test_run/ && cp ../XenonCord.jar ./)
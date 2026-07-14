#!/bin/sh

set -eu

CWD=$(pwd)

(cd native/ && git submodule update --init --recursive)
(cd $CWD/native/ && ./compile-native.sh)
(cd $CWD && mvn clean install)
(mkdir -p $CWD/test_run/ && rm -f $CWD/test_run/XenonCord.jar && cd $CWD/test_run/ && mv ../XenonCord.jar ./)

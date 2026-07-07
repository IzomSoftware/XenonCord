#!/bin/sh

set -eu

CWD=$(pwd)

(cd native/ && git submodule update --init --recursive)
(cd $CWD/native/ && ./compile-native.sh)
(cd $CWD && rm -f XenonCord.jar && mvn clean install)
mkdir -p $CWD/test_run/
(cd $CWD/test_run/ && rm -f ./XenonCord.jar)
(mv $CWD/XenonCord.jar $CWD/test_run/)
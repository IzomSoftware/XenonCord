#!/bin/sh

set -eu

CWD=$(pwd)
NPROC=$(nproc)
MAKE="make -j$NPROC"
C_DIR=$CWD/src/main/c
LIBDEFLATE=$CWD/libdeflate
MBEDTLS=$CWD/mbedtls
RESOURCES=$CWD/src/main/resources
JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))

echo "COMPILE: mbedtls"
(cd mbedtls && CFLAGS="-fPIC -I$C_DIR/src -DMBEDTLS_USER_CONFIG_FILE='<mbedtls_custom_config.h>'" $MAKE no_test)
echo "DONE"

echo "COMPILE: libdeflate"
(cd libdeflate && cmake -DCMAKE_C_FLAGS="-fPIC" . && $MAKE)
echo "DONE"
# echo "Compiling zlib"
# (cd zlib && CFLAGS="-fPIC -DNO_GZIP" ./configure --static && make -j$NPROC)

echo "COMPILE: BungeeNative"
(cd $C_DIR && cmake -D JAVA_HOME=$JAVA_HOME -D LIBDEFLATE=$LIBDEFLATE -D MBEDTLS=$MBEDTLS . && $MAKE && mv $C_DIR/native-compress.so $RESOURCES && mv $C_DIR/native-cipher.so $RESOURCES)
echo "DONE"

# $CC $CFLAGS -Izlib -o NativeCompressImpl.o src/main/c/NativeCompressImpl.c
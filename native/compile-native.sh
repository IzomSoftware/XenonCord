#!/bin/sh

set -eu

CWD=$(pwd)
NPROC=$(nproc)
MAKE="make -j$NPROC"
C_DIR=$CWD/src/main/c
LIBDEFLATE=$CWD/libdeflate
MBEDTLS=$CWD/mbedtls
RESOURCES=$CWD/src/main/resources
NATIVE_COMPRESS=$C_DIR/NativeCompress
NATIVE_CIPHER=$C_DIR/NativeCipher
JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))

echo "COMPILE: mbedtls"
(cd mbedtls && CFLAGS="-fPIC -I$NATIVE_CIPHER -DMBEDTLS_USER_CONFIG_FILE='<mbedtls_custom_config.h>'" $MAKE no_test)
echo "DONE"

echo "COMPILE: libdeflate"
(cd libdeflate && cmake -DCMAKE_C_FLAGS="-fPIC" . && $MAKE)
echo "DONE"
# echo "Compiling zlib"
# (cd zlib && CFLAGS="-fPIC -DNO_GZIP" ./configure --static && make -j$NPROC)

echo "COMPILE: NativeCompress"
(cd $NATIVE_COMPRESS && cmake -D JAVA_HOME=$JAVA_HOME -D LIBDEFLATE=$LIBDEFLATE . && $MAKE && mv $NATIVE_COMPRESS/native-compress.so $RESOURCES)
echo "DONE"

echo "COMPILE: NativeCipher"
(cd $NATIVE_CIPHER && cmake -D JAVA_HOME=$JAVA_HOME -D MBEDTLS=$MBEDTLS . && $MAKE && mv $NATIVE_CIPHER/native-cipher.so $RESOURCES)
echo "DONE"

# $CC $CFLAGS -Izlib -o NativeCompressImpl.o src/main/c/NativeCompressImpl.c
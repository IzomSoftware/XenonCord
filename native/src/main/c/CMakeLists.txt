cmake_minimum_required(VERSION 3.10)

project(BungeeCipher VERSION 1 LANGUAGES C)

add_library(
  native-cipher
  SHARED
    src/NativeCipherImpl.c src/net_md_5_bungee_jni_cipher_NativeCipherImpl.h
    src/mbedtls_custom_config.h)

add_library(
  native-compress
  SHARED
    src/NativeCompressImpl.c src/net_md_5_bungee_jni_zlib_NativeCompressImpl.h
    src/shared.c src/shared.h)

set_target_properties(native-cipher PROPERTIES PREFIX "")
set_target_properties(native-compress PROPERTIES PREFIX "")
execute_process(
  COMMAND /bin/sh -c "dirname $(dirname $(readlink -f $(which java)))"
  OUTPUT_VARIABLE JAVA_HOME
  OUTPUT_STRIP_TRAILING_WHITESPACE
)
execute_process(
  COMMAND /bin/sh -c "cd ../../../libdeflate && pwd"
  OUTPUT_VARIABLE LIBDEFLATE
  OUTPUT_STRIP_TRAILING_WHITESPACE
)
execute_process(
  COMMAND /bin/sh -c "cd ../../../mbedtls && pwd"
  OUTPUT_VARIABLE MBEDTLS
  OUTPUT_STRIP_TRAILING_WHITESPACE
)
target_include_directories(
  native-cipher
  PRIVATE ${MBEDTLS}/include ${JAVA_HOME}/include/ ${JAVA_HOME}/include/linux)

target_include_directories(
  native-compress
  PRIVATE ${LIBDEFLATE} ${JAVA_HOME}/include/ ${JAVA_HOME}/include/linux/)

if(MSVC)
  target_compile_options(native-cipher PRIVATE /W4)
else()
  target_compile_options(native-cipher PRIVATE -Wall -Wextra -Wpedantic)
endif()

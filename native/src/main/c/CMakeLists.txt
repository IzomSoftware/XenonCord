cmake_minimum_required(VERSION 3.10)

project(BungeeCipher VERSION 1 LANGUAGES C)

add_compile_options(-O2 -march=native -flto)

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
  OUTPUT_VARIABLE JAVA_HOME OUTPUT_STRIP_TRAILING_WHITESPACE)
set(LIBDEFLATE "${CMAKE_SOURCE_DIR}/../../../libdeflate")
set(MBEDTLS "${CMAKE_SOURCE_DIR}/../../../mbedtls")

target_include_directories(
  native-cipher
  PRIVATE ${MBEDTLS}/include ${JAVA_HOME}/include/ ${JAVA_HOME}/include/linux)

target_link_libraries(native-cipher
                      ${MBEDTLS}/library/libmbedtls.a)

target_include_directories(
  native-compress
  PRIVATE ${LIBDEFLATE} ${JAVA_HOME}/include/ ${JAVA_HOME}/include/linux/)

target_link_libraries(native-compress
                      ${LIBDEFLATE}/libdeflate.a)

if(MSVC)
  target_compile_options(native-cipher PRIVATE /W4)
else()
  target_compile_options(native-cipher PRIVATE -Wall -Wextra -Wpedantic)
endif()

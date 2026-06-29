cmake_minimum_required(VERSION 3.10)

project(BungeeCipher VERSION 1 LANGUAGES C)

add_library(
  native-cipher
  SHARED
    src/NativeCipherImpl.c src/net_md_5_bungee_jni_cipher_NativeCipherImpl.h
    src/mbedtls_custom_config.h)

set_target_properties(native-cipher PROPERTIES PREFIX "")
target_include_directories(
  native-cipher
  PRIVATE ${MBEDTLS}/include ${JAVA_HOME}/include/ ${JAVA_HOME}/include/linux)

if(MSVC)
  target_compile_options(native-cipher PRIVATE /W4)
else()
  target_compile_options(native-cipher PRIVATE -Wall -Wextra -Wpedantic)
endif()

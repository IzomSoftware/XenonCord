cmake_minimum_required(VERSION 3.10)

project(BungeeNative VERSION 1 LANGUAGES C)

add_library(
  native-compress
  SHARED
    src/NativeCompressImpl.c src/net_md_5_bungee_jni_zlib_NativeCompressImpl.h
    src/shared.c src/shared.h)

set_target_properties(native-compress PROPERTIES PREFIX "")

target_include_directories(
  native-compress
  PRIVATE ${LIBDEFLATE} ${JAVA_HOME}/include/ ${JAVA_HOME}/include/linux/)

if(MSVC)
  target_compile_options(native-compress PRIVATE /W4)
else()
  target_compile_options(native-compress PRIVATE -Wall -Wextra -Wpedantic)
endif()

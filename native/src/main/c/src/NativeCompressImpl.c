#include "shared.h"
#include <libdeflate.h>
#include <stdlib.h>
#include <string.h>
#if !defined(__aarch64__)
#include "cpuid_helper.h"
#endif
#include "net_md_5_bungee_jni_zlib_NativeCompressImpl.h"

typedef unsigned char byte;

static jclass classID;
static jfieldID consumedID;
static jfieldID finishedID;
static jmethodID makeExceptionID;

typedef struct {
  struct libdeflate_compressor *compressor;
  struct libdeflate_decompressor *decompressor;
  byte *compressed_buf;
  size_t compressed_size;
  size_t compressed_offset;
} libdeflate_ctx;

void JNICALL Java_net_md_15_bungee_jni_zlib_NativeCompressImpl_initFields(
    JNIEnv *env, jclass clazz) {
  classID = clazz;
  // We trust that these will be there
  consumedID = (*env)->GetFieldID(env, clazz, "consumed", "I");
  finishedID = (*env)->GetFieldID(env, clazz, "finished", "Z");
  makeExceptionID = (*env)->GetMethodID(
      env, clazz, "makeException",
      "(Ljava/lang/String;I)Lnet/md_5/bungee/jni/NativeCodeException;");
}

jint throwException(JNIEnv *env, const char *message, int err) {
  jstring jMessage = (*env)->NewStringUTF(env, message);
  jthrowable throwable = (jthrowable)(*env)->CallStaticObjectMethod(
      env, classID, makeExceptionID, jMessage, err);
  return (*env)->Throw(env, throwable);
}

JNIEXPORT jboolean JNICALL
Java_net_md_15_bungee_jni_zlib_NativeCompressImpl_checkSupported(JNIEnv *env,
                                                                 jobject obj) {
  return JNI_TRUE;
}

void JNICALL Java_net_md_15_bungee_jni_zlib_NativeCompressImpl_reset(
    JNIEnv *env, jobject obj, jlong ctx, jboolean compress) {
  libdeflate_ctx *lctx = (libdeflate_ctx *)(intptr_t)ctx;
  if (lctx->compressed_buf) {
    free(lctx->compressed_buf);
    lctx->compressed_buf = NULL;
  }
  lctx->compressed_size = 0;
  lctx->compressed_offset = 0;
}

void JNICALL Java_net_md_15_bungee_jni_zlib_NativeCompressImpl_end(
    JNIEnv *env, jobject obj, jlong ctx, jboolean compress) {
  libdeflate_ctx *lctx = (libdeflate_ctx *)(intptr_t)ctx;
  if (!lctx)
    return;
  if (lctx->compressor)
    libdeflate_free_compressor(lctx->compressor);
  if (lctx->decompressor)
    libdeflate_free_decompressor(lctx->decompressor);
  if (lctx->compressed_buf)
    free(lctx->compressed_buf);
  free(lctx);
}

jlong JNICALL Java_net_md_15_bungee_jni_zlib_NativeCompressImpl_init(
    JNIEnv *env, jobject obj, jboolean compress, jint level) {
  libdeflate_ctx *lctx = (libdeflate_ctx *)calloc(1, sizeof(libdeflate_ctx));
  if (!lctx) {
    throwOutOfMemoryError(env, "Failed to allocate context");
    return 0;
  }
  if (compress) {
    lctx->compressor = libdeflate_alloc_compressor(level);
    if (!lctx->compressor) {
      free(lctx);
      throwOutOfMemoryError(env, "Failed to allocate compressor");
      return 0;
    }
  } else {
    lctx->decompressor = libdeflate_alloc_decompressor();
    if (!lctx->decompressor) {
      free(lctx);
      throwOutOfMemoryError(env, "Failed to allocate decompressor");
      return 0;
    }
  }
  return (jlong)(intptr_t)lctx;
}

jint JNICALL Java_net_md_15_bungee_jni_zlib_NativeCompressImpl_process(
    JNIEnv *env, jobject obj, jlong ctx, jlong in, jint inLength, jlong out,
    jint outLength, jboolean compress) {
  libdeflate_ctx *lctx = (libdeflate_ctx *)(intptr_t)ctx;

  (*env)->SetIntField(env, obj, consumedID, 0);

  if (compress) {
    if (!lctx->compressed_buf) {
      size_t bound =
          libdeflate_zlib_compress_bound(lctx->compressor, (size_t)inLength);
      lctx->compressed_buf = (byte *)malloc(bound);
      if (!lctx->compressed_buf) {
        throwOutOfMemoryError(env, "Failed to allocate compression buffer");
        return -1;
      }
      size_t actual = libdeflate_zlib_compress(
          lctx->compressor, (const void *)(intptr_t)in, (size_t)inLength,
          lctx->compressed_buf, bound);
      if (actual == 0) {
        throwException(env, "compress returned 0", 0);
        return -1;
      }
      lctx->compressed_size = actual;
      lctx->compressed_offset = 0;
      (*env)->SetIntField(env, obj, consumedID, inLength);
    }
    size_t remaining = lctx->compressed_size - lctx->compressed_offset;
    size_t to_copy =
        remaining < (size_t)outLength ? remaining : (size_t)outLength;
    memcpy((void *)(intptr_t)out,
           lctx->compressed_buf + lctx->compressed_offset, to_copy);
    lctx->compressed_offset += to_copy;
    if (lctx->compressed_offset >= lctx->compressed_size) {
      (*env)->SetBooleanField(env, obj, finishedID, JNI_TRUE);
    }
    return (jint)to_copy;
  } else {
    size_t actual_in = 0;
    size_t actual_out = 0;
    enum libdeflate_result result = libdeflate_zlib_decompress_ex(
        lctx->decompressor, (const void *)(intptr_t)in, (size_t)inLength,
        (void *)(intptr_t)out, (size_t)outLength, &actual_in, &actual_out);
    if (result != LIBDEFLATE_SUCCESS) {
      throwException(env, "decompress failed", (int)result);
      return -1;
    }
    (*env)->SetIntField(env, obj, consumedID, (jint)actual_in);
    (*env)->SetBooleanField(env, obj, finishedID, JNI_TRUE);
    return (jint)actual_out;
  }
}
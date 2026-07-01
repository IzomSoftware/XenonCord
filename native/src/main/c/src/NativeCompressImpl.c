#include "net_md_5_bungee_jni_zlib_NativeCompressImpl.h"
#include "shared.h"
#include <libdeflate.h>
#include <stdlib.h>
#include <string.h>

typedef unsigned char byte;

static jclass classID;
static jfieldID consumedID;
static jfieldID finishedID;
static jmethodID makeExceptionID;

typedef struct {
  size_t compressed_size;
  size_t compressed_offset;
  byte *compressed_buf;
  struct libdeflate_compressor *compressor;
  struct libdeflate_decompressor *decompressor;
} libdeflate_ctx;

void JNICALL Java_net_md_15_bungee_jni_zlib_NativeCompressImpl_initFields(
    JNIEnv *env, jclass clazz) {
  const JNIEnv toEnv = *env;

  classID = clazz;
  // We trust that these will be there
  consumedID = toEnv->GetFieldID(env, clazz, "consumed", "I");
  finishedID = toEnv->GetFieldID(env, clazz, "finished", "Z");
  makeExceptionID = toEnv->GetMethodID(
      env, clazz, "makeException",
      "(Ljava/lang/String;I)Lnet/md_5/bungee/jni/NativeCodeException;");
}

jint throwException(JNIEnv *env, const char *message, int err) {
  const JNIEnv toEnv = *env;
  const jstring jMessage = toEnv->NewStringUTF(env, message);

  return toEnv->Throw(env, toEnv->CallStaticObjectMethod(
                               env, classID, makeExceptionID, jMessage, err));
}

JNIEXPORT jboolean JNICALL
Java_net_md_15_bungee_jni_zlib_NativeCompressImpl_checkSupported(JNIEnv *env,
                                                                 jobject obj) {
  return JNI_TRUE;
}

void JNICALL Java_net_md_15_bungee_jni_zlib_NativeCompressImpl_reset(
    JNIEnv *env, jobject obj, jlong ctx, jboolean compress) {
  libdeflate_ctx *lctx = (libdeflate_ctx *)ctx;

  free(lctx->compressed_buf);
  lctx->compressed_buf = NULL;
  lctx->compressed_size = 0;
  lctx->compressed_offset = 0;
}

void JNICALL Java_net_md_15_bungee_jni_zlib_NativeCompressImpl_end(
    JNIEnv *env, jobject obj, jlong ctx, jboolean compress) {
  libdeflate_ctx *lctx = (libdeflate_ctx *)ctx;

  if (!lctx)
    return;
  if (lctx->compressor)
    libdeflate_free_compressor(lctx->compressor);
  if (lctx->decompressor)
    libdeflate_free_decompressor(lctx->decompressor);
  free(lctx->compressed_buf);
  free(lctx);
}

jlong JNICALL Java_net_md_15_bungee_jni_zlib_NativeCompressImpl_init(
    JNIEnv *env, jobject obj, jboolean compress, jint level) {
  const JNIEnv toEnv = *env;
  libdeflate_ctx *lctx = calloc(1, sizeof(libdeflate_ctx));

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
  return (intptr_t)lctx;
}

jint JNICALL Java_net_md_15_bungee_jni_zlib_NativeCompressImpl_process(
    JNIEnv *env, jobject obj, jlong ctx, jlong in, jint inLength, jlong out,
    jint outLength, jboolean compress) {
  const JNIEnv toEnv = *env;
  libdeflate_ctx *lctx = (libdeflate_ctx *)ctx;
  const byte *in_ptr = (byte *)in;
  byte *out_ptr = (byte *)out;

  toEnv->SetIntField(env, obj, consumedID, 0);

  if (compress) {
    if (!lctx->compressed_buf) {
      size_t bound = libdeflate_zlib_compress_bound(lctx->compressor, inLength);

      lctx->compressed_buf = malloc(bound);
      if (!lctx->compressed_buf) {
        throwOutOfMemoryError(env, "Failed to allocate compression buffer");
        return -1;
      }

      size_t actual = libdeflate_zlib_compress(
          lctx->compressor, in_ptr, inLength, lctx->compressed_buf, bound);

      if (actual == 0) {
        throwException(env, "compress returned 0", 0);
        return -1;
      }

      lctx->compressed_size = actual;
      lctx->compressed_offset = 0;
      toEnv->SetIntField(env, obj, consumedID, inLength);
    }
    size_t remaining = lctx->compressed_size - lctx->compressed_offset;
    size_t to_copy = remaining < outLength ? remaining : outLength;

    memmove(out_ptr, lctx->compressed_buf + lctx->compressed_offset, to_copy);

    lctx->compressed_offset += to_copy;
    if (lctx->compressed_offset >= lctx->compressed_size) {
      toEnv->SetBooleanField(env, obj, finishedID, JNI_TRUE);
    }

    return to_copy;
  } else {
    size_t actual_in = 0;
    size_t actual_out = 0;
    enum libdeflate_result result = libdeflate_zlib_decompress_ex(
        lctx->decompressor, in_ptr, inLength, out_ptr, (size_t)outLength,
        &actual_in, &actual_out);

    if (result != LIBDEFLATE_SUCCESS) {
      throwException(env, "decompress failed", result);
      return -1;
    }
    toEnv->SetIntField(env, obj, consumedID, actual_in);
    toEnv->SetBooleanField(env, obj, finishedID, JNI_TRUE);
    return actual_out;
  }
}
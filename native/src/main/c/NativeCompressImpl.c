#include <stdlib.h>
#include <string.h>
#include <libdeflate.h>
#include "shared.h"
#if !defined(__aarch64__)
#include "cpuid_helper.h"
#endif
#include "net_md_5_bungee_jni_zlib_NativeCompressImpl.h"

typedef unsigned char byte;

static jclass classID;
static jfieldID consumedID;
static jfieldID finishedID;
static jmethodID makeExceptionID;

void JNICALL Java_net_md_15_bungee_jni_zlib_NativeCompressImpl_initFields(JNIEnv* env, jclass clazz) {
    classID = clazz;
    // We trust that these will be there
    consumedID = (*env)->GetFieldID(env, clazz, "consumed", "I");
    finishedID = (*env)->GetFieldID(env, clazz, "finished", "Z");
    makeExceptionID = (*env)->GetMethodID(env, clazz, "makeException", "(Ljava/lang/String;I)Lnet/md_5/bungee/jni/NativeCodeException;");
}

jint throwException(JNIEnv *env, const char* message, int err) {
    jstring jMessage = (*env)->NewStringUTF(env, message);
    jthrowable throwable = (jthrowable) (*env)->CallStaticObjectMethod(env, classID, makeExceptionID, jMessage, err);
    return (*env)->Throw(env, throwable);
}

JNIEXPORT jboolean JNICALL Java_net_md_15_bungee_jni_zlib_NativeCompressImpl_checkSupported(JNIEnv* env, jobject obj) {
#if !defined(__aarch64__)
    unsigned int eax, ebx, ecx, edx;
    if (__get_cpuid(1, &eax, &ebx, &ecx, &edx)) {
        return (jboolean)((edx & bit_SSE2) != 0);
    }
    return JNI_FALSE;
#else
    return JNI_TRUE;
#endif
}

void JNICALL Java_net_md_15_bungee_jni_zlib_NativeCompressImpl_reset(
    JNIEnv* env, jobject obj, jlong ctx, jboolean compress) {
}

void JNICALL Java_net_md_15_bungee_jni_zlib_NativeCompressImpl_end(
    JNIEnv* env, jobject obj, jlong ctx, jboolean compress) {
    if (compress) {
        libdeflate_free_compressor((struct libdeflate_compressor*) ctx);
    } else {
        libdeflate_free_decompressor((struct libdeflate_decompressor*) ctx);
    }
}

jlong JNICALL Java_net_md_15_bungee_jni_zlib_NativeCompressImpl_init(
    JNIEnv* env, jobject obj, jboolean compress, jint level) {
    if (compress) {
        struct libdeflate_compressor* c = libdeflate_alloc_compressor((level <= 0) ? 6 : (level <= 9) ? level * 12 / 9 : 12);
        if (!c) {
            throwOutOfMemoryError(env, "Failed to alloc decompressor");
            return 0;
        }
        return (jlong) c;
    } else {
        struct libdeflate_decompressor* d = libdeflate_alloc_decompressor();
        if (!d) {
            throwOutOfMemoryError(env, "Failed to alloc decompressor");
            return 0;
        }
        return (jlong) d;
    }
}

jint JNICALL Java_net_md_15_bungee_jni_zlib_NativeCompressImpl_process(
    JNIEnv* env, jobject obj, jlong ctx, jlong in, jint inLength,
    jlong out, jint outLength, jboolean compress) {

    byte* inBuf = (byte*) in;
    byte* outBuf = (byte*) out;

    if (compress) {
        size_t actual = libdeflate_deflate_compress(
            (struct libdeflate_compressor*) ctx,
            inBuf, inLength, outBuf, outLength
        );
        if (actual == 0) {
            throwException(env, "compress output buffer too small", -1);
            return -1;
        }
        (*env)->SetBooleanField(env, obj, finishedID, JNI_TRUE);
        (*env)->SetIntField(env, obj, consumedID, inLength);
        return (jint) actual;
    } else {
        size_t actual_in = 0, actual_out = 0;
        enum libdeflate_result res = libdeflate_deflate_decompress_ex(
            (struct libdeflate_decompressor*) ctx,
            inBuf, inLength, outBuf, outLength,
            &actual_in, &actual_out
        );
        if (res == LIBDEFLATE_SUCCESS || res == LIBDEFLATE_SHORT_OUTPUT) {
            (*env)->SetBooleanField(env, obj, finishedID, JNI_TRUE);
            (*env)->SetIntField(env, obj, consumedID, (jint) actual_in);
            return (jint) actual_out;
        }
        throwException(env, "decompress failed", (int) res);
        return -1;
    }
}
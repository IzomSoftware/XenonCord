#include "shared.h"
#include <stdio.h>
#include <stdlib.h>

void throwOutOfMemoryError(JNIEnv *env, const char *msg) {
  const JNIEnv toEnv = *env;
  const jclass exceptionClass =
      toEnv->FindClass(env, "java/lang/OutOfMemoryError");
  if (!exceptionClass) {
    // If the proxy ran out of memory, loading this class may fail
    fprintf(stderr, "OUT OF MEMORY: %s\n", msg);
    fprintf(stderr, "Could not load class java.lang.OutOfMemoryError!\n");
    exit(-1);
    return;
  }
  toEnv->ThrowNew(env, exceptionClass, msg);
}

#include <jni.h>
#include <android/log.h>
#include "miniSat/core/Launch.h"

extern "C"
{
JNIEXPORT jstring JNICALL Java_com_uni_ailab_scp_provider_ScpProvider_minisatJNI(
		JNIEnv* env, jobject thiz, jstring jin, jint debug);
}
;

JNIEXPORT jstring JNICALL Java_com_uni_ailab_scp_provider_ScpProvider_minisatJNI(
		JNIEnv* env, jobject thiz, jstring jin, jint debug)
{
	const char* bella = env->GetStringUTFChars(jin, 0);

	char* bomber = minisat(bella, debug != 0);

	jstring bionda = env->NewStringUTF(bomber);

	delete[] bomber;

	env->ReleaseStringUTFChars(jin, bella);

	return bionda;
}

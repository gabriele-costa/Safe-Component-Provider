#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include "dbHandler.h"

//java_package_package_..._nomeMetodo(JNIEnv *env, jobject thisObj, eventuali parametri del metodo)
JNIEXPORT jint JNICALL Java_com_uni_ailab_scp_receiver_SCPPublicReceiver_insert(
		JNIEnv *env, jclass c, jstring p, jstring n, jstring ty, jstring perm,
		jstring pol, jstring sc)
{
	int retValue = 0;

	const char *pack = (*env)->GetStringUTFChars(env, p, NULL);
	const char *name = (*env)->GetStringUTFChars(env, n, NULL);
	const char *type = (*env)->GetStringUTFChars(env, ty, NULL);
	const char *permissions = (*env)->GetStringUTFChars(env, perm, NULL);
	const char *policy = (*env)->GetStringUTFChars(env, pol, NULL);
	const char *scope = (*env)->GetStringUTFChars(env, sc, NULL);

	__android_log_print(ANDROID_LOG_INFO, "MYPROG",
			"JNI: Received component: '%s', '%s', '%s', '%s', '%s', '%s');",
			pack, name, type, permissions, policy, scope);

	t_component *bella = component_create(0, pack, name, type, permissions,
			policy, scope);

	retValue = insertComponent(bella);

	component_destroy(bella);
	return retValue;
}

JNIEXPORT jint JNICALL Java_com_uni_ailab_scp_receiver_SCPPublicReceiver_remove(
		JNIEnv *env, jclass c, jstring p)
{
	int retValue = 0;

	const char *pack = (*env)->GetStringUTFChars(env, p, NULL);

	__android_log_print(ANDROID_LOG_INFO, "MYPROG",
			"JNI: remove components of package: '%s');", pack);

	retValue = removeComponents(pack);

	return retValue;
}

JNIEXPORT jobject JNICALL Java_com_uni_ailab_scp_provider_ScpProvider_query(
		JNIEnv *env, jclass c, jstring p)
{
	int retValue = 0;
	t_componentsArray retComp, *ptr_retComp;
	jclass arrayListClass, componentCalss;
	jobject arrayListObject, componentObject;
	jmethodID arrayListConstructor, arrayListPut, componentCosnstructor;

	const char *componentName = (*env)->GetStringUTFChars(env, p, NULL);

	__android_log_print(ANDROID_LOG_INFO, "MYPROG", "JNI: query: '%s');",
			componentName);

	ptr_retComp = &retComp;

	initMyArray(ptr_retComp, 1);

	selectComponent(componentName, ptr_retComp);

	__android_log_print(ANDROID_LOG_INFO, "MYPROG",
					"1");

	if (!ptr_retComp->used)
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"JNI: query: non sono state trovate componenti");
		freeMyArray(ptr_retComp);
		return NULL;
	}

	arrayListClass = (*env)->FindClass(env, "java/util/ArrayList");

	if (arrayListClass == NULL)
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"JNI: impossibile trovare la classe: com/uni/ailab/scp/provider/ComponentList");
		return NULL;
	}

	componentCalss = (*env)->FindClass(env,
			"com/uni/ailab/scp/provider/Component");

	__android_log_print(ANDROID_LOG_INFO, "MYPROG",
						"2");

	if (componentCalss == NULL)
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"JNI: impossibile trovare la classe: com/uni/ailab/scp/provider/ComponentList");
		return NULL;
	}

	//prendo un riferimento al costruttore
	arrayListConstructor = (*env)->GetMethodID(env, arrayListClass, "<init>",
			"()V");

	if (arrayListConstructor == NULL)
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"JNI: impossibile trovare la il costruttore della classe: com/uni/ailab/scp/provider/ComponentList");
		return NULL;
	}

	//prendo un riferimento al costruttore
	componentCosnstructor =
			(*env)->GetMethodID(env, componentCalss, "<init>",
					"(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");

	__android_log_print(ANDROID_LOG_INFO, "MYPROG",
						"3");

	if (componentCosnstructor == NULL)
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"JNI: impossibile trovare la il costruttore della classe: com/uni/ailab/scp/provider/ComponentList");
		return NULL;
	}

	//creo l'oggetto componentList;
	arrayListObject = (*env)->NewObject(env, arrayListClass,
			arrayListConstructor);

	if (arrayListObject == NULL)
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"JNI: impossibile creare l'oggetto: com/uni/ailab/scp/provider/ComponentList");
		return NULL;
	}

	__android_log_print(ANDROID_LOG_INFO, "MYPROG",
							"4");

	//nota che non hai bisogno di specificare il tipo di oggetto che andrai ad inserire nella list in quanto i generic servono solo al compilatore per verificare errori
	arrayListPut = (*env)->GetMethodID(env, arrayListClass, "add",
			"(Ljava/lang/Object;)Z");

	if (arrayListPut == NULL)
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"JNI: impossibile trovare il metodo: com/uni/ailab/scp/provider/ComponentList");
		return NULL;
	}

	int i = 0;

	__android_log_print(ANDROID_LOG_INFO, "MYPROG",
							"5");

	for (i = 0; i < ptr_retComp->used; i++)
	{
		component_toString(ptr_retComp->array[i]);

		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
									"5a");

		jstring j_package = (*env)->NewStringUTF(env,
				ptr_retComp->array[i].package);
		jstring j_clazz = (*env)->NewStringUTF(env,
				ptr_retComp->array[i].clazz);
		jstring j_policy = (*env)->NewStringUTF(env,
				ptr_retComp->array[i].policy);
		jstring j_type = (*env)->NewStringUTF(env, ptr_retComp->array[i].type);
		jstring j_permissions = (*env)->NewStringUTF(env,
				ptr_retComp->array[i].permissions);
		jstring j_ptype = (*env)->NewStringUTF(env,
				ptr_retComp->array[i].ptype);

		componentObject = (*env)->NewObject(env, componentCalss,
				componentCosnstructor, ptr_retComp->array[i]._id, j_package,
				j_clazz, j_policy, j_type, j_permissions, j_ptype);

		if (componentObject == NULL)
		{
			__android_log_print(ANDROID_LOG_INFO, "MYPROG",
					"JNI: impossibile creare l'oggetto: com/uni/ailab/scp/provider/ComponentList");
			return NULL;
		}

		(*env)->CallBooleanMethod(env, arrayListObject, arrayListPut,
				componentObject);

		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
											"5b");
		//(*env)->ReleaseStringUTFChars(env, p, componentName);
		(*env)->ReleaseStringUTFChars(env, j_package,
				ptr_retComp->array[i].package);
		(*env)->ReleaseStringUTFChars(env, j_clazz,
				ptr_retComp->array[i].clazz);
		(*env)->ReleaseStringUTFChars(env, j_policy,
				ptr_retComp->array[i].policy);
		(*env)->ReleaseStringUTFChars(env, j_type, ptr_retComp->array[i].type);
		(*env)->ReleaseStringUTFChars(env, j_permissions,
				ptr_retComp->array[i].permissions);
		(*env)->ReleaseStringUTFChars(env, j_ptype,
				ptr_retComp->array[i].ptype);

		//(*env)->DeleteLocalRef(env, p);
		(*env)->DeleteLocalRef(env, j_package);
		(*env)->DeleteLocalRef(env, j_clazz);
		(*env)->DeleteLocalRef(env, j_policy);
		(*env)->DeleteLocalRef(env, j_type);
		(*env)->DeleteLocalRef(env, j_permissions);
		(*env)->DeleteLocalRef(env, j_ptype);
	}

	__android_log_print(ANDROID_LOG_INFO, "MYPROG",
							"6");

	freeMyArray(ptr_retComp);
	return arrayListObject;
}

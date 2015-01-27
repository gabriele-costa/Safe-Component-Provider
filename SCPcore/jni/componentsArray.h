#ifndef _T_component_h
#define _T_component_h
#include <android/log.h>
#include <stdlib.h>
#include <string.h>
typedef struct component
{
	int _id;
	char *package;
	char *clazz;
	char *type;
	char *permissions;
	char *policy;
	char *ptype;
}t_component;

typedef struct componentsArray
{
	t_component *array;
	size_t used;
	size_t size;
}t_componentsArray;

void initMyArray(t_componentsArray *a, size_t initialSize);
void insertMyComponent(t_componentsArray *a, t_component *element);
void freeMyArray(t_componentsArray *a);

t_component * component_create(int _id, const char *package, const char * clazz,
		const char *type, const char *permissions, const char *policy, const char *ptype);
void component_destroy(t_component *component);

#endif

#include "componentsArray.h"

void initMyArray(t_componentsArray *a, size_t initialSize)
{
	__android_log_print(ANDROID_LOG_INFO, "MYPROG", "initMyArray");
	a->array = (t_component *) malloc(initialSize * sizeof(t_component));
	a->used = 0;
	a->size = initialSize;
}

void insertMyComponent(t_componentsArray *a, t_component *element)
{
	__android_log_print(ANDROID_LOG_INFO, "MYPROG",
			"insertMyComponent: usato %i, dimensioni %i", a->used, a->size);
	if (a->used == a->size)
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"insertMyComponent: rialloco");
		a->size *= 2;
		a->array = (t_component *) realloc(a->array,
				a->size * sizeof(t_component));
	}

	a->array[a->used++] = *element;

	//qua dovrei poter eliminare element, in quanto eseguendo la copia, non mi serve piu
	__android_log_print(ANDROID_LOG_INFO, "MYPROG",
			"insertMyComponent: usato %i, dimensioni %i", a->used, a->size);

	__android_log_print(ANDROID_LOG_INFO, "MYPROG",
			"insertMyComponent: indirizzo element %p, %p, %p , %p, %p", element,
			&element, *element, &a->array[a->used], a->array[a->used].clazz);

}

void freeMyArray(t_componentsArray *a)
{
	free(a->array);
	a->array = NULL;
	a->used = a->size = 0;
}

t_component * component_create(int _id, const char *package, const char * clazz,
		const char *type, const char *permissions, const char *policy,
		const char *ptype)
{
	t_component *component = malloc(sizeof(t_component));
	if (!component)
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"component_create: impossibile allocarela memoria");
		free(component);
		return NULL;
	}
	component->_id = _id;
	component->package = strdup(package);
	component->clazz = strdup(clazz);
	component->type = strdup(type);
	component->permissions = strdup(permissions);
	component->policy = strdup(policy);
	component->ptype = strdup(ptype);

	return component;
}

void component_toString(t_component component)
{
	__android_log_print(ANDROID_LOG_INFO, "MYPROG",
			"COMPONENTE: id: %i, package: %s, classe: %s, tipo: %s,"
					" permessi: %s, politica: %s, tipo: %s", component._id,
			component.package, component.clazz, component.type,
			component.permissions, component.policy, component.ptype);
}

void component_destroy(t_component *component)
{
	if (!component)
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"component_destroy: non esiste componente da eliminare");
		return;
	}

	free(component->package);
	free(component->clazz);
	free(component->type);
	free(component->permissions);
	free(component->policy);
	free(component->ptype);

	free(component);
}

#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include <unistd.h>
#include <string.h>
#include <sys/stat.h>
#include "dbHandler.h"

/**
 * Ritorna 0 se tutto è andato ok; Non chiude sql, in quanto lo chiuderai negli altri metodi che useranno openDb
 */
int openDb(sqlite3 **db)
{
	__android_log_print(ANDROID_LOG_INFO, "MYPROG", "openDb: enter");

	char *fullPathName;
	char *sql = 0;
	char *zErrMsg = 0;
	int rc = 0;

	// check the exist of the path
	if (access(DB_PATH, F_OK))
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"openDb: create db path");
		int status = mkdir(DB_PATH, S_IRWXU | S_IRWXG | S_IWOTH | S_IXOTH);
		if (status != 0)
		{
			__android_log_print(ANDROID_LOG_INFO, "MYPROG",
					"openDb: impossible to create db path");
			return -1;
		}
	}

	// prepare the db path
	fullPathName = malloc(strlen(DB_PATH) + strlen(DB_NAME) + 1); /* make space for the new string (should check the return value ...) */
	strcpy(fullPathName, DB_PATH); /* copy name into the new var */
	strcat(fullPathName, DB_NAME); /* add the extension */

	/* Open or create the db */
	rc = sqlite3_open(fullPathName, db);

	if (rc)
	{
		//qua non c'é bisogno di liberare la memoria per il messaggio di errore, è fatto in automatico da sqlite3
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"openDb: Can't open database: %s", sqlite3_errmsg(*db));
	}
	else
	{
		/* Create SQL statement */
		asprintf(&sql, "CREATE TABLE IF NOT EXISTS %s ("
				"_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				"%s TEXT NOT NULL,"
				"%s TEXT NOT NULL UNIQUE,"
				"%s TEXT NOT NULL,"
				"%s TEXT NOT NULL,"
				"%s TEXT NOT NULL,"
				"%s TEXT NOT NULL);",
		TABLE_NAME, COLUMN_PACKAGE, COLUMN_NAME, COLUMN_TYPE,
		COLUMN_PERMISSION, COLUMN_POLICY, COLUMN_SCOPE);

		/* Execute SQL statement */
		rc = sqlite3_exec(*db, sql, NULL, 0, &zErrMsg);

		if (rc)
		{
			__android_log_print(ANDROID_LOG_INFO, "MYPROG",
					"openDb: SQL error: %s", zErrMsg);
			//libero la memoria occupata per memorizzare il messaggio di errore
			sqlite3_free(zErrMsg);
		}
		else
		{
			__android_log_print(ANDROID_LOG_INFO, "MYPROG",
					"openDb: Table %s created or found successfully",
					TABLE_NAME);

//			asprintf(&sql, "CREATE TABLE IF NOT EXISTS %s ("
//					"_id INTEGER PRIMARY KEY AUTOINCREMENT,"
//					"%s TEXT NOT NULL UNIQUE);",
//			TABLE_PERMISSIONS, COLUMN_VALUE);
//
//			/* Execute SQL statement */
//			rc = sqlite3_exec(*db, sql, NULL, 0, &zErrMsg);
//
//			if (rc)
//			{
//				__android_log_print(ANDROID_LOG_INFO, "MYPROG",
//						"openDb: SQL error: %s", zErrMsg);
//				//libero la memoria occupata per memorizzare il messaggio di errore
//				sqlite3_free(zErrMsg);
//			}
//			else
//			{
//				__android_log_print(ANDROID_LOG_INFO, "MYPROG",
//						"openDb: Table %s created or found successfully",
//						TABLE_PERMISSIONS);
//			}
		}
	}
	free(fullPathName);
	free(sql);
	return rc;
}

/**
 * Ritorna il numero di entry inserite nel db o -1 in caso di errore;
 */
int insertComponent(t_component *tc)
{
	__android_log_print(ANDROID_LOG_INFO, "MYPROG", "insertComponent: enter");

	sqlite3 *db = 0;
	int rc = 0;
	char *zErrMsg = 0;
	char *sql = 0;
	char *start = 0;

	/* Open database */
	rc = openDb(&db);

	if (rc)
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"insertComponent: errore nell'apertura del DB");
		sqlite3_close(db);
		return -1;
	}

	asprintf(&start, "INSERT INTO %s ("
			"%s,%s,%s,%s,%s,%s) VALUES (", TABLE_NAME, COLUMN_PACKAGE,
	COLUMN_NAME,
	COLUMN_TYPE,
	COLUMN_PERMISSION, COLUMN_POLICY, COLUMN_SCOPE);

	/* Create SQL statement */
	asprintf(&sql, "%s '%s', '%s', '%s', '%s', '%s', '%s');", start,
			tc->package, tc->clazz, tc->type, tc->permissions, tc->policy,
			tc->ptype);

	__android_log_print(ANDROID_LOG_INFO, "MYPROG", "insertComponent: SQL: %s",
			sql);

	/* Execute SQL statement */
	rc = sqlite3_exec(db, sql, NULL, 0, &zErrMsg);

	if (rc)
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"insertComponent: SQL error: %s", zErrMsg);
		sqlite3_free(zErrMsg);
		rc = -1;
	}
	else
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"insertComponent: Records created successfully");
		rc = sqlite3_changes(db);
	}

	free(start);
	free(sql);
	sqlite3_close(db);
	return rc;
}

int insertPermission(char * permission)
{
	__android_log_print(ANDROID_LOG_INFO, "MYPROG", "insertPermission: enter");

	sqlite3 *db = 0;
	int rc = 0;
	char *zErrMsg = 0;
	char *sql = 0;
	char *start = 0;

	/* Open database */
	rc = openDb(&db);

	if (rc)
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"insertPermission: errore nell'apertura del DB");
		sqlite3_close(db);
		return -1;
	}

	asprintf(&start, "INSERT INTO %s ("
			"%s) VALUES (", TABLE_PERMISSIONS, COLUMN_VALUE);

	/* Create SQL statement */
	asprintf(&sql, "%s '%s');", start, permission);

	__android_log_print(ANDROID_LOG_INFO, "MYPROG", "insertPermission: SQL: %s",
			sql);

	/* Execute SQL statement */
	rc = sqlite3_exec(db, sql, NULL, 0, &zErrMsg);

	// DATO CHE INSERISCO IN UN CAMPO UNIQUE, SE IL PERMESSO È GIA PRESENTE, RITORNA ERRORE

	if (rc)
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"insertPermission: SQL error: %s", zErrMsg);
		sqlite3_free(zErrMsg);
		rc = -1;
	}
	else
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"insertPermission: Records created successfully");
		//ritorno l'id della tupla inserita;
		rc = sqlite3_last_insert_rowid(db);
	}

	free(start);
	free(sql);
	sqlite3_close(db);
	return rc;
}

/**
 * Ritorna il numero di entry eliminate nel db o -1 in caso di errore;
 */
int removeComponents(const char *pack)
{
	int rc = 0;
	char *sql = 0;
	sqlite3 *db = 0;
	char *zErrMsg = 0;

	/* Open database */
	rc = openDb(&db);

	if (rc)
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"removeComponents: errore nell'apertura del DB");
		sqlite3_close(db);
		return -1;
	}

	/* Create merged SQL statement */
	asprintf(&sql, "DELETE FROM %s WHERE %s = '%s';", TABLE_NAME,
	COLUMN_PACKAGE, pack);

	__android_log_print(ANDROID_LOG_INFO, "MYPROG",
			"removeComponents: SQL : %s", sql);

	/* Execute SQL statement */
	rc = sqlite3_exec(db, sql, NULL, 0, &zErrMsg);

	if (rc)
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"removeComponents: SQL error: %s", zErrMsg);
		sqlite3_free(zErrMsg);
		rc = -1;
	}
	else
	{
		rc = sqlite3_changes(db);

		if (rc)
		{
			__android_log_print(ANDROID_LOG_INFO, "MYPROG",
					"removeComponents: Records removed successfully, %i components removed",
					rc);
		}
		//forse sarebbe il caso di ritornare un qualcosa per comunicare a livello superiore se non hai cancellato nulla..sto scemo ti da rc =0 anche se non cancella nulla..
	}

	free(sql);
	sqlite3_close(db);

	return rc;
}

void selectComponent(const char *componentName, t_componentsArray *ptr_retValue)
{
	int rc = 0;
	char *sql = 0;
	sqlite3 *db = 0;
	char *zErrMsg = 0;

	/* Open database */
	rc = openDb(&db);

	if (rc)
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"removeComponents: errore nell'apertura del DB");
		sqlite3_close(db);
		return;
	}

	/* Create SQL statement */

	/* TEST */
	//asprintf(&sql, "SELECT * FROM %s;", TABLE_NAME);

	asprintf(&sql, "SELECT * FROM %s WHERE %s;", TABLE_NAME, componentName);

//	asprintf(&sql,
//			"SELECT * FROM %s WHERE ComponentPackage = 'it.unige.androscp.front';",
//			TABLE_NAME);

	__android_log_print(ANDROID_LOG_INFO, "MYPROG",
			"selectComponent: looking for %s", sql);

	/* Execute SQL statement */
	rc = sqlite3_exec(db, sql, callback, &ptr_retValue, &zErrMsg);
	if (rc)
	{
		__android_log_print(ANDROID_LOG_INFO, "MYPROG",
				"selectComponent: SQL error: %s", zErrMsg);
		sqlite3_free(zErrMsg);
	}
	else
	{
		rc = ptr_retValue->used;
		if (!rc)
		{
			__android_log_print(ANDROID_LOG_INFO, "MYPROG",
					"selectComponent: Non sono state trovate componenti");
			freeMyArray(ptr_retValue);
		}
		else
		{
			__android_log_print(ANDROID_LOG_INFO, "MYPROG",
					"selectComponent: Operation done successfully, found %i tuple",
					rc);
		}
	}
	free(sql);
	sqlite3_close(db);
}

static int callback(void *data, int argc, char **argv, char **azColName)
{
	t_componentsArray *ptr_retValue;

	/* Devo castare il data a puntatore che mi interessa */
	t_componentsArray ** retComp = (t_componentsArray **) data;

	ptr_retValue = *retComp;

	__android_log_print(ANDROID_LOG_INFO, "MYPROG",
			"trovato: %s %s %s %s %s %s %s", argv[0], argv[1], argv[2], argv[3],
			argv[4], argv[5], argv[6]);

	//casto a intero il carattere ottenuto:
	int i = *argv[0] - '0';

	//inserisco i valori della tupla nella mia struttura:
	t_component *componente = component_create(i, argv[1], argv[2], argv[3], argv[4],
			argv[5], argv[6]);

	__android_log_print(ANDROID_LOG_INFO, "MYPROG", "componente: %p, &componente: %p, *componente: %p ", componente,
			&componente, *componente);
	insertMyComponent(ptr_retValue, componente);

	__android_log_print(ANDROID_LOG_INFO, "MYPROG", "punto: %p, %p, %p ", componente,
			&componente, *componente);

	//QUESTO DA PROBLEMI: DOVREI ELIMINARE LA COMPONENTE PROVA
	//APPENA CREATA MA SE LO FACCIO HO PROBLEMI SULLA STRINGA CHE RITORNO A JNI
	//component_destroy(prova);
	return 0;
}

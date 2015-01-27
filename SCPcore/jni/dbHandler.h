#include "sqlite/sqlite3.h"
#include "constants.h"
#include "componentsArray.h"

int openDb(sqlite3 **db);
int insertComponent(t_component *tc);
int removeComponents(const char *pack);
static int callback(void *data, int argc, char **argv, char **azColName);
void selectComponent(const char *componentName, t_componentsArray *);

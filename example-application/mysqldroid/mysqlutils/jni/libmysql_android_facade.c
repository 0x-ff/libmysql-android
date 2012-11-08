
/* Simplest JNI libmysql wrapper 
	Pavel V. Ivanisenko pahanmipt@mail.ru */

#include <mysql/mysql.h>
#include <mysql/errmsg.h>
#include <string.h>
#include <stdlib.h>
#include <jni.h>
#include <android/log.h>


jstring get_attr( JNIEnv* env, jobject thiz, jstring name ) {
	
	if (env == NULL || thiz == NULL || name == NULL) 
		{ return NULL; }

	jclass cls  = (*env)->GetObjectClass(env, thiz);
	jfieldID id = (*env)->GetFieldID(env, cls, name, "Ljava/lang/String;");
	
	if (id == NULL) 
		{ return NULL; }

	jstring val = (*env)->GetObjectField(env,thiz,id);
	return val;
}

void put_message(const char *where, const char *message) 
{
	__android_log_print(ANDROID_LOG_INFO,"libmysql_wrapper",
		"Message: '%s' '%s'", where, message);
}

void put_wrong_usage(const char *err) {

	__android_log_print(ANDROID_LOG_ERROR,"libmysql_wrapper",
		"Wrong usage: '%s'", err);
}

void put_fatal_out_of_mem(const char *where) {

	__android_log_print(ANDROID_LOG_FATAL,"libmysql_wrapper",
		"Out of memory at '%s'", where);
}

void put_error ( JNIEnv* env, jobject thiz, const char* mark, MYSQL* handle) {
	
	const char *error = mysql_error(handle);
	__android_log_print(ANDROID_LOG_ERROR,"libmysql_wrapper",
		"%s '%s'", mark, error);

	jclass cls = (*env)->GetObjectClass(env, thiz);
	jfieldID id = (*env)->GetFieldID(env, cls, "lastError", "Ljava/lang/String;");
	if (id != NULL) {

		jstring str = (*env)->NewStringUTF(env, error);
		if (str != NULL) {
			(*env)->SetObjectField(env, thiz, id, str);
		} else {
			put_fatal_out_of_mem("put_error(), str");
		}
	} else {
		put_fatal_out_of_mem("put_error(), id");
	}
}

void *xmalloc(int size) {
	
	if (size <= 0) {
		return NULL;
	}
	
	void* ptr = NULL;
	ptr = malloc(size);

	if ( ptr == NULL ) {
		__android_log_print(ANDROID_LOG_FATAL, "libmysql_wrappers", 
			"out of memory '%d'", size);
		exit(-1);
	}
	
	return ptr;
}


void 
Java_android_database_mysql_natives_Connector_disconnect(JNIEnv* env, jobject thiz) 
{
	jclass cls = (*env)->GetObjectClass(env, thiz);
	jfieldID handle_id = (*env)->GetFieldID(env, cls, "handle", "I");
	
	if (handle_id == NULL) { 
		put_fatal_out_of_mem("disconnect(),handle_id");
		return; 
	}
	
	MYSQL* handle = (MYSQL*) (*env)->GetIntField(env,thiz,handle_id);
	if (handle != NULL) {
		mysql_close(handle);
		put_message("disconnect()", "SUCCESS");
	} else {
		put_message("disconnect()", "already disconnected");
	}
	(*env)->SetIntField(env, thiz, handle_id, 0);
}

jboolean
Java_android_database_mysql_natives_Connector_connect(JNIEnv* env, jobject thiz)
{
	jstring host, user, passwd, dbname, port;
    const char *c_host, *c_user, *c_passwd, *c_dbname, *c_port;
            
	host = get_attr(env, thiz, "hostAddr");
	if (host == NULL) { 
		put_fatal_out_of_mem("connect(),host");
		return 0; 
	}
	c_host = (*env)->GetStringUTFChars(env, host, NULL);
    if (c_host == NULL) { 
		put_fatal_out_of_mem("connect(),c_host");
		return 0; 
	}

	user = get_attr(env, thiz, "userName");
	if (!user) { 
		put_fatal_out_of_mem("connect(),user");
		return 0; 
	}
	c_user = (*env)->GetStringUTFChars(env, user, NULL);
    if (c_user == NULL) { 
		put_fatal_out_of_mem("connect(),c_user");
		return 0; 
	}
    
	passwd = get_attr(env, thiz, "passWord");
	if (!passwd) { 
		put_fatal_out_of_mem("connect(),passwd");
		return 0; 
	}
	c_passwd = (*env)->GetStringUTFChars(env, passwd, NULL);
    if (c_passwd == NULL) { 
		put_fatal_out_of_mem("connect(),c_passwd");
		return 0; 
	}

	dbname = get_attr(env, thiz, "dbName");
	if (!dbname) { 
		put_fatal_out_of_mem("connect(),dbname");
		return 0; 
	}
	c_dbname = (*env)->GetStringUTFChars(env, dbname, NULL);
    if (c_dbname == NULL) { 
		put_fatal_out_of_mem("connect(),c_dbname");
		return 0; 
    }
    
	port = get_attr(env, thiz, "portNum");
	if (!port) { 
		put_fatal_out_of_mem("connect(),port");
		return 0; 
	}
	c_port = (*env)->GetStringUTFChars(env, port, NULL);
    if (c_port == NULL) { 
		put_fatal_out_of_mem("connect(),c_port");
		return 0; 
	}
    
	jclass cls  = (*env)->GetObjectClass(env, thiz);
	jfieldID handle_id = (*env)->GetFieldID(env, cls, "handle", "I");
	
	if (handle_id == NULL) { 
		put_fatal_out_of_mem("connect(),handle_id");
		return 0; 
	}
    
    // На случай java-психов, вызывающих connect() в цикле, без вызова disconnect()
    (void) Java_android_database_mysql_natives_Connector_disconnect(env, thiz);
    
    __android_log_print(ANDROID_LOG_INFO,"libmysql_wrapper",
            "Connecting to %s@%s:%s:%s", c_user, c_host, c_port, c_dbname);

	MYSQL *handle;
	handle = mysql_init(NULL);
	if (handle == NULL) {
		__android_log_print(ANDROID_LOG_ERROR,"libmysql_wrapper",
            "mysql_init() failed, possible out of memory");
        return 0;
	}

    my_bool reconnect = 1;
    mysql_options(handle, MYSQL_OPT_RECONNECT, &reconnect);

	if ( mysql_real_connect(handle, c_host, c_user, c_passwd, 
		c_dbname, atoi(c_port), NULL, 0) == NULL ) 
	{
        __android_log_print(ANDROID_LOG_ERROR,"libmysql_wrapper",
            "Connect failed to %s@%s:%s:%s", c_user, c_host, c_port, c_dbname);
        put_error(env, thiz, "connect()", handle);
		jfieldID id = (*env)->GetFieldID(env, cls, "lastError", "Ljava/lang/String;");
		if (id != NULL) {
			jstring str = (*env)->NewStringUTF(env, mysql_error(handle));
			if (str != NULL) {
				(*env)->SetObjectField(env, thiz, id, str);
			} else {
				put_fatal_out_of_mem("connect(),str");
			}
		} else {
			put_fatal_out_of_mem("connect(),id");
		}
		mysql_close(handle);
		handle = NULL;
	}
    else 
    {
        __android_log_print(ANDROID_LOG_INFO,"libmysql_wrapper",
            "Successfull connect to %s@%s:%s:%s %d", c_user, 
            c_host, c_port, c_dbname, (int) handle);
        put_message("character encoding is ", mysql_character_set_name(handle));
        put_message("stat ", mysql_stat(handle));
        put_message("info ", mysql_get_server_info(handle));
    }

    (*env)->ReleaseStringUTFChars(env, host, c_host);
    (*env)->ReleaseStringUTFChars(env, user, c_user);
    (*env)->ReleaseStringUTFChars(env, passwd, c_passwd);
    (*env)->ReleaseStringUTFChars(env, dbname, c_dbname);
    (*env)->ReleaseStringUTFChars(env, port, c_port);
	(*env)->SetIntField(env, thiz, handle_id, ( handle == NULL ? 0 : (int) handle));
    return ( handle == NULL ? 0 : 1);
}
 
jboolean
Java_android_database_mysql_natives_Connector_query(JNIEnv* env, jobject thiz, jstring sql, jboolean bin) 
{
	jclass cls = (*env)->GetObjectClass(env, thiz);
	jfieldID handle_id = (*env)->GetFieldID(env, cls, "handle", "I");
	
	if (handle_id == NULL) { 
		put_fatal_out_of_mem("query(),handle_id");
		return 0; 
	}
	MYSQL* handle = (MYSQL*) (*env)->GetIntField(env,thiz,handle_id);
	if (handle == 0) { 
		put_wrong_usage("query() on non-initialized MYSQL*");
		return 0; 
	}
	
    if (mysql_ping(handle) != 0) { // MYSQL_OPT_RECONNECT is set
        put_error(env, thiz, "mysql_ping()", handle);
        mysql_close(handle);
		put_message("disconnect()", "SUCCESS");
        return 0;
    } else {
        (*env)->SetIntField(env, thiz, handle_id, (int) handle);
    }
	
	jfieldID res_id = (*env)->GetFieldID(env, cls, "res", "I");
	if (res_id == NULL) { 
		put_fatal_out_of_mem("query(),res_id");
		return 0; 
	}
		
	(*env)->SetIntField(env, thiz, res_id, 0);
	
	const char * sql_str = (*env)->GetStringUTFChars(env, sql, NULL);
    if (sql_str == NULL) { 
		put_fatal_out_of_mem("query(),sql_str");
		return 0; 
	}
	int res;
	if (bin > 0) {
		put_message("mysql_real_query()", sql_str);
		res = mysql_real_query(handle, sql_str, strlen(sql_str));
	} else {
		put_message("mysql_query()", sql_str);
		res = mysql_query(handle, sql_str);
	}
	(*env)->ReleaseStringUTFChars(env, sql, sql_str);
	if (res != 0) {
		
		put_error(env, thiz, "mysql_query()", handle);
		return 0;
	}
	return 1;
}

jint 
Java_android_database_mysql_natives_Connector_hasMoreRows (JNIEnv* env, jobject thiz) 
{
	jclass cls = (*env)->GetObjectClass(env, thiz);
	jfieldID handle_id = (*env)->GetFieldID(env, cls, "handle", "I");
	
	if (handle_id == NULL) { 
		put_fatal_out_of_mem("hasMoreRows(),handle_id");
		return 0; 
	}
	MYSQL* handle = (MYSQL*) (*env)->GetIntField(env,thiz,handle_id);
	if (handle == 0) { 
		put_wrong_usage("hasMoreRows() on non-initialized MYSQL*");
		return 0; 
	}
	
	jfieldID res_id = (*env)->GetFieldID(env, cls, "res", "I");
	if (res_id == NULL) {
		put_fatal_out_of_mem("hasMoreRows(),res_id");
		return 0;
	}
	jfieldID row_id = (*env)->GetFieldID(env, cls, "row", "I");
	if (row_id == NULL) {
		put_fatal_out_of_mem("hasMoreRows(),row_id");
		return 0;
	}
	MYSQL_RES *res = (MYSQL_RES*) (*env)->GetIntField(env, thiz, res_id);

	if ((int)res == 0) {

		// Never use mysql_store_result on Android ! Only mysql_use_result
		MYSQL_RES* result = mysql_use_result(handle);
		if (result == NULL) {

			put_error(env, thiz, "mysql_use_result()", handle);
			return -1;
		} else {
			put_message("hasMoreRows()", "mysql_use_result success");
		}
		(*env)->SetIntField(env, thiz, res_id, (int) result);
		res = result;
	}
	
	MYSQL_ROW row = mysql_fetch_row(res);
	if (row == NULL) {

		mysql_free_result(res);
		put_message("hasMoreRows()", "mysql_free_result success");
		(*env)->SetIntField(env, thiz, res_id, 0);
		(*env)->SetIntField(env, thiz, row_id, 0);
		if (mysql_errno(handle) != 0) {
			put_error(env, thiz, "mysql_fetch_row()", handle);
			return -1;
		}
		return 0;
	}
	(*env)->SetIntField(env, thiz, row_id, (int)row);
	return 1;
}

jstring 
Java_android_database_mysql_natives_Connector_fetchOne(JNIEnv* env, jobject thiz) 
{
	jclass cls = (*env)->GetObjectClass(env, thiz);
	jfieldID handle_id = (*env)->GetFieldID(env, cls, "handle", "I");
	
	if (handle_id == NULL) { 
		put_fatal_out_of_mem("fetchOne(), handle_id");
		return (*env)->NewStringUTF(env, "NULL"); 
	}
	MYSQL* handle = (MYSQL*) (*env)->GetIntField(env,thiz,handle_id);
	if (handle == 0) { 
		put_wrong_usage("fetchOne() on non-initialized MYSQL*");
		return (*env)->NewStringUTF(env, "NULL"); 
	}
	
	jfieldID row_id = (*env)->GetFieldID(env, cls, "row", "I");
	if (row_id == NULL) {
		put_fatal_out_of_mem("fetchOne(), row_id");
		return (*env)->NewStringUTF(env, "NULL");
	}
	
	MYSQL_ROW row = (MYSQL_ROW) (*env)->GetIntField(env, thiz, row_id);
	if (row == NULL) {
		put_wrong_usage("fetchOne() on non-initialized MYSQL_ROW");
		return (*env)->NewStringUTF(env, "NULL");
	}
	
	if (row[0] == NULL) {
		// OK really null value
		return (*env)->NewStringUTF(env, "NULL");
	}
    
    jstring ret = (*env)->NewStringUTF(env, row[0]);
    if (ret == NULL) {
        put_message("fetchOne() wrong NewStringUTF ", row[0]);
        return (*env)->NewStringUTF(env, "NULL");
    }
	return ret;
}

jint 
Java_android_database_mysql_natives_Connector_lastInsertId(JNIEnv* env, jobject thiz) 
{
	jclass cls = (*env)->GetObjectClass(env, thiz);
	jfieldID handle_id = (*env)->GetFieldID(env, cls, "handle", "I");
	
	if (handle_id == NULL) { 
		put_fatal_out_of_mem("lastInsertId(), handle_id");
		return -1; 
	}
	
	MYSQL* handle = (MYSQL*) (*env)->GetIntField(env, thiz, handle_id);
	
	if (handle == 0) { 
		put_wrong_usage("mysql_insert_id() on non-initialized MYSQL*");
		return 0;
	}

	return mysql_insert_id(handle);
}

jstring 
Java_android_database_mysql_natives_Connector_escape(JNIEnv* env, jobject thiz, jstring str) 
{
	jclass cls = (*env)->GetObjectClass(env, thiz);
	jfieldID handle_id = (*env)->GetFieldID(env, cls, "handle", "I");
	
	if (handle_id == NULL) { 
		put_fatal_out_of_mem("escape(), handle_id");
		return (*env)->NewStringUTF(env, "");
	}
	
	MYSQL* handle = (MYSQL*) (*env)->GetIntField(env, thiz, handle_id);
	
	if (handle == 0) { 
		put_wrong_usage("mysql_real_escape_string() on non-initialized MYSQL*");
		return (*env)->NewStringUTF(env, "");
	}
	
	const char * c_str = (*env)->GetStringUTFChars(env, str, NULL);
    if (c_str == NULL) { 
		put_fatal_out_of_mem("escape(),c_str");
		return (*env)->NewStringUTF(env, "");
	}
	
	int l = strlen(c_str);
	if (l <= 0) {
		return (*env)->NewStringUTF(env, "");
	}
	char *escaped = (char*) xmalloc(2 * l + 1);
	if (escaped == NULL) {
		return (*env)->NewStringUTF(env, ""); 
	}
	*escaped = '\0';
	mysql_real_escape_string(handle,escaped,c_str,l);
	jstring res = (*env)->NewStringUTF(env, escaped);
	free(escaped);
	return res;
}

jobjectArray 
Java_android_database_mysql_natives_Connector_fields(JNIEnv* env, jobject thiz) 
{
	jobjectArray result;
	jclass cls = (*env)->GetObjectClass(env, thiz);
	jfieldID handle_id = (*env)->GetFieldID(env, cls, "handle", "I");
	
	if (handle_id == NULL) { 
		put_fatal_out_of_mem("fields(), handle_id");
		return NULL;
	}
	
	MYSQL* handle = (MYSQL*) (*env)->GetIntField(env, thiz, handle_id);
	
	if (handle == 0) { 
		put_wrong_usage("fields() on non-initialized MYSQL*");
		return NULL;
	}
	
	jfieldID res_id = (*env)->GetFieldID(env, cls, "res", "I");
	if (res_id == NULL) {
		put_fatal_out_of_mem("fields(),res_id");
		return NULL;
	}
	
	MYSQL_RES *res = (MYSQL_RES*) (*env)->GetIntField(env, thiz, res_id);
	
	if (res == NULL) {
		put_wrong_usage("fields() on non-initialized MYSQL_RES*");
		return NULL;
	}
	
	jclass arrclass = (*env)->FindClass(env, "java/lang/String");
	if (arrclass == NULL) {
		put_fatal_out_of_mem("fields(), arrclass");
		return NULL;
	}
	int size = mysql_num_fields(res);
	
	if (size <= 0) {
		put_error(env,thiz, "fields()", handle);
		return NULL;
	}
	
	result = (*env)->NewObjectArray(env, size, arrclass, NULL);
    if (result == NULL) {
        put_fatal_out_of_mem("fields(), result");
        return NULL;
    }
	MYSQL_FIELD *field;
	int i = 0;
	while ((field = mysql_fetch_field(res)) != NULL) {
		
		(*env)->SetObjectArrayElement( env, result, i, 
			(*env)->NewStringUTF(env, field->name) );
		i++;
	}
	return result;
}

jobjectArray 
Java_android_database_mysql_natives_Connector_fetchRowArray(JNIEnv* env, jobject thiz)
{
	jobjectArray result;
	
	jclass cls = (*env)->GetObjectClass(env, thiz);
	jfieldID handle_id = (*env)->GetFieldID(env, cls, "handle", "I");
	
	if (handle_id == NULL) { 
		put_fatal_out_of_mem("fetchRowArray(), handle_id");
		return NULL; 
	}
	MYSQL* handle = (MYSQL*) (*env)->GetIntField(env,thiz,handle_id);
	if (handle == 0) { 
		put_wrong_usage("fetchRowArray() on non-initialized MYSQL*");
		return NULL;
	}
	
	jfieldID res_id = (*env)->GetFieldID(env, cls, "res", "I");
	if (res_id == NULL) {
		put_fatal_out_of_mem("fetchRowArray(),res_id");
		return NULL;
	}
	
	MYSQL_RES *res = (MYSQL_RES*) (*env)->GetIntField(env, thiz, res_id);
	
	if (res == NULL) {
		put_wrong_usage("fetchRowArray() on non-initialized MYSQL_RES*");
		return NULL;
	}
	
	jfieldID row_id = (*env)->GetFieldID(env, cls, "row", "I");
	if (row_id == NULL) {
		put_fatal_out_of_mem("fetchRowArray(), row_id");
		return NULL;
	}

	MYSQL_ROW row = (MYSQL_ROW) (*env)->GetIntField(env, thiz, row_id);
	if (row == NULL) {
		put_wrong_usage("fetchRowArray() on non-initialized MYSQL_ROW");
		return NULL;
	}
	
	int size = mysql_num_fields(res);
	if (size <= 0) {
		put_error(env,thiz, "fetchRowArray()", handle);
		return NULL;
	}
	
	jclass arrclass = (*env)->FindClass(env, "java/lang/String");
	if (arrclass == NULL) {
		put_fatal_out_of_mem("fetchRowArray(), arrclass");
		return NULL;
	}
    result = (*env)->NewObjectArray(env, size, arrclass, NULL);
    if (result == NULL) {
        put_fatal_out_of_mem("fetchRowArray(), result");
        return NULL;
    }
	int i = 0;
	for (; i < size; i++) {
		
		const char* val = (row[i] == NULL ? "NULL" : row[i]);
		
		(*env)->SetObjectArrayElement( env, result, i, 
			(*env)->NewStringUTF(env, val) );
	}
	return result;
}

jboolean 
Java_android_database_mysql_natives_Connector_forceFreeResult(JNIEnv* env, jobject thiz) 
{
	jclass cls = (*env)->GetObjectClass(env, thiz);
	jfieldID handle_id = (*env)->GetFieldID(env, cls, "handle", "I");
	
	if (handle_id == NULL) { 
		put_fatal_out_of_mem("forceFreeResult(),handle_id");
		return 0; 
	}
	MYSQL* handle = (MYSQL*) (*env)->GetIntField(env,thiz,handle_id);
	if (handle == 0) { 
		put_wrong_usage("forceFreeResult() on non-initialized MYSQL*");
		return 0; 
	}
	
	jfieldID res_id = (*env)->GetFieldID(env, cls, "res", "I");
	if (res_id == NULL) {
		put_fatal_out_of_mem("forceFreeResult(),res_id");
		return 0;
	}
	jfieldID row_id = (*env)->GetFieldID(env, cls, "row", "I");
	if (row_id == NULL) {
		put_fatal_out_of_mem("forceFreeResult(),row_id");
		return 0;
	}
	MYSQL_RES *res = (MYSQL_RES*) (*env)->GetIntField(env, thiz, res_id);

	if ((int)res != 0) {
		
		while (mysql_fetch_row(res) != NULL) { 
			/*
			 http://dev.mysql.com/doc/refman/5.6/en/mysql-use-result.html
			 
			 When using mysql_use_result(), you must execute mysql_fetch_row() 
			 until a NULL value is returned, otherwise, the unfetched rows are 
			 returned as part of the result set for your next query. 
			 The C API gives the error Commands out of sync; you can't run 
			 this command now if you forget to do this!
			*/
			put_message("forceFreeResult()", "continue with mysql_fetch_row");
		}
		put_message("forceFreeResult()", "all rows done => free result");
		mysql_free_result(res);
		put_message("forceFreeResult()", "free result complete");
		(*env)->SetIntField(env, thiz, res_id, 0);
		(*env)->SetIntField(env, thiz, row_id, 0);
	}
	return 1;
}

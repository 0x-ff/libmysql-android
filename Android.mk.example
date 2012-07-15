LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_C_INCLUDES += .
LOCAL_LDLIBS += -L/media/pavel/projects/libmysql-android/android14-arm -lmysqlclient -llog
LOCAL_MODULE    := libmysql_android_facade
LOCAL_SRC_FILES := libmysql_android_facade.c

include $(BUILD_SHARED_LIBRARY)
 

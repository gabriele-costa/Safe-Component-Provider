#LOCAL_PATH is used to locate source files in the development tree.
#the macro my-dir provided by the build system, indicates the path of the current directory
LOCAL_PATH:=$(call my-dir)


#####################################################################
#            build sqlite library				                    #
#####################################################################
include $(CLEAR_VARS)
LOCAL_C_INCLUDES := $(LOCAL_PATH)
LOCAL_MODULE:=sqlite
LOCAL_SRC_FILES:= sqlite/sqlite3.c
LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)


#####################################################################
#            build minisat library				                    #
#####################################################################
include $(CLEAR_VARS)
LOCAL_CPP_EXTENSION := .cc
LOCAL_CFLAGS    := -fexceptions
LOCAL_MODULE    := minisat
LOCAL_SRC_FILES := miniSat/core/Main.cc \
		miniSat/core/Launch.cc \
		miniSat/core/Solver.cc \
		miniSat/utils/Options.cc \
		miniSat/utils/System.cc
LOCAL_LDLIBS    := -lm -llog
include $(BUILD_SHARED_LIBRARY)

#####################################################################
#            build our code                    #
#####################################################################
include $(CLEAR_VARS)
LOCAL_C_INCLUDES := $(LOCAL_PATH)
LOCAL_MODULE:=scplib
LOCAL_SRC_FILES:= javaInterface.c \
				componentsArray.c \
 				dbHandler.c  				
LOCAL_LDLIBS := -llog
LOCAL_SHARED_LIBRARIES:= $(LOCAL_PATH)/sqlite sqlite
LOCAL_SHARED_LIBRARIES+= $(LOCAL_PATH)/minisat minisat
include $(BUILD_SHARED_LIBRARY)

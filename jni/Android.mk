LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS    := -lm -llog -ljnigraphics
LOCAL_MODULE    := dominantcolors
LOCAL_SRC_FILES := dominantcolors.c kdtree.c

include $(BUILD_SHARED_LIBRARY)
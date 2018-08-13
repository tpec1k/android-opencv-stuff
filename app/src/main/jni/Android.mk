LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_ROOT:=/Users/sergeygorun/dev/OpenCV-android-sdk
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCV_ROOT}/sdk/native/jni/OpenCV.mk

NDK_MODULE_PATH=/home/wenfahu/NVPACK/android-sdk-linux/ndk-bundle
LOCAL_ARM_NEON := true
LOCAL_SRC_FILES := hello-native.cpp
LOCAL_CPPFLAGS := -std=gnu++0x
LOCAL_CFLAGS += -O2
LOCAL_LDLIBS += -llog -ldl
LOCAL_MODULE := MyLibs

include $(BUILD_SHARED_LIBRARY)

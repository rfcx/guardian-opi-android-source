LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_PACKAGE_NAME := PTT
$(shell cp -rf $(LOCAL_PATH)/libs/armeabi/libserial_port.so $(TARGET_OUT)/lib/libserial_port.so)
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v4
#LOCAL_DEX_PREOPT := false
LOCAL_PRIVILEGED_MODULE := true
LOCAL_CERTIFICATE := platform
include $(BUILD_PACKAGE) 


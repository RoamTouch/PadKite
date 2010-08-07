# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PRELINK_MODULE := false
TARGET_PRELINK_MODULE := false

TOP_LOCAL_PATH:= $(call my-dir)

# Build application

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)


LOCAL_SRC_FILES := $(call all-subdir-java-files)

#LOCAL_PACKAGE_NAME := RoamTouchWebKit
LOCAL_NO_STANDARD_LIBRARIES := true
LOCAL_JAVA_LIBRARIES := core ext framework

LOCAL_MODULE := RoamTouchWebKit

LOCAL_MODULE_CLASS := JAVA_LIBRARIES

LOCAL_JNI_SHARED_LIBRARIES := librtwebcore


#include $(BUILD_PACKAGE)

# Build a static jar file.
include $(BUILD_STATIC_JAVA_LIBRARY)


# additionally, build tests in sub-folders in a separate .apk
include $(LOCAL_PATH)/jni/webkit/Android.mk

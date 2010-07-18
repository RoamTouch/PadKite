#!/bin/bash

REL="Alpha"

# Find actual md5sum

[ ! -d .git ] && { echo "Please run only from toplevel directory." 1>&2; exit 1; }

LIBSTR=$(md5 AndroidTeam/Swiftee/libs/RoamTouchWebKit.jar AndroidTeam/Swiftee/libs/armeabi/librtwebcore.so | cut -d'=' -f2 | awk '{ printf("%s\\/", substr($0, 2, 6)); }' | awk '{ print substr($0,0,14); }')
VER=$(cat AndroidTeam/Swiftee/AndroidManifest.xml | grep android:versionName | perl -pi -e 's/.*="(.*)"/\1/')

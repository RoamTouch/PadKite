#!/bin/bash

cd /android/roamtouch_android || exit 1

source $(dirname $0)/common.sh || exit 1

OLDLIBSTR=$LIBSTR

echo -n "Updating library version from \"$REL-v$VER #$LIBSTR\" ... "

rsync -avP /android/build/out/target/product/generic/obj/lib/librtwebcore.so AndroidTeam/Swiftee/libs/armeabi/librtwebcore.so
rsync -avP /android/build/out/target/common/obj/JAVA_LIBRARIES/RoamTouchWebKit_intermediates/javalib.jar AndroidTeam/Swiftee/libs/RoamTouchWebKit.jar
source $(dirname $0)/common.sh || exit 1

git commit --author "Fabian Franz <fabian.franz@roamtouch.com>" -m"Update library from $OLDLIBSTR to $LIBSTR." AndroidTeam/Swiftee/libs/

echo "Updated library to \"$REL-v$VER #$LIBSTR\" ... done"

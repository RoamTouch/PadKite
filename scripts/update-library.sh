#!/bin/bash

source $(dirname $0)/common.sh || exit 1

OLDLIBSTR=$LIBSTR

echo -n "Updating library version from \"$REL-v$VER #$LIBSTR\" ... "

rsync -avP RoamTouchWebKit/libs/* AndroidTeam/Swiftee/libs/
source $(dirname $0)/common.sh || exit 1

git commit --author "Fabian Franz <fabian.franz@roamtouch.com>" -m"Update library from $OLDLIBSTR to $LIBSTR." AndroidTeam/Swiftee/libs/

echo "Updated library to \"$REL-v$VER #$LIBSTR\" ... done"

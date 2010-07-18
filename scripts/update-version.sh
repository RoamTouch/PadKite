#!/bin/bash

source $(dirname $0)/common.sh || exit 1

echo -n "Updating Version to \"$REL-v$VER #$LIBSTR\" ... "

perl -pi -e "s/public static String version = \"Version (.*) build #(.*)\";/public static String version = \"Version $REL-v$VER build #$LIBSTR\";/" AndroidTeam/Swiftee/src/com/roamtouch/swiftee/BrowserActivity.java

echo "done"

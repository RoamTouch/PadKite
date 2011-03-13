#!/bin/bash

cd /android/roamtouch_android

source $(dirname $0)/common.sh || exit 1

cd AndroidTeam/Swiftee/
echo -n "Building Version \"$REL-v$VER #$LIBSTR\" ... "

ant clean 
ant release
RC=$?

echo "Building Version \"$REL-v$VER #$LIBSTR\" ... done: $RC"

# Sent mail notification here / upload to HP / etc.
exit $RC

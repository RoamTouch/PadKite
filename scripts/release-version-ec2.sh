#!/bin/bash

cd /android/roamtouch_android

source $(dirname $0)/common.sh || exit 1

echo -n "Releasing Version \"$REL-v$VER #$LIBSTR\" ... "

NIGHTLY=""
if [ -n "$ROAMTOUCH_NIGHTLY" ]
then
	NIGHTLY="nightly/"
fi

cp AndroidTeam/Swiftee/bin/PadKite-release.apk /android/releases/$NIGHTLY"PadKite-$REL-v$VER.apk"

echo "done"

# Sent mail notification here

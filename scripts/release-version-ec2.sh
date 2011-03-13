#!/bin/bash

cd /android/roamtouch_android

source $(dirname $0)/common.sh || exit 1

echo -n "Releasing Version \"$REL-v$VER #$LIBSTR\" ... "

cp AndroidTeam/Swiftee/bin/PadKite-release.apk /android/releases/"PadKite-$REL-v$VER.apk"

echo "done"

# Sent mail notification here

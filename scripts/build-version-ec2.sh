#!/bin/bash

if [ -z "$1" ]
then
	echo "Syntax: $0 <branch>"
fi

cd /android/roamtouch_android

git checkout "$1" || exit 1

source $(dirname $0)/common.sh || exit 1

cd AndroidTeam/Swiftee/
echo -n "Building Version \"$REL-v$VER #$LIBSTR\" ... "

ant release

echo "Building Version \"$REL-v$VER #$LIBSTR\" ... done"

# Sent mail notification here / upload to HP / etc.

#!/bin/bash

source $(dirname $0)/common.sh || exit 1

echo -n "Releasing Version \"$REL-v$VER #$LIBSTR\" ... "

cp AndroidTeam/Swiftee/bin/Swiftee.apk releases/"PadKite-$REL-v$VER.apk"

if [ "$1" == "" ]
then
	git add releases/
fi

echo "done"

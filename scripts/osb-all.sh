#!/bin/bash

cd /android/roamtouch_android/ || exit 1

for branch in $(git branch -l)
do
	echo "============== Building $branch ..."
	$(dirname $0)/osb.sh "$branch" "$1"
done

#!/bin/bash

export GIT_BRANCH="$1"
export ROAMTOUCH_VERSION="$2"

# For nightly builds
if [ -z "$ROAMTOUCH_VERSION" ]
then
	ROAMTOUCH_VERSION=$(date +%F-%s)
	export ROAMTOUCH_NIGHTLY=1
fi

if [ -z "$GIT_BRANCH" ]
then
	GIT_BRANCH="master"
fi

git checkout "$GIT_BRANCH" -- || exit 1

if [ "$GIT_BRANCH" = "master" ]
then
	GIT_BRANCH="eclair"
fi

$(dirname $0)/build-version-ec2.sh && $(dirname $0)/release-version-ec2.sh

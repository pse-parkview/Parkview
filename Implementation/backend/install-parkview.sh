#!/bin/sh

set -xe

# remove old version
rm -rf build

# build backend
./gradlew build -x browserTest

# copy over new version
rm -rf ../frontend/node_modules/parkview
mv build/js/packages/parkview ../frontend/node_modules

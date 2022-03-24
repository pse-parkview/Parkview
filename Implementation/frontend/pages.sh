#!/bin/sh

set -xe

# Update the gtihub.io page with the current state of the repo

# Requires the angular-cli-ghpages module:
# npm i angular-cli-ghpages --save-dev

# Build a production version
ng build --prod --base-href "https://pse-parkview.github.io/Parkview/"

# The rest is done by angular-cli-ghpages
npx angular-cli-ghpages --dir=dist/frontend

#!/usr/bin/env bash
set -eu

MOD=$(basename "$(pwd)")
VERSION=$(jq -r .version mod_info.json)
RELEASE=$MOD-$VERSION

# copy over mod to release folder
rm -rf $MOD
mkdir $MOD
cp -R data/ graphics/ $MOD/
cp mod_info.json ${MOD}.jar settings.json ${MOD}.version $MOD/

rm -f $RELEASE.zip
zip -r $RELEASE.zip $MOD
rm -rf $MOD

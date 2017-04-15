#!/usr/bin/env bash

echo "../run.sh hierarchy.scxml hierarchy.js"
../run.sh hierarchy.scxml hierarchy.js

echo "node ./test.js"
node ./test.js
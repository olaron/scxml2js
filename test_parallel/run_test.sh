#!/usr/bin/env bash

echo "../run.sh parallel.scxml parallel.js"
../run.sh parallel.scxml parallel.js

echo "node ./test.js"
node ./test.js
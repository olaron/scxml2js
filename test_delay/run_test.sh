#!/usr/bin/env bash

echo "../run.sh delay.scxml delay.js"
../run.sh delay.scxml delay.js

echo "node ./test.js"
node ./test.js
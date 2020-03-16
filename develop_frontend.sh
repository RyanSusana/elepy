#!/usr/bin/env bash

# This script allows you to work on the frontend, located in 'admin/src/main/resources/frontend'
# It assumes you have Maven and Node installed
# To change the Elepy model go to 'basic/src/test/java/'


shutdown() {

  # Kill child processes in a new new process group
  pkill -P $$
  exit 0
}

trap "shutdown" SIGINT SIGTERM


pushd basic
mvn clean compile package exec:java -Dexec.mainClass="com.elepy.tests.Main" -Dexec.classpathScope=test &
popd

pushd admin/src/main/resources/frontend
npm install
npm run serve &
popd

wait
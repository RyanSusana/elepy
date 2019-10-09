#!/usr/bin/env bash

npm config set //registry.npmjs.org/:_authToken $NPM_OAUTH_TOKEN
mvn deploy --settings .travis.maven.settings.xml -Psign-artifacts -Ptravis -DskipTests=true -B -U -e
npm publish ./admin/src/main/resources/frontend
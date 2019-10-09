#!/usr/bin/env bash

mvn versions:set-property -Dproperty=revision -DnewVersion=$1
npm config set //registry.npmjs.org/:_authToken $NPM_OAUTH_TOKEN
mvn deploy --settings .travis.maven.settings.xml -Psign-artifacts -Ptravis -DskipTests=true -B -U -e
npm publish ./admin/src/main/resources/frontend
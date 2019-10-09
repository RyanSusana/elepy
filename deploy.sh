#!/usr/bin/env bash

# Exit on any command failure
set -e

# Login to NPM
npm config set //registry.npmjs.org/:_authToken $NPM_OAUTH_TOKEN

# Deploy to Maven Central
mvn deploy --settings .travis.maven.settings.xml -Psign-artifacts -Ptravis -DskipTests=true -B -U -e

# Publish to the NPM registry
npm publish ./admin/src/main/resources/frontend
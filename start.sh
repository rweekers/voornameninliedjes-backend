#!/bin/sh

echo 'starting containers...'

./gradlew startMyMongoAppContainer
./gradlew startMyPostgresAppContainer

echo 'containers started...'


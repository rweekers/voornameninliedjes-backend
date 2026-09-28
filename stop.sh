#!/bin/sh

echo 'stopping containers...'

docker stop some-postgres
docker rm some-postgres
docker stop some-keycloak
docker rm some-keycloak

echo 'containers stopped...'


#!/bin/sh

echo 'stopping containers...'

docker stop some-mongo
docker rm some-mongo
docker stop some-postgres
docker rm some-postgres

echo 'containers stopped...'


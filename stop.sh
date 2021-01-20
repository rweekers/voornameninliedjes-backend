#!/bin/sh

echo 'stopping containers...'

docker stop some-postgres
docker rm some-postgres

echo 'containers stopped...'


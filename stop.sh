#!/bin/sh

echo 'stopping containers...'

docker stop some-mongo
docker rm some-mongo

echo 'containers stopped...'


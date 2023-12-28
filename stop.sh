#!/bin/sh

echo 'stopping containers...'

docker stop some-postgres
docker rm some-postgres
docker stop elasticsearch
docker rm elasticsearch
docker stop kibana
docker rm kibana
docker stop logstash
docker rm logstash

echo 'containers stopped...'


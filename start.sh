#!/bin/sh

echo 'starting postgres container...'

docker run -d --name some-postgres -p 5432:5432 -e POSTGRES_USER=vil_app -e POSTGRES_PASSWORD=secret -e POSTGRES_DB=voornameninliedjes postgres:14.6-bullseye

echo 'postgres container started...'


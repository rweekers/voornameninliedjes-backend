#!/bin/sh

echo 'starting postgres container...'

docker run -d \
  --name some-postgres \
  -p 5432:5432 \
  -e POSTGRES_USER=vil_app \
  -e POSTGRES_PASSWORD=secret \
  -e POSTGRES_DB=voornameninliedjes \
  postgres:18.4-bookworm

echo 'postgres container started...'

echo 'starting keycloak container...'

docker run -d \
  --name some-keycloak \
  -p 8080:8080 \
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
  -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
  -v "$(pwd)/config:/opt/keycloak/data/import:ro" \
  quay.io/keycloak/keycloak:26.7.4 \
  start-dev --import-realm

echo 'keycloak container started...'

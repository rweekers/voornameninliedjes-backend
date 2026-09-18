#!/bin/sh

API_URL="http://localhost:8080/admin/artists"
TOKEN_FILE=".keycloak-token"

if [ ! -s "$TOKEN_FILE" ]; then
    echo "No access token found. Run ./auth.sh first."
    exit 1
fi

xh --auth-type bearer --auth "$(cat "$TOKEN_FILE")" \
    "$API_URL"
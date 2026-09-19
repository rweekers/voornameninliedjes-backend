#!/bin/sh

SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
TOKEN_FILE="$PROJECT_ROOT/.keycloak-token"

API_URL="http://localhost:8080/admin/artists"

if [ ! -s "$TOKEN_FILE" ]; then
    echo "No access token found. Run ./auth.sh first."
    exit 1
fi

xh --auth-type bearer --auth "$(cat "$TOKEN_FILE")" \
    "$API_URL"
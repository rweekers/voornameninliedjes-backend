#!/bin/sh

KEYCLOAK_URL="http://localhost:8180"
REALM="voornameninliedjes"
CLIENT_ID="voornameninliedjes-beheer"

USERNAME="${1:-admin}"
PASSWORD="$USERNAME"

SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
TOKEN_FILE="$PROJECT_ROOT/.keycloak-token"

echo "Fetching access token for '$USERNAME'..."

curl -sS -X POST \
    "$KEYCLOAK_URL/realms/$REALM/protocol/openid-connect/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "client_id=$CLIENT_ID" \
    -d "username=$USERNAME" \
    -d "password=$PASSWORD" \
    -d "grant_type=password" |
jq -r '.access_token' > "$TOKEN_FILE"

if [ ! -s "$TOKEN_FILE" ] || [ "$(cat "$TOKEN_FILE")" = "null" ]; then
    echo "Failed to obtain access token."
    rm -f "$TOKEN_FILE"
    exit 1
fi

echo "Token stored in $TOKEN_FILE."
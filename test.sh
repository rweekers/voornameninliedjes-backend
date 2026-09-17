#!/bin/sh

KEYCLOAK_URL="http://localhost:8180"
REALM="voornameninliedjes"
CLIENT_ID="voornameninliedjes-admin"

USERNAME="admin"
PASSWORD="admin"

API_URL="http://localhost:8080/admin/artists"

echo "Fetching access token..."

TOKEN=$(
    curl -sS -X POST \
        "$KEYCLOAK_URL/realms/$REALM/protocol/openid-connect/token" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "client_id=$CLIENT_ID" \
        -d "username=$USERNAME" \
        -d "password=$PASSWORD" \
        -d "grant_type=password" |
    jq -r '.access_token'
)

if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
    echo "Failed to obtain access token."
    exit 1
fi

echo "Token obtained."
echo
echo "Calling API..."
echo

curl -i \
    -H "Authorization: Bearer $TOKEN" \
    "$API_URL"

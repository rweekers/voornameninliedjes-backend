#!/bin/sh

if [ $# -ne 1 ]; then
    echo "Usage: $0 <artist-id>"
    exit 1
fi

ARTIST_ID="$1"

SCRIPT_DIR="$(CDPATH= cd -- "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
TOKEN_FILE="$PROJECT_ROOT/.keycloak-token"

API_URL="http://localhost:8080/admin/artists/$ARTIST_ID"

if [ ! -s "$TOKEN_FILE" ]; then
    echo "No access token found. Run ./auth.sh first."
    exit 1
fi

TOKEN="$(cat "$TOKEN_FILE")"

xh --auth-type bearer --auth "$(cat "$TOKEN_FILE")" \
    "$API_URL"

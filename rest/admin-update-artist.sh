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

xh --auth-type bearer --auth "$TOKEN" \
    PUT "$API_URL" \
    name="Updated Test Artist" \
    background="Updated English rock band" \
    mbid="00000000-0000-0000-0000-000000000001" \
    lastFmUrl="https://www.last.fm/music/Updated+Test+Artist" \
    photos:='[
        {
            "imageUrl": "https://example.com/updated-test-artist.jpg",
            "imageAttribution": "Updated photo by Example"
        }
    ]'
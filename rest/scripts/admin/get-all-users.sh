#!/usr/bin/env bash

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/../.env"

xh get "$ADMIN_HOST/users" --auth "$ADMIN:$ADMIN_PASSWORD" | jq
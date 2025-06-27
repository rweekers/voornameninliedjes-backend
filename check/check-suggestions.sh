#!/bin/bash

BASE_URL="https:/api.voornameninliedjes.nl/songs?name-starts-with="

PREFIX_FILE="prefixes.txt"

if [[ ! -f "$PREFIX_FILE" ]]; then
  echo "❌ Prefix file '$PREFIX_FILE' not found."
  exit 1
fi

FAIL=0

while IFS= read -r prefix; do
  url="${BASE_URL}${prefix}"
  echo "🔍 Checking $url"
  status=$(curl -s -o /dev/null -w "%{http_code}" "$url")
  if [[ "$status" -ne 200 ]]; then
    echo "❌ $url failed with status $status"
    FAIL=1
  else
    echo "✅ $url OK"
  fi
  sleep 0.1
done < "$PREFIX_FILE"

exit $FAIL

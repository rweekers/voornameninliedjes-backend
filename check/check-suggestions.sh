#!/bin/bash

BASE_URL="https:/api.voornameninliedjes.nl/songs?name-starts-with="

PREFIX_FILE="$(dirname "$0")/prefixes.txt"

if [[ ! -f "$PREFIX_FILE" ]]; then
  echo "❌ Prefix file '$PREFIX_FILE' not found."
  exit 1
fi

FAIL=0

urlencode() {
  local str="$1"
  local encoded=""
  local i c
  for ((i=0; i<${#str}; i++)); do
    c="${str:$i:1}"
    case "$c" in
      [a-zA-Z0-9.~_-]) encoded+="$c" ;;
      *) encoded+=$(printf '%%%02X' "'$c") ;;
    esac
  done
  echo "$encoded"
}

while IFS= read -r prefix; do
  encoded_prefix=$(urlencode "$prefix")
  url="${BASE_URL}${encoded_prefix}"
  echo "🔍 Checking $url"
  status=$(curl -s -o /dev/null -w "%{http_code}" \
  -H "Accept: application/vnd.voornameninliedjes.songs.v2+json" \
  "$url")
  if [[ "$status" -ne 200 ]]; then
    echo "❌ $url failed with status $status"
    FAIL=1
  else
    echo "✅ $url OK"
  fi
  sleep 0.1
done < "$PREFIX_FILE"

exit $FAIL

#!/bin/bash

BASE_URL="https:/api.voornameninliedjes.nl/songs?name-starts-with="
PREFIX_FILE="$(dirname "$0")/prefixes.txt"
FAIL_LOG="$(mktemp)"

if [[ ! -f "$PREFIX_FILE" ]]; then
  echo "❌ Prefix file '$PREFIX_FILE' not found."
  exit 1
fi

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

check_suggestion() {
  local prefix="$1"
  local encoded_prefix
  encoded_prefix=$(urlencode "$prefix")
  local url="${BASE_URL}${encoded_prefix}"
  echo "🔍 Checking $url"
  local status
  status=$(curl -s -o /dev/null -w "%{http_code}" \
    -H "Accept: application/vnd.voornameninliedjes.songs.v2+json" \
    "$url")
  if [[ "$status" -ne 200 ]]; then
    echo "❌ $url failed with status $status" | tee -a "$FAIL_LOG"
  else
    echo "✅ $url OK"
  fi
}

export -f urlencode
export -f check_prefix
export BASE_URL
export FAIL_LOG


# --- Run calls in parallel ---
cat "$PREFIX_FILE" | xargs -n 1 -P 10 -I {} bash -c 'check_prefix "$@"' _ {}

# --- Final status ---
if [[ -s "$FAIL_LOG" ]]; then
  echo "⚠️ Some requests failed:"
  cat "$FAIL_LOG"
  exit 1
else
  echo "🎉 All requests succeeded."
  exit 0
fi

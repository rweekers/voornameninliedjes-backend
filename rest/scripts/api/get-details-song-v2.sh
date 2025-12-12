#!/usr/bin/env bash

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/../.env"

xh get "$HOST/songs/the police/roxanne" Accept:application/vnd.voornameninliedjes.songs.v2+json | jq

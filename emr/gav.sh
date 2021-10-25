#!/bin/sh

sha=$(shasum -b "$1" | awk '{print $1}')
url="http://search.maven.org/solrsearch/select?q=1:%22${sha}%22&rows=1&wt=json"
if curl -s -o json -L "$url"; then
  gav=$(jq -r '..response.docs[0].g + ":" + .response.docs[0].a + ":" + .response.docs[0].v' json)
  if [ "$gav" != "null" ]; then
    cat gav
  fi
  rm -f json
fi
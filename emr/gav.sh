#!/bin/sh

lib_dir="$1"
if [ "$lib_dir" == "" ]; then
  echo Please specifiy the directory for the jars
  exit 1
fi

sha=$(shasum -b $1 | awk '{print $1}')
url="http://search.maven.org/solrsearch/select?q=1:%22${sha}%22&rows=1&wt=json"
if curl -s -o json -L $url; then
  gid=$(jq -r '.response.docs[0].g' json)
  aid=$(jq -r '.response.docs[0].a' json)
  ver=$(jq -r '.response.docs[0].v' json)
  if [ "$gid" != "null" ]; then
    cat <<EOF
    implementation group: '${gid}', name: '${aid}', version: '${ver}'
EOF
  fi
  rm -f json
fi
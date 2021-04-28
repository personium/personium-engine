#!/bin/bash

PERSONIUM_URL=localhost:8080/personium-core

for i in {1..30}; do
  RESULT=$(curl --silent --fail ${PERSONIUM_URL})
  if [ $? -eq 0 ]; then
    >&2 echo "Personium is ready"
    >&2 echo ${RESULT} | jq .
    exit 0
  else
    >&2 echo "Personium is not ready (${i}): ${RESULT}"
    sleep 10
  fi
done

>&2 echo "Elastic search is unavailable - Timeout"
exit 1

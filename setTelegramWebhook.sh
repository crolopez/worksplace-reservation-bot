#!/bin/bash

ENDPOINT=$1
TELEGRAM_BOT_TOKEN=$2
OPTION="$3"

if [ "$OPTION" == "delete" ]
then
curl --request POST \
  --url "https://api.telegram.org/bot$TELEGRAM_BOT_TOKEN/deleteWebhook" \
  --header "content-type: application/json"
exit 0
fi

curl --request POST \
  --url "https://api.telegram.org/bot$TELEGRAM_BOT_TOKEN/setWebhook" \
  --header "content-type: application/json" \
  --data "{\"url\": \"$ENDPOINT\"}"
 
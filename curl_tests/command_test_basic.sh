#!/bin/bash

SERVLET=http://localhost:9000/device/command

read -d '' DATA <<EOF
{
  "deviceId": "1234",
  "command": "CMD1",
  "arguments" : [
    {"name": "ciccio1", "value": "1234"}
  ]
}
EOF

echo Invoking $SERVLET with the following json:
echo $DATA
echo

echo Begin request...
curl --header "Content-type: application/json" --request POST --data "$DATA" $SERVLET

echo ... End!

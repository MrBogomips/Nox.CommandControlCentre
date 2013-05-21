#!/bin/bash

SERVLET=http://localhost:9000/device/123/execute

read -d '' DATA <<EOF
{
  "device": "1234",
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

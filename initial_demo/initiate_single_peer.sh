if [ $? != 2 ]
then
  echo " Usage: sh ./initiate_single_peer.sh <version> <suffix>"
  exit 1
fi
version=0
if [ "$1" = "1.0" ]
then
  version=1.0
elif [ "$1" = "2.0" ]
then
  version=2.0
else
  echo "Invalid version requested (use 1.0 or 2.0)"
  exit 2
fi

PEER="thisispeer"

access_point="$PEER$2"
x-terminal-emulator -e java project.peer.Peer "$version" "$access_point" 224.0.0.0 8000 224.0.0.64 8001 224.0.0.128 8002
echo "Initiated peer with access point: ${access_point}"
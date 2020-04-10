if [ $? != 1 ]
then
  echo " Usage: sh ./initiate_single_peer.sh <sufix>"
  exit 1
fi

PEER="thisispeer"

access_point="$PEER$1"
x-terminal-emulator -e java project.peer.Peer 1.0 "$access_point" 224.0.0.0 8000 224.0.0.64 8001 224.0.0.128 8002
echo "Initiated peer with access point: ${access_point}"
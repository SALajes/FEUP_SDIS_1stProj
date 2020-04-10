if [ $? != 1 ]
then
  echo "Usage: sh ./initiate_peers.sh <n_peers>"
  exit 1
fi

sh "./finish_peers.sh"

cd ../src

n_peers=$1

if [ $n_peers -lt 2 ]
then
  echo "Number of peers must be more than 1"
  exit 1
fi

PEER="thisispeer"

while [ $n_peers -gt 0 ]
do
  access_point="$PEER$n_peers"
  x-terminal-emulator -e java project.peer.Peer 1.0 "$access_point" 224.0.0.0 8000 224.0.0.64 8001 224.0.0.128 8002
  echo "Initiated peer with access point: ${access_point}"
  n_peers=$((n_peers-1))
done
if [ $? != 1 ]
then
  echo " Usage: sh ./finish_single_peer.sh <sufix>"
  exit 1
fi

PEER="thisispeer"
pkill -f "java.*Peer.*$PEER$1"
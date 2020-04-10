if [ $1 -gt 0 ]
then
  echo "Insert valid number of peer"
  exit 1
fi

PEER="thisispeer"
pkill -f "java.*Peer.*$PEER$1"
if [ $# -lt 2 ]
then
  echo "Usage: sh ./initiate_peers.sh <n_peers> <version> [<start suffix>] [-k]"
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

suffix=1

if [ $# -eq 3 ]
then
    if [ "$3" = "-k" ]
    then sh "./finish_peers.sh"
    elif [ "$3" -gt 0 ]
    then suffix=$3
    else
      echo "Wrong third argument"
      exit 2
    fi
elif [ $# -eq 4 ]
then
  if [ "$3" -gt 0 ]
    then suffix=$3
    else
      echo "Wrong third argument. Expected: <start suffix>"
      exit 2
  fi
  if [ "$4" = "-k" ];
  then
    sh "./finish_peers.sh"
  else
    echo "Invalid forth argument. Expected: -k"
    exit 3
  fi
else
  echo "Usage: sh ./initiate_peers.sh <n_peers> <version> [<start suffix>] [-k]"
  exit 4
fi

cd ../src

n_peers=$1

if [ "$n_peers" -lt 2 ]
then
  echo "Number of peers must be more than 1"
  exit 1
fi

PEER="thisispeer"

while [ "$n_peers" -gt 0 ]
do
  n=$((suffix-1+n_peers))
  access_point="$PEER$n"
  x-terminal-emulator -e java project.peer.Peer "$version" "$access_point" 224.0.0.0 8000 224.0.0.64 8001 224.0.0.128 8002
  echo "Initiated peer with access point: ${access_point}"
  n_peers=$((n_peers-1))
done
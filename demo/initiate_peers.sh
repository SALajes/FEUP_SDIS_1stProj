if [ $# -lt 1 ]
then
  echo "Usage: sh ./initiate_peers.sh <n_peers> <start sufix> <-k>"
  exit 1
fi
echo $2
suffix=1

if [ $# -eq 2 ];
then
    if [ "$2" = "-k" ]
    then sh "./finish_peers.sh"
    elif [ $2 -gt 0 ]
    then suffix=$2
    else
      echo "Wrong second argument"
      exit 2
    fi
elif [ $# -eq 3 ]
then
  if [ $2 -gt 0 ]
    then suffix=$2
    else
      echo "Wrong second argument"
      exit 2
  fi
  if [ "$3" = "-k" ];
  then
    sh "./finish_peers.sh"
  else
    echo "Invalid third argument"
    exit 3
  fi
else
  echo "Usage: sh ./initiate_peers.sh <n_peers> <start sufix> <-k>"
  exit 4
fi

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
  n=$((suffix-1+n_peers))
  access_point="$PEER$n"
  x-terminal-emulator -e java project.peer.Peer 1.0 "$access_point" 224.0.0.0 8000 224.0.0.64 8001 224.0.0.128 8002
  echo "Initiated peer with access point: ${access_point}"
  n_peers=$((n_peers-1))
done
cd ../src

if [ $# -lt 2 ]
then
  echo "Usage: sh ./run_app.sh <peer sufix> <protocol> <argument>*"
  echo "Note: in <peer sufix> the prefix thisispeer is inserted automatically, input the identifier (suffix) only"
  exit 1
fi

PEERid="thisispeer$1"

case $2 in
 "BACKUP")
  if [ $# -ne 4 ]
  then
    echo "Usage: sh ./run_app.sh <peer sufix> BACKUP <file path> <replication degree>"
    exit 2
  else
    x-terminal-emulator -e java project.TestApp "$PEERid" "$2" "$3" "$4"
  fi
  ;;
 "RESTORE")
  if [ $# -ne 3 ]
  then
    echo "Usage: sh ./run_app.sh <peer sufix> RESTORE <file path>"
    exit 2
  else
    x-terminal-emulator -e java project.TestApp "$PEERid" "$2" "$3"
  fi
  ;;
 "DELETE")
  if [ $# -ne 3 ]
  then
    echo "Usage: sh ./run_app.sh <peer sufix> DELETE <file path>"
    exit 2
  else
    x-terminal-emulator -e java project.TestApp "$PEERid" "$2" "$3"
  fi
  ;;
 "RECLAIM")
  if [ $# -ne 3 ]
  then
    echo "Usage: sh ./run_app.sh <peer sufix> RECLAIM <space>"
    exit 2
  else
    x-terminal-emulator -e java project.TestApp "$PEERid" "$2" "$3"
  fi
  ;;
 "STATE")
  if [ $# -ne 2 ]; then
    echo "Usage: sh ./run_app.sh <peer sufix> STATE"
    exit 2
  else
    java project.TestApp "$PEERid" "$2"
  fi
  ;;
esac
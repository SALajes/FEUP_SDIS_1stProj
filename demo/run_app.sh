cd ../src

if [ $# -lt 2 ]
then
  echo "Usage: (sh ./)run_app.sh <peer id> <protocol> <argument>*"
  echo "Note: in <peer id> the prefix thisispeer is inserted automatically, input the identifier (suffix) only"
  exit 1
fi

PEERid="thisispeer$1"

case $2 in
 "BACKUP")
  if [ $# -ne 4 ]
  then
    echo "Usage: (sh ./)run_app.sh <peer id> BACKUP <file path> <replication degree>"
    exit 2
  else
    x-terminal-emulator -e java project.TestApp "$PEERid" "$2" "$3" "$4"
  fi
  ;;
 "RESTORE")
  if [ $# -ne 3 ]
  then
    echo "Usage: (sh ./)run_app.sh <peer id> RESTORE <file path>"
    exit 2
  else
    x-terminal-emulator -e java project.TestApp "$PEERid" "$2" "$3"
  fi
  ;;
 "DELETE")
  if [ $# -ne 3 ]
  then
    echo "Usage: (sh ./)run_app.sh <peer id> DELETE <file path>"
    exit 2
  else
    x-terminal-emulator -e java project.TestApp "$PEERid" "$2" "$3"
  fi
  ;;
 "RECLAIM")
  if [ $# -ne 3 ]
  then
    echo "Usage: (sh ./)run_app.sh <peer id> RECLAIM <space>"
    exit 2
  else
    x-terminal-emulator -e java project.TestApp "$PEERid" "$2" "$3"
  fi
  ;;
 "STATE")
  if [ $# -ne 2 ]; then
    echo "Usage: (sh ./)run_app.sh <peer id> STATE"
    exit 2
  else
    java project.TestApp "$PEERid" "$2"
  fi
  ;;
esac
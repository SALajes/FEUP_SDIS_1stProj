sh ./app.sh 3 RECLAIM 100
sh ./app.sh 9 RECLAIM 40
sh ./app.sh 1 RECLAIM 320

sleep 5

sh ./app.sh 1 STATE
sh ./app.sh 3 STATE
sh ./app.sh 5 STATE
sh ./app.sh 9 STATE
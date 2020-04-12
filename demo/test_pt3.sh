sh ./run_app.sh 3 BACKUP ../resources/Test/test5.jpg 3
sh ./run_app.sh 10 BACKUP ../resources/Test/test3.gif 4
sh ./run_app.sh 5 BACKUP ../resources/Test/test6.txt 3

sleep 7

sh ./run_app.sh 3 RECLAIM 100
sh ./run_app.sh 9 RECLAIM 4
sh ./run_app.sh 1 RECLAIM 320

sleep 5

sh ./run_app.sh 1 STATE
sh ./run_app.sh 3 STATE
sh ./run_app.sh 5 STATE
sh ./run_app.sh 9 STATE
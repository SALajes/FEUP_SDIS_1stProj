sh ./app.sh 3 BACKUP ../resources/Test/test5.jpg 3
sh ./app.sh 10 BACKUP ../resources/Test/test3.gif 4
sh ./app.sh 5 BACKUP ../resources/Test/test6.txt 3

sleep 7

sh ./app.sh 3 DELETE ../resources/Test/test5.jpg
sh ./app.sh 10 DELETE ../resources/Test/test3.gif
sh ./app.sh 5 DELETE ../resources/Test/test6.txt

sleep 5

sh ./app.sh 1 STATE
sh ./app.sh 3 STATE
sh ./app.sh 5 STATE
sh ./app.sh 9 STATE
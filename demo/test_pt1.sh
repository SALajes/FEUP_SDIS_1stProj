sh ./build.sh

sh ./initiate_peers.sh 6 2.0 -k
sh ./initiate_peers.sh 4 1.0 7

sleep 3

sh ./app.sh 1 BACKUP ../resources/Test/test2.jpg 4
sh ./app.sh 2 BACKUP ../resources/Test/test3.gif 7
sh ./app.sh 2 BACKUP ../resources/Test/test5.jpg 3
sh ./app.sh 9 BACKUP ../resources/Test/test3.gif 4
sh ./app.sh 7 BACKUP ../resources/Test/test6.txt 3
sh ./app.sh 3 BACKUP ../resources/Test/test6.txt 8
sh ./app.sh 4 BACKUP ../resources/Test/test2.jpg 5
sh ./app.sh 6 BACKUP ../resources/Test/test4.jpeg 9

sleep 7

sh ./app.sh 1 RESTORE ../resources/Test/test2.jpg
sh ./app.sh 2 RESTORE ../resources/Test/test3.gif
sh ./app.sh 2 RESTORE ../resources/Test/test5.jpg
sh ./app.sh 9 RESTORE ../resources/Test/test3.gif
sh ./app.sh 7 RESTORE ../resources/Test/test6.txt
sh ./app.sh 3 RESTORE ../resources/Test/test6.txt
sh ./app.sh 4 RESTORE ../resources/Test/test2.jpg
sh ./app.sh 6 RESTORE ../resources/Test/test4.jpeg

sleep 7

sh ./app.sh 1 STATE
sh ./app.sh 3 STATE
sh ./app.sh 5 STATE
sh ./app.sh 9 STATE
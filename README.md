# sdis1920-t1g07
Scripts were developed in order to run the program more esily, these can be found in "demo" folder of this project's root.

To run the scripts, please go to the demo folder.


## Build
To build, simply call:
  > sh ./build.sh

in the command line. This script compiles all java files in the src folder.


## Initiate peers
All peers have the same prefix: "thisispeer" and are followed by a suffix, that when generated automatically (by initiate_peers.sh) corresponds to an integer.

It is possible to initiate several peers at the same time calling:
 > sh ./initiate_peers.sh <n_peers> [<start_sufix\>] [<-k\>]

n_peers is the number of peers to initiate.
start suffix and -k are optional.
-k is a flag to finish all previously initiated peers.
start_suffix defines an inferior limit for the interval of peer access point generated ([start_suffix , start_suffix + n_peers]); it is useful when X peers have been initiated and now we want to create Y more peers, we should start with a suffix of X+1
If start_sufix is not given, it will initiate peers with a suffix from 1 to n_peers.

To initiate one single peer:
 > sh ./initiate_single_peer.sh <sufix\>

This will initiate a single peer with the specified sufix.

**Note**: Each peer initiated opens and executes in a new terminal window.


## Finish peers 
It is possible to finish all peers through:
 > sh ./finish_peers.sh

Or one single peer by calling a similar script and indicating its sufix:
 > sh ./finish_single_peer.sh <sufix\>


## Run the TestApp
To execute a protocol through the TestApp, run:
 > sh ./run_app <peer sufix\> <protocol\> <argument\>*

Next are specified the arguments of each protocol:

For BACKUP:
 > sh ./run_app.sh <peer sufix\> BACKUP <file path\> <replication degree\>

For RESTORE:
 > sh ./run_app.sh <peer sufix\> RESTORE <file path\>

For DELETE:
 > sh ./run_app.sh <peer sufix\> DELETE <file path\>

For RECLAIM:
 > sh ./run_app.sh <peer sufix\> RECLAIM <space\>

For STATE:
 > sh ./run_app.sh <peer sufix\> STATE

##Directories

Each peer has its own directory, being [peer id]_directory. 
Inside this directory there are three folders, one for the own files, named “files”, other of the restore files, name “restored” .
There is a third folder for the store chunks, which are the other peer chunks, named “stored”.  <br>
When a chunk is store, a folder with is file id is created, and the chunks add are inside that folder with a name that corresponds to is chunk number.


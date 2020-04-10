# sdis1920-t1g07
Scripts were developed in order to run the program more esily, these can be found in "demo" folder of this project's root.

To run the scripts, please go to the demo folder.


## Build
To build, simply call:
  > sh ./build.sh

in the command line. This script compiles all java files in the src folder.


## Initiate peers
All peers have the same prefix: "thisispeer".

It is possible to initiate several peers at the same time calling:
 > sh ./initiate_peers.sh <n_peers\>

n_peers is the number of peers to initiate.
This will initiate peers with sufix from 1 to n_peers.
This script also finishes all previously created peers.

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

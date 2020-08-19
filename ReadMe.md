# Distributed Systems / Sistemas Distribuídos - 2019/2020
First project of SDIS curricular unit: **Distributed Backup Service**

Link for the first project: https://github.com/SALajes/FEUP_SDIS_2ndProj

## Java SE
The Java SE 11.0.6 was the version used on the development of this project.
The 'project' module is located inside 'src' folder.

## Report
The report describing this project's concurrency and enhancements implementations can be found on 'documents' folder.

## Scripts
The scripts for the demo were included as requested:

- compile.sh (since our src folder has one project package with many subpackages inside, we had to make a slight change in the project's structure)
- peer.sh
- test.sh
- cleanup.sh (added code fragment for deleting a peer's designated directory)

(setup.sh and rmiregistry.sh were not included for they were not necessary: the program creates the directories for each peer and creates the registry if it doesn't already exist)

Note: We had previously developed scripts for a demo. The scripts and text file explaining how to run it can be found in 'initial_demo' folder of this project's root.


##Filesystem Structure

In order to allow the test of several peers on a single computer, each peer uses its own filesystem subtree to keep the chunks it is backing up, the files it has recovered, its own metadata and files. The name of that subtree is the format [peer id]_directory.
Inside this directory there are three folders, one for the own files, named “files”, other of the restored files, name “restored” .
There is a third folder for the store chunks, which are the other peer chunks, named “stored”.
When a chunk is store, a folder with is file id is created, and the chunks add are inside that folder with a name that corresponds to is chunk number.



##Authors
Maria Helena Ferreira -- *up201704508*
Sofia de Araújo Lajes -- *up201704066*

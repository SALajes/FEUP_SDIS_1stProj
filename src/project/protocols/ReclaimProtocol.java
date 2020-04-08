package project.protocols;

import project.chunk.Chunk;
import project.message.BaseMessage;
import project.message.PutChunkMessage;
import project.message.RemovedMessage;
import project.message.StoredMessage;
import project.peer.Peer;
import project.store.FileManager;
import project.store.FilesListing;
import project.store.Store;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ReclaimProtocol {
    public static void send_removed(Double version, Integer sender_id, String file_id, Integer chunk_number){
        RemovedMessage removedMessage = new RemovedMessage(version, sender_id, file_id, chunk_number);
        Runnable task = () -> process_remove_message(removedMessage);
        new Thread(task).start();

    }

    public static void process_remove_message(RemovedMessage removedMessage){
        Peer.MC.send_message(removedMessage.convert_message());
    }


    public static void receive_removed(RemovedMessage removedMessage ){

        String file_id = removedMessage.getFile_id();
        Integer chunk_number = removedMessage.get_chunk_number();
        String chunk_id = file_id + "_" + chunk_number;

        System.out.println("------------------");
        System.out.println("Received remove: with file_id " + file_id +
                " and chunk number "+ chunk_number);
        System.out.println("------------------");

        //check if this is this peer with a file
        if(Store.getInstance().check_backup_chunks_occurrences(chunk_id) != -1) {

            //update local count of this chunk replication degree
            Store.getInstance().remove_Backup_chunk_occurrence(chunk_id, removedMessage.getSender_id());

        } else if (Store.getInstance().check_stored_chunks_occurrences(chunk_id) != -1 ){

            Store.getInstance().remove_stored_chunk_occurrence(chunk_id, removedMessage.getSender_id() );

            //check if count drops below the desired replication degree of that chunk
            if(!Store.getInstance().has_replication_degree(chunk_id)) {
                System.out.println("File lost is replication degree");

                Runnable task = ()-> send_putchunk(Peer.version, Peer.id, Store.getInstance().get_replication_degree(chunk_id), file_id, FileManager.retrieveChunk(file_id, chunk_number));

                //initiate the chunk backup subprotocol after a random delay uniformly distributed between 0 and 400 ms
                Peer.scheduled_executor.schedule(task, new Random().nextInt(401), TimeUnit.MILLISECONDS);
            }
        }
    }

    public static void send_putchunk(double version, int sender_id, int replication_degree, String file_id, Chunk chunk) {
        //send put chunk

        PutChunkMessage putchunk = new PutChunkMessage(version, sender_id, file_id, chunk.chunk_no, replication_degree, chunk.content);

        String chunk_id = file_id + "_" + chunk.chunk_no;

        process_putchunk(putchunk.convert_message(), putchunk.getReplicationDegree(), chunk_id, 0);

    }

    private static void process_putchunk(byte[] message, int replication_degree, String chunk_id, int tries) {

        if(tries > 5){
            System.out.println("Put chunk failed desired replication degree: " + chunk_id);
            return;
        }

        if ( Store.getInstance().has_replication_degree(chunk_id)) {
            System.out.println("Backed up " + chunk_id + " with desired replication_degree");
            return;
        }

        Peer.MDB.send_message(message);

        int try_aux = tries + 1;
        long time = (long) Math.pow(2, try_aux-1);
        Runnable task = () -> process_putchunk(message, replication_degree, chunk_id, try_aux);
        Peer.scheduled_executor.schedule(task, time, TimeUnit.SECONDS);
    }
}
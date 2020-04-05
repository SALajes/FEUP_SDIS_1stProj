package project.protocols;

import project.chunk.Chunk;
import project.message.BaseMessage;
import project.message.PutChunkMessage;
import project.message.RemovedMessage;
import project.message.StoredMessage;
import project.peer.Peer;
import project.store.FilesListing;
import project.store.Store;

import java.util.ArrayList;

public class ReclaimProtocol {
    public static void send_removed(Double version, Integer sender_id, String file_id, Integer chunk_number){
        RemovedMessage removedMessage = new RemovedMessage(version, sender_id, file_id, chunk_number);
        Runnable task = () -> process_remove_message(removedMessage);
        new Thread(task).start();

    }

    public static void process_remove_message(RemovedMessage removedMessage){

    }



    public static void receive_removed(RemovedMessage removedMessage ){

        String file_id = removedMessage.getFile_id();
        System.out.println("------------------");
        System.out.println("Received remove: with file_id " + file_id +
                " and chunk number "+ removedMessage.get_chunk_number());
        System.out.println("------------------");

        //check if this is this peer file
        if(FilesListing.get_files_Listing().get_file_name(file_id) == null)
            return;

        //TODO update local count of this chunk replication degree

        // If this count drops below the desired replication degree of that chunk, it shall initiate the chunk backup subprotocol
        // after a random delay uniformly distributed between 0 and 400 ms. If during this delay, a peer receives a PUTCHUNK
        // message for the same file chunk, it should back off and restrain from starting yet another backup subprotocol for that
        // file chunk.
    }
}

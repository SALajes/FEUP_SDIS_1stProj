package project.protocols;

import project.chunk.Chunk;
import project.message.PutChunkMessage;
import project.message.StoredMessage;
import project.peer.Peer;
import project.store.FileManager;
import project.store.FilesListing;
import project.store.Store;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BackupProtocol {
    public static void send_putchunk(double version, int sender_id, int replication_degree, String file_id, ArrayList<Chunk> chunks) {
        //sends putchunks
        for (Chunk chunk : chunks) {
            PutChunkMessage putchunk = new PutChunkMessage(version, sender_id, file_id, chunk.chunk_no, replication_degree, chunk.content);

            String chunk_id = file_id + "_" + chunk.chunk_no;

            Store.getInstance().new_Backup_chunk(chunk_id, replication_degree);

            Runnable task = () -> process_putchunk(putchunk.convert_message(), putchunk.getReplicationDegree(), chunk_id, 0);
            Peer.scheduled_executor.execute(task);
        }
    }

    private static void process_putchunk(byte[] message, int replication_degree, String chunk_id, int tries) {
        if(tries > 5){
            System.out.println("Putchunk failed desired replication degree: " + chunk_id);
            return;
        }

        if (Store.getInstance().check_backup_chunks_occurrences(chunk_id) >= replication_degree) {
            System.out.println("Backed up " + chunk_id + " with desired replication_degree");
            return;
        }

        Peer.MDB.send_message(message);

        int try_aux = tries+1;
        long time = (long) Math.pow(2, try_aux-1);
        Runnable task = () -> process_putchunk(message, replication_degree, chunk_id, try_aux);
        Peer.scheduled_executor.schedule(task, time, TimeUnit.SECONDS);
    }

    public static void receive_putchunk(PutChunkMessage putchunk){

        String file_id = putchunk.getFile_id();

        if(FileManager.storeChunk(file_id, putchunk.getChunkNo(), putchunk.getChunk(), putchunk.getReplicationDegree())){
            StoredMessage stored = new StoredMessage(putchunk.getVersion(), Peer.id, putchunk.getFile_id(), putchunk.getChunkNo());

            Runnable task = ()->send_stored(stored.convert_message());
            Peer.scheduled_executor.schedule(task, new Random().nextInt(401), TimeUnit.MILLISECONDS);
        }
    }

    private static void send_stored(byte[] message){
        Peer.MC.send_message(message);
    }

    public static void receive_stored(StoredMessage message){
        String file_id = message.getFile_id();
        String chunk_id = file_id + "_" + message.getChunk_no();
        Integer peer_id = message.getSender_id();

        if(FilesListing.get_files_Listing().get_file_name(file_id) != null) {
            Store.getInstance().add_Backup_chunks_occurrences(chunk_id, peer_id );
        } else {
            //adds replication degree of the stored file
            Store.getInstance().add_replication_degree(file_id, message.getChunk_no(), peer_id);
        }

    }
}

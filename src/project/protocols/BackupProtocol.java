package project.protocols;

import project.chunk.Chunk;
import project.message.PutChunkMessage;
import project.message.StoredMessage;
import project.peer.Peer;
import project.store.Store;

import java.util.ArrayList;

public class BackupProtocol {
    public static void send_putchunk(double version, int sender_id, int replication_degree, String file_id, ArrayList<Chunk> chunks){
        //sends putchunks
        for(int i = 0; i < chunks.size(); i++){
            PutChunkMessage putchunk = new PutChunkMessage(version, sender_id, file_id, chunks.get(i).chunk_no, replication_degree, chunks.get(i).content);

            Runnable task = () -> process_putchunk(putchunk);

            new Thread(task).start();
        }
    }

    public static void process_putchunk(PutChunkMessage putchunk){
        int tries = 1;
        String chunk_id = putchunk.getFile_id() + "_" + putchunk.getChunkNo();
        while(tries <= 5){
            Peer.MDB.send_message(putchunk.convert_message());

            try{
                Thread.sleep(1000*tries);

                if(Store.getInstance().check_backup_chunks_occurrences(chunk_id) >= putchunk.getReplicationDegree()){
                    break;
                }

                tries++;

            } catch (InterruptedException e) {
                e.getStackTrace();
                break;
            }
        }
        if(tries > 5){
            System.out.println("Putchunk failed desired replication degree: <file_id: " + putchunk.getFile_id() + "> <chunk_no: " + putchunk.getChunkNo() + ">");
        }
    }

    public static void receive_putchunk(PutChunkMessage putchunk){
        System.out.println("Received putchunk: ");
        System.out.println(putchunk.getFile_id());
        System.out.println(putchunk.getChunkNo());
        System.out.println("------------------");
        if(Store.getInstance().storeChunk(putchunk.getFile_id(), putchunk.getChunkNo(), putchunk.getChunk().getBytes())){
            StoredMessage stored = new StoredMessage(putchunk.getVersion(), Peer.id, putchunk.getFile_id(), putchunk.getChunkNo());

            Peer.MC.send_message(stored.convert_message());
        }
    }

    public static void receive_stored(StoredMessage message){
        System.out.println("Received stored: ");
        System.out.println(message.getFile_id());
        System.out.println(message.getChunk_no());
        System.out.println("------------------");
        String chunk_id = message.getFile_id() + "_" + message.getChunk_no();
        Store.getInstance().add_Backup_chunks_occurrences(chunk_id, message.getSender_id());
    }
}

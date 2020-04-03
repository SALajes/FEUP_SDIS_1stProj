package project.protocols;

import project.chunk.Chunk;
import project.message.BaseMessage;
import project.message.PutChunkMessage;
import project.peer.Peer;

import java.util.ArrayList;

public class BackupProtocol {
    public static void send_putchunk(double version, int sender_id, int replication_degree, String file_id, ArrayList<Chunk> chunks){
        //sends putchunk
        for(int i = 0; i < chunks.size(); i++){
            PutChunkMessage putchunk = new PutChunkMessage(version, sender_id, file_id, chunks.get(i).chunk_no, replication_degree, chunks.get(i).content);

            //lançar cada putchunk na sua thread

            Peer.MDB.send_message(putchunk.convert_message());

            //é suposto esperar pelo store?
        }
    }

    public static void receive_putchunk(){

    }

    public static void send_store(){

    }

    public static void receive_store(BaseMessage message){

    }
}

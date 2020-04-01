package project.protocols;

import project.chunk.Chunk;
import project.message.BaseMessage;
import project.message.PutChunkMessage;
import project.peer.Peer;

import java.util.ArrayList;

public class RestoreProtocol {
    public static void send_getchunk(String version, String sender_id, int replication_degree, String file_id, ArrayList<Chunk> chunks){

    }

    public static void receive_getchunk(BaseMessage message){

    }

    public static void send_chunk(){
    }

    public static void receive_chunk(){

    }
}

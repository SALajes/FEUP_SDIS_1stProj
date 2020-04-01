package project.protocols;

import project.chunk.Chunk;
import project.message.BaseMessage;
import project.message.PutChunkMessage;
import project.peer.Peer;

import java.util.ArrayList;

public class ReclaimProtocol {
    public static void send_removed(String version, String sender_id, int replication_degree, String file_id, ArrayList<Chunk> chunks){

    }

    public static void receive_removed(BaseMessage message){

    }
}

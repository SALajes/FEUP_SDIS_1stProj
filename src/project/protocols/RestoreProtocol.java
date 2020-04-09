package project.protocols;

import project.chunk.Chunk;
import project.message.ChunkMessage;
import project.message.GetChunkMessage;
import project.peer.Peer;
import project.store.FileManager;
import project.store.FilesListing;
import project.store.Store;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RestoreProtocol {

    public static void sendGetchunk(double version, Integer sender_id, String file_id, int number_of_chunks){
        //Restore all chunks
        for(int i = 0; i < number_of_chunks; i++) {
            GetChunkMessage getChunkMessage = new GetChunkMessage(version, sender_id, file_id, i);

            Runnable task = () -> processGetchunk(getChunkMessage);
            Peer.scheduled_executor.execute(task);
        }
    }

    public static void processGetchunk(GetChunkMessage getChunkMessage){
        Peer.MC.sendMessage(getChunkMessage.convertMessage());
    }

    /**
     * a peer that has a copy of the specified chunk shall send it in the body of a CHUNK message via the MDR channel
     * @param getChunkMessage message received
     */
    public static void receiveGetchunk(GetChunkMessage getChunkMessage){
        String file_id = getChunkMessage.getFile_id();
        Integer chunk_number = getChunkMessage.get_chunk_no();
        Chunk chunk = FileManager.retrieveChunk( file_id, chunk_number);

        if(chunk != null)
            sendChunk(getChunkMessage.getVersion(), Peer.id, file_id, chunk_number, chunk.content);
    }

    public static void sendChunk(double version, Integer sender_id, String file_id, Integer chunk_no, byte[] chunk_data){
        ChunkMessage chunkMessage = new ChunkMessage(version, sender_id, file_id, chunk_no, chunk_data);

        String chunk_id = chunkMessage.getFile_id() + "_" + chunkMessage.getChunkNo();
        Store.getInstance().addGetchunkReply(chunk_id);

        Runnable task = () -> processChunk(chunkMessage, chunk_id);
        Peer.scheduled_executor.schedule(task, new Random().nextInt(401), TimeUnit.MILLISECONDS);
    }

    public static void processChunk(ChunkMessage chunkMessage, String chunk_id){
        if(!Store.getInstance().getGetchunkReply(chunk_id))
            Peer.MDR.sendMessage(chunkMessage.convertMessage());
        Store.getInstance().removeGetchunkReply(chunk_id);
    }

    public static void receiveChunk(ChunkMessage chunkMessage){
        String file_id = chunkMessage.getFile_id();
        String file_name = FilesListing.getInstance().getFileName(file_id);

        String chunk_id = file_id + "_" + chunkMessage.getChunkNo();

        if(Store.getInstance().checkBackupChunksOccurrences(chunk_id) != -1) {
            FileManager.writeChunkToRestoredFile(file_name, chunkMessage.getChunk(), chunkMessage.getChunkNo());
        }
        Store.getInstance().checkGetchunkReply(chunk_id);
    }
}

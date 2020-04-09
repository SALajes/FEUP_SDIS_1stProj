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

    public static void send_getchunk(double version, Integer sender_id,  String file_id, int number_of_chunks){
        //Restore all chunks
        for(int i = 0; i < number_of_chunks; i++) {
            GetChunkMessage getChunkMessage = new GetChunkMessage(version, sender_id, file_id, i);

            Runnable task = () -> process_getchunk(getChunkMessage);
            Peer.scheduled_executor.execute(task);
        }
    }

    public static void process_getchunk(GetChunkMessage getChunkMessage){
        Peer.MC.send_message(getChunkMessage.convert_message());
    }

    /**
     * a peer that has a copy of the specified chunk shall send it in the body of a CHUNK message via the MDR channel
     * @param getChunkMessage message received
     */
    public static void receive_getchunk(GetChunkMessage getChunkMessage){
        String file_id = getChunkMessage.getFile_id();
        Integer chunk_number = getChunkMessage.get_chunk_no();
        Chunk chunk = FileManager.retrieveChunk( file_id, chunk_number);

        if(chunk != null)
            send_chunk(getChunkMessage.getVersion(), Peer.id, file_id, chunk_number, chunk.content);
    }

    public static void send_chunk(double version, Integer sender_id,  String file_id, Integer chunk_no, byte[] chunk_data){
        ChunkMessage chunkMessage = new ChunkMessage(version, sender_id, file_id, chunk_no, chunk_data);

        String chunk_id = chunkMessage.getFile_id() + "_" + chunkMessage.get_chunk_no();
        Store.getInstance().add_getchunk_reply(chunk_id);

        Runnable task = () -> process_chunk(chunkMessage, chunk_id);
        Peer.scheduled_executor.schedule(task, new Random().nextInt(401), TimeUnit.MILLISECONDS);
    }

    public static void process_chunk(ChunkMessage chunkMessage, String chunk_id){
        if(!Store.getInstance().get_getchunk_reply(chunk_id))
            Peer.MDR.send_message(chunkMessage.convert_message());
        Store.getInstance().remove_getchunk_reply(chunk_id);
    }

    public static void receive_chunk(ChunkMessage chunkMessage){
        String file_id = chunkMessage.getFile_id();
        String file_name = FilesListing.getInstance().getFileName(file_id);

        String chunk_id = file_id + "_" + chunkMessage.get_chunk_no();

        if(Store.getInstance().check_backup_chunks_occurrences(chunk_id) != -1) {
            FileManager.write_chunk_to_restored_file(file_name, chunkMessage.getChunk(), chunkMessage.get_chunk_no());
        }
        Store.getInstance().check_getchunk_reply(chunk_id);
    }
}

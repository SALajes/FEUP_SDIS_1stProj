package project.protocols;

import project.Macros;
import project.chunk.Chunk;
import project.message.*;
import project.peer.Peer;
import project.store.FileManager;
import project.store.FilesListing;
import project.store.Store;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RestoreProtocol {

    public static void sendGetchunk(double version, Integer sender_id, String file_id, int number_of_chunks){

        if(version == Macros.VERSION_ENHANCEMENT && Peer.version == Macros.VERSION_ENHANCEMENT) {
            System.out.println("Restore version 2.0");
            ServerSocket server_socket = null;

            try {
                 server_socket = new ServerSocket(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Integer port = server_socket.getLocalPort();
            SocketAddress address = server_socket.getLocalSocketAddress();
           // InetAddress inetAddress = server_socket.getInetAddress();

            try {
                server_socket.setSoTimeout(100000);
            } catch (SocketException e) {
                e.printStackTrace();
                System.err.println("No one respond");
            }

            //Restore all chunks
            for (int i = 0; i < number_of_chunks; i++) {

                GetChunkEnhancementMessage getChunkEnhancementMessage = new GetChunkEnhancementMessage(version, sender_id, file_id, i, port , address.toString());
                Runnable task = () -> processGetchunkEnhancement(getChunkEnhancementMessage);
                Peer.scheduled_executor.execute(task);
            }

            get_chunks(server_socket);

            try {
                server_socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            //Restore all chunks
            for (int i = 0; i < number_of_chunks; i++) {
                GetChunkMessage getChunkMessage = new GetChunkMessage(version, sender_id, file_id, i);

                Runnable task = () -> processGetchunk(getChunkMessage);
                Peer.scheduled_executor.execute(task);
            }
        }
    }



    public static void processGetchunk(GetChunkMessage getChunkMessage){
        Peer.MC.sendMessage(getChunkMessage.convertMessage());
    }

    public static void processGetchunkEnhancement(GetChunkEnhancementMessage getChunkMessage){
        System.out.println("Sending getChunk version 2.0");
        Peer.MC.sendMessage(getChunkMessage.convertMessage());
    }

    public static void get_chunks(ServerSocket server_socket ){
        try {
            final Socket socket = server_socket.accept();

            System.out.println("A peer open the socket to transfer!");

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            // ObjectIntputStream is atomic
            StoredMessage storedMessage = (StoredMessage) objectInputStream.readObject();

            String file_name = FilesListing.getInstance().getFileName(storedMessage.getFile_id());
            FileManager.writeChunkToRestoredFile(file_name, storedMessage.getChunk(), storedMessage.getChunkNo());
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * a peer that has a copy of the specified chunk shall send it in the body of a CHUNK message via the MDR channel
     * @param  getChunkMessage message received
     */
    public static void receiveGetchunk(GetChunkMessage getChunkMessage ){

        System.out.println("receive get chunk version " + getChunkMessage.getVersion());
        String file_id = getChunkMessage.getFile_id();

        Integer chunk_number = getChunkMessage.get_chunk_no();
        Chunk chunk = FileManager.retrieveChunk(file_id, chunk_number);

        if (chunk == null)
           return;

        sendChunk(getChunkMessage.getVersion(), Peer.id, file_id, chunk_number, chunk.content);

    }

    public static void receiveGetchunkEnhacement(GetChunkEnhancementMessage getChunkEnhancementMessage) {

        System.out.println("receive get chunk version " + getChunkEnhancementMessage.getVersion());

        Integer chunk_number = getChunkEnhancementMessage.get_chunk_no();
        String file_id = getChunkEnhancementMessage.getFile_id();
        Chunk chunk = FileManager.retrieveChunk(file_id, chunk_number);

        if (chunk == null)
            return;

        sendChunk(getChunkEnhancementMessage.getVersion(), Peer.id, file_id, chunk_number, new byte[0]);

        //send chunk
        try {
            System.out.println("open socket");
            InetAddress inetAddress = InetAddress.getByName(getChunkEnhancementMessage.get_address());
            Socket socket = new Socket(inetAddress, getChunkEnhancementMessage.get_port());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(chunk);
        } catch (IOException e) {
            e.printStackTrace();
        }


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

        //in version 2.0 chunk_data is null
        if(Peer.version == Macros.VERSION || chunkMessage.getVersion() == Macros.VERSION) {
            if (Store.getInstance().checkBackupChunksOccurrences(chunk_id) != -1) {
                FileManager.writeChunkToRestoredFile(file_name, chunkMessage.getChunk(), chunkMessage.getChunkNo());
            }
        }
        Store.getInstance().checkGetchunkReply(chunk_id);
    }


}

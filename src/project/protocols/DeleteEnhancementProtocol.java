package project.protocols;

import project.message.DeleteMessage;
import project.message.ReceiveDeleteMessage;
import project.peer.Peer;
import project.store.FileManager;
import project.store.FilesListing;
import project.store.Store;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DeleteEnhancementProtocol {

    public static void sendDelete(double version, int sender_id, String file_id){
        DeleteMessage deleteMessage = new DeleteMessage( version, sender_id, file_id);
        Runnable task = () -> process_delete_enhancement(deleteMessage.convertMessage(), file_id, 0);
        Peer.scheduled_executor.execute(task);
    }

    public static void process_delete_enhancement(byte[] message, String file_id, int tries){

        if(tries >= 5){
            System.out.println("Couldn't delete all chunks of the file " + file_id);
            return;
        }

        if (Store.getInstance().check_if_all_deleted(file_id)) {
            System.out.println("Delete all chunks of the file " + file_id);
            return;
        }

        System.out.println("Sending delete with version 2.0");

        Peer.MC.sendMessage(message);

        int try_aux = tries+1;

        long time = (long) Math.pow(3, try_aux-1);
        Runnable task = () -> process_delete_enhancement(message, file_id, try_aux);
        Peer.scheduled_executor.schedule(task, time, TimeUnit.SECONDS);

    }

    public static void receiveDelete(DeleteMessage deleteMessage){

        System.out.println("Receive Delete");
        String file_id = deleteMessage.getFile_id();

        //delete all files and records in stored
        FileManager.deleteFileFolder(Store.getInstance().getStoreDirectoryPath() + file_id);
        Store.getInstance().removeStoredChunks(file_id);

        sendReceiveDelete(deleteMessage.getVersion(), Peer.id, file_id);

    }

    public static void sendReceiveDelete(double version, int sender_id, String file_id){

        System.out.println("Sending Receive Delete");
        ReceiveDeleteMessage receiveDeleteMessage = new ReceiveDeleteMessage(version, sender_id, file_id);

        Runnable task = () -> processReceiveDelete(receiveDeleteMessage);
        //Message ar short, Time can be a low integer
        Peer.scheduled_executor.schedule(task, new Random().nextInt(21), TimeUnit.MILLISECONDS);
    }

    public static void processReceiveDelete(ReceiveDeleteMessage receivedeleteMessage){
        Peer.MC.sendMessage(receivedeleteMessage.convertMessage());
    }

    public static void receiveReceiveDelete(ReceiveDeleteMessage message) {

        System.out.println("Receive ReceiveDelete");

        Integer peer_id = message.getSender_id();
        String file_id = message.getFile_id();

        String file_name = FilesListing.getInstance().getFileName(file_id);
        Integer number_of_chunks = FilesListing.getInstance().get_number_of_chunks(file_name);

        for(int i = 0; i < number_of_chunks; i++ ) {
            String chunk_id = file_id + "_" + i;
            //remove peer from the list of chunks backup, if chunk doesn't exists it's fine
            Store.getInstance().removeBackupChunkOccurrence(chunk_id, peer_id);

        }

        System.out.println("Delete all chunks of file " + file_id + "on peer " + message.getSender_id());
    }
}

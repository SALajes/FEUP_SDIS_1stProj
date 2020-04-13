package project.protocols;

import project.Macros;
import project.message.*;
import project.peer.Peer;
import project.store.FileManager;
import project.store.FilesListing;
import project.store.Store;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DeleteProtocol {

    public static void sendDelete(double version, int sender_id, String file_id){

        if(Peer.version == Macros.VERSION && version == Macros.VERSION) {

            DeleteMessage deleteMessage = new DeleteMessage( version, sender_id, file_id);
            Runnable task = () -> processDelete(deleteMessage);
            Peer.scheduled_executor.execute(task);

            //Sends twice because protocol says ". An implementation may send this message as many times as it is
            // deemed necessary to ensure that all space used by chunks of the deleted
            // file are deleted in spite of the loss of some messages."
            Peer.scheduled_executor.schedule(task, new Random().nextInt(201), TimeUnit.MILLISECONDS);

        } else if(Peer.version == Macros.VERSION_ENHANCEMENT && version == Macros.VERSION_ENHANCEMENT ) {

            DeleteMessage deleteMessage = new DeleteMessage( version, sender_id, file_id);
            Runnable task = () -> process_delete_enhancement(deleteMessage.convertMessage(), file_id, 0);
            Peer.scheduled_executor.execute(task);
        }

    }

    public static void processDelete(DeleteMessage deleteMessage){
        Peer.MC.sendMessage(deleteMessage.convertMessage());
    }

    public static void receiveDelete(DeleteMessage deleteMessage){

        if(Peer.version == Macros.VERSION && deleteMessage.getVersion() == Macros.VERSION ) {

            String file_id = deleteMessage.getFile_id();

            //delete all files and records in stored
            FileManager.deleteFileFolder(Store.getInstance().getStoreDirectoryPath() + file_id);
            Store.getInstance().removeStoredChunks(file_id);

        } else if(Peer.version == Macros.VERSION_ENHANCEMENT && deleteMessage.getVersion() == Macros.VERSION_ENHANCEMENT) {

            System.out.println("Receive Delete");
            String file_id = deleteMessage.getFile_id();

            //delete all files and records in stored
            FileManager.deleteFileFolder(Store.getInstance().getStoreDirectoryPath() + file_id);
            Store.getInstance().removeStoredChunks(file_id);

            sendReceiveDelete(deleteMessage.getVersion(), Peer.id, file_id);
        }

    }

    // -------------  Delete enhancement  -----------------------------------------

    public static void process_delete_enhancement(byte[] message, String file_id, int tries){

        if(tries >= 5){
            
            System.out.println("Couldn't delete all chunks of the file " + file_id);
            Store.getInstance().change_from_backup_to_delete(file_id);
            return;
        }

        if (Store.getInstance().check_if_all_deleted(file_id)) {
            System.out.println("Delete all chunks of the file " + file_id);

            String file_name = FilesListing.getInstance().getFileName(file_id);
            Integer number_of_chunks = FilesListing.getInstance().get_number_of_chunks(file_name);

            for(int i = 0; i< number_of_chunks; i++ ) {
                String chunk_id = file_id + "_" + i;
                Store.getInstance().removeBackupChunksOccurrences( chunk_id);
            }

            // Remove entry with the file_name and correspond file_id from allFiles
            FilesListing.getInstance().delete_file_records(file_name, file_id); //no reason to keep them

            return;
        }

        System.out.println("Sending delete with version 2.0");

        Peer.MC.sendMessage(message);

        int try_aux = tries + 1;

        long time = (long) Math.pow(3, try_aux-1);
        Runnable task = () -> process_delete_enhancement(message, file_id, try_aux);
        Peer.scheduled_executor.schedule(task, time, TimeUnit.SECONDS);

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

        Integer peer_id = message.getSender_id();
        String file_id = message.getFile_id();

        String file_name = FilesListing.getInstance().getFileName(file_id);
        Integer number_of_chunks = FilesListing.getInstance().get_number_of_chunks(file_name);

        for(int i = 0; i < number_of_chunks; i++ ) {
            String chunk_id = file_id + "_" + i;
            //remove peer from the list of chunks backup, if chunk doesn't exists it's fine
            Store.getInstance().removeBackupChunkOccurrence(chunk_id, peer_id);

        }

        System.out.println("Confirm deletion all chunks of file " + file_id + " on peer " + message.getSender_id());
    }
}



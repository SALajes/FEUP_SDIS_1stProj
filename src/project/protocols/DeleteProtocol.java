package project.protocols;

import project.message.*;
import project.peer.Peer;
import project.store.FileManager;
import project.store.Store;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DeleteProtocol {

    public static void sendDelete(double version, int sender_id, String file_id){
        DeleteMessage deleteMessage = new DeleteMessage( version, sender_id, file_id);
        Runnable task = () -> processDelete(deleteMessage);
        Peer.scheduled_executor.execute(task);

        //Sends twice because protocol says ". An implementation may send this message as many times as it is
        // deemed necessary to ensure that all space used by chunks of the deleted
        // file are deleted in spite of the loss of some messages."
        Peer.scheduled_executor.schedule(task, new Random().nextInt(201), TimeUnit.MILLISECONDS);
    }

    public static void processDelete(DeleteMessage deleteMessage){
        Peer.MC.sendMessage(deleteMessage.convertMessage());
    }

    public static void receiveDelete(DeleteMessage deleteMessage){
        String file_id = deleteMessage.getFile_id();

        //delete all files and records in stored
        FileManager.deleteFileFolder(Store.getInstance().getStoreDirectoryPath() + file_id);
        Store.getInstance().removeStoredChunks(file_id);
    }
}

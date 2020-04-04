package project.protocols;

import project.message.BaseMessage;
import project.message.DeleteMessage;

public class DeleteProtocol {

    public static void send_delete(double version, int sender_id, String file_id){
        DeleteMessage deleteMessage = new DeleteMessage( version, sender_id, file_id);
    }

    public static void receive_delete(BaseMessage message){

    }
}

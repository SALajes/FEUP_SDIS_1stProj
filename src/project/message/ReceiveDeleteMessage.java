package project.message;

public class ReceiveDeleteMessage extends BaseMessage {

    public ReceiveDeleteMessage(double version, int sender_id, String file_id) {
        super(version, Message_Type.RECEIVEDELETE, sender_id, file_id);
    }


}

package project.message;

import project.Macros;

public class DeleteMessage extends BaseMessage {
    public DeleteMessage(String version, String sender_id, String file_id) {
        super(version, Message_type.DELETE, sender_id, file_id);
    }
}

package project.message;

import project.Macros;

public class DeleteMessage extends BaseMessage {
    public DeleteMessage(double version, int sender_id, String file_id) {
        super(version, Message_type.DELETE, sender_id, file_id);
    }
}

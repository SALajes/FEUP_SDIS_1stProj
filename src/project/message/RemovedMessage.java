package project.message;

import project.Macros;

public class RemovedMessage extends BaseMessage {
    private final int chunk_no;

    public RemovedMessage(String version, String sender_id, String file_id, int chunk_no) {
        super(version, Message_type.REMOVED, sender_id, file_id);

        this.chunk_no = chunk_no;
    }

    @Override
    public String get_header(){
        return super.get_header() + " " + chunk_no;
    }
}

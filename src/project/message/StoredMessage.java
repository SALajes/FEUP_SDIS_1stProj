package project.message;

import project.Macros;

public class StoredMessage extends BaseMessage {
    private final int chunk_no;

    public StoredMessage(String version, String sender_id, String file_id, int chunk_no) {
        super(version, Message_type.STORED, sender_id, file_id);

        this.chunk_no = chunk_no;
    }

    @Override
    public String get_header(){
        return super.get_header() + " " + chunk_no;
    }
}
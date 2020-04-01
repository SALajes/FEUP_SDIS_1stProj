package project.message;

import project.Macros;

public class GetChunkMessage extends BaseMessage {
    private final int chunk_no;

    public GetChunkMessage(double version, int sender_id, String file_id, int chunk_no) {
        super(version, Message_type.GETCHUNK, sender_id, file_id);

        this.chunk_no = chunk_no;
    }

    @Override
    public String get_header(){
        return super.get_header() + " " + chunk_no;
    }
}
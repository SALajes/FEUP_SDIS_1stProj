package project.message;

import project.Macros;

public class PutChunkMessage extends BaseMessage {
    private final int chunk_no;
    private final int replication_degree;

    public PutChunkMessage(String version, String sender_id, String file_id, int chunk_no, byte[] chunk, int replication_degree) {
        super(version, Message_type.PUTCHUNK, sender_id, file_id);

        this.chunk_no = chunk_no;
        this.chunk = new String(chunk, 0, chunk.length);
        this.replication_degree = replication_degree;
    }

    @Override
    public String get_header(){
        return super.get_header() + " " + chunk_no + " " + replication_degree;
    }
}

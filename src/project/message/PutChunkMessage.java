package project.message;

import project.Macros;

public class PutChunkMessage extends BaseMessage {
    private final int chunk_no;
    private final int replication_degree;

    public PutChunkMessage(String version, String sender_id, String file_id, int chunk_no, int replication_degree, byte[] chunk) {
        super(version, Message_type.PUTCHUNK, sender_id, file_id);

        this.chunk_no = chunk_no;
        this.replication_degree = replication_degree;
        this.chunk = new String(chunk, 0, chunk.length);
    }

    @Override
    public String get_header(){
        return super.get_header() + " " + chunk_no + " " + replication_degree;
    }
}

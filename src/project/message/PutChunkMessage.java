package project.message;

import project.Macros;

public class PutChunkMessage extends BaseMessage {
    private final int chunk_no;
    private final int replication_degree;

    public PutChunkMessage(double version, int sender_id, String file_id, int chunk_no, int replication_degree, byte[] chunk) throws InvalidMessageException {
        super(version, Message_type.PUTCHUNK, sender_id, file_id);

        this.chunk_no = chunk_no;
        //replication degree of the chunk  is a digit, thus allowing a replication degree of up to 9. It takes one byte, which is the ASCII code of that digit.
        if(replication_degree <= 0 || replication_degree > 9)
            throw new InvalidMessageException("Replication degree is invalid");

        this.replication_degree = replication_degree;
        this.chunk = new String(chunk, 0, chunk.length);
    }

    @Override
    public String get_header(){
        return super.get_header() + " " + chunk_no + " " + replication_degree;
    }
}

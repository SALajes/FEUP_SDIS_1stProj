package project.message;

/**
 *  Only message type possible is PUTCHUNK that was a BODY
 */
class MessageWRepDegree extends MessageWBody {

    private final int chunk_no;
    private final int replication_degree;

    public MessageWRepDegree(String version, Message_type message_type, String sender_id, String file_id, int chunk_no,
            int replication_degree, byte[] message, int message_length) {
        super(version, message_type, sender_id, file_id, message, message_length);
        this.chunk_no = chunk_no;
        this.replication_degree = replication_degree;
    }

    public int getReplication_degree() {
        return replication_degree;
    }

    public int getChunk_no() {
        return chunk_no;
    }
}
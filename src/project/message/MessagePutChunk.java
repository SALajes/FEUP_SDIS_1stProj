package project.message;

/**
 *   Possible messages_types are STORED, GETCHUNK, REMOVED
 */

public class MessagePutChunk extends MessageWBody {

    private final int chunk_no;

    public MessageWChunk_no(String version, String message_type, String sender_id, String file_id, int chunk_no,
            byte[] message, int message_length) {
        super(version, message_type, sender_id, file_id, message, message_length);
        this.chunk_no = chunk_no;

    }

    public int getChunk_no() {
        return chunk_no;
    }

}
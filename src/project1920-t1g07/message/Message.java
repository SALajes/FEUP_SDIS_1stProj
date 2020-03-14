public class Message {
    //Header
    private final String version;
    private final String message_type;
    private final String sender_id;
    private final String file_id;
    private final int chunk_no;
    private final int replication_degree;
    //terminated with the sequence '0xD''0xA' - <CRLF>

    //   BODY
    protected final byte[] message;
    protected final int message_length;

    public Message(String version, String message_type, String sender_id, String file_id, int chunk_no,
            int replication_degree, byte[] message, int message_length) {
        this.version = version;
        this.message_type = message_type;
        this.sender_id = sender_id;
        this.file_id = file_id;
        this.chunk_no = chunk_no;
        this.replication_degree = replication_degree;
        this.message = message;
        this.message_length = message_length;
    }

    public int getReplication_degree() {
        return replication_degree;
    }

    public int getChunk_no() {
        return chunk_no;
    }

    public String getFile_id() {
        return file_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getVersion() {
        return version;
    }

    public String getMessage_type() {
        return message_type;
    }

}
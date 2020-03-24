package project.message;


/**
 *   Possible messages_types are STORED, GETCHUNK, REMOVED
 *   All without <BODY>
 */

public class MessageWChunk_no extends BaseMessage {

    private final int chunk_no;

    public MessageWChunk_no(String version, Message_type message_type, String sender_id, String file_id, int chunk_no) {
        super(version, message_type, sender_id, file_id );
        this.chunk_no = chunk_no;
    }

}
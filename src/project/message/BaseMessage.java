package message;

/**
 * fields common to all messages
 * Message used when message_type == DELETE
 */
public class BaseMessage {
    //Header
    private final String version;
    private final Message_type message_type;
    private final String sender_id;
    private final String file_id;
    //terminated with the sequence '0xD''0xA' '0xD''0xA' - <CRLF><CRLF>
    public final static byte CR = 0xD;
    public final static byte LF = 0xA;

    public enum Message_type {
        PUTCHUNK,
        STORED,
        GETCHUNK,
        CHUNK,
        DELETE,
        REMOVED
    }

    public BaseMessage(String version, Message_type message_type, String sender_id, String file_id) {
        this.version = version;
        this.message_type = message_type;
        this.sender_id = sender_id;
        this.file_id = file_id;
    }

    public String getVersion() {
        return version;
    }

    public Message_type getMessage_type() {
        return message_type;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getFile_id() {
        return file_id;
    }

}
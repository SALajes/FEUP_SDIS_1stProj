package project.message;

import project.Macros;

/**
 * fields common to all messages
 */
public abstract class BaseMessage {
    //Header
    protected final String version;
    protected final Message_type message_type;
    protected final String sender_id;
    protected final String file_id;
    protected String chunk;

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
        this.chunk = null;
    }

    public String get_header(){
        return this.version + " " + this.message_type + " " + this.sender_id + " " + this.file_id;
    }

    public byte[] convert_message(){
        String message = get_header() + " " + Macros.CR + Macros.LF + Macros.CR + Macros.LF + this.chunk;
        return message.getBytes();
    }
}
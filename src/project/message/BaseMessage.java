package project.message;

import project.InvalidFileException;
import project.Macros;

import java.util.Arrays;

/**
 * fields common to all messages
 */
public abstract class BaseMessage {
    //Header
    private final String version;
    private final Message_type message_type;
    private final String sender_id;
    private final String file_id;
    protected String chunk;

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
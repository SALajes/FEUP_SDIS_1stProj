package message;

public class MessageWBody extends BaseMessage {
    //   BODY - for messages PUTCHUNK, CHUNK
    protected final byte[] message;
    protected final int message_length;

    public MessageWBody(String version, Message_type message_type, String sender_id, String file_id, byte[] message, int message_length) {
        super(version, message_type, sender_id, file_id);

        this.message = message;
        this.message_length = message_length;
    }

    public byte[] getMessage() {
        return message;
    }

    public int getMessage_Length() {
        return message_length;
    }

}
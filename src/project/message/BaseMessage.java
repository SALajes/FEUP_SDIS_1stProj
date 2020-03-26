package project.message;

import project.InvalidFileException;
import project.Macros;

import java.util.Arrays;

/**
 * fields common to all messages
 */
public class BaseMessage {
    //Header
    private final String version;
    private final Message_type message_type;
    private final String sender_id;
    private final String file_id;
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

    /**
     *
     * @param message
     * @param message_length
     * @param start_position used because some message have 2 CRLF
     * @return
     */
    public int getCRLFPosition(byte[] message, int message_length, int start_position) {

        for (int i = start_position; i < message_length - 1; ++i) {
            if (message[i] == Macros.CR && message[i + 1] == Macros.LF) {
                return i;
            }
        }

        return -1;
    }


    public static byte[][] splitInChunks(byte[] file_data) throws InvalidFileException {

        byte[][] chunks_data;
        if(file_data.length > Macros.FILE_MAX_SIZE) {
            throw new InvalidFileException();
        }

        int needed_chunks = (int) Math.ceil((float) file_data.length / Macros.CHUNK_MAX_SIZE);
        boolean needs_0_size_chunk = false;


        if (file_data.length % Macros.CHUNK_MAX_SIZE == 0) {
            needs_0_size_chunk = true;
        }

        // If the file size is a multiple of the chunk size, the last chunk has size 0.
        if(needs_0_size_chunk) {
            chunks_data = new byte[needed_chunks + 1][];
            chunks_data[needed_chunks] = new byte[0];
        } else {
            chunks_data = new byte[needed_chunks][];
        }

        int current_position = 0;

        for (int j = 0; j < needed_chunks; j++) {

            int chunk_size = Math.min(Macros.CHUNK_MAX_SIZE, file_data.length - current_position);
            chunks_data[j] = Arrays.copyOfRange(file_data, current_position, current_position + chunk_size);
            current_position += Macros.CHUNK_MAX_SIZE;
        }

        return chunks_data;
    }


}
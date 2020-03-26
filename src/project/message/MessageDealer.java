package project.message;

import project.InvalidFileException;
import project.Macros;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageDealer {

    /**
     *
     * @param message
     * @param message_length
     * @param initial_position used because some message have 2 CRLF
     * @return initial position of the CRLF or -1 if not found
     */
    public int getCRLFPosition(byte[] message, int message_length, int initial_position) {

        for (int i = initial_position; i < message_length - 1; ++i) {
            if (message[i] == Macros.CR && message[i + 1] == Macros.LF) {
                return i;
            }
        }

        return -1;
    }

    public List<String> getMessageHeaderFields( String header) throws InvalidMessageException {
        List<String> header_fields = Arrays.asList(header.split(" "));

        //Shorter header is "<Version> DELETE <SenderId> <FileId> <CRLF><CRLF>"
        if (header_fields.size() <= 4) {
            throw new InvalidMessageException();
        }
        return header_fields;
    }

    public BaseMessage parseFields(byte[] message, int message_length) throws InvalidMessageException {
        //The last header line is always an empty line, i.e. the <CRLF> ASCII character sequence
        int first_CRLF_position = getCRLFPosition(message, message_length, 0);
        if (first_CRLF_position < 0) {
            throw new InvalidMessageException();
        }


        List<String> message_header = getMessageHeaderFields(new String(message, 0, first_CRLF_position));
        if(Double.parseDouble(message_header.get(0)) != Macros.VERSION) {
            throw new InvalidMessageException();
        }

        switch (Message_type.valueOf(message_header.get(1))) {
            case PUTCHUNK:
                return new PutChunkMessage(
                        message_header.get(0), //version
                        message_header.get(2), //sender_id
                        message_header.get(3), //file_id
                        Integer.parseInt(message_header.get(4)), //chunk_no
                        message, //TODO only send body
                        Integer.parseInt(message_header.get(4)) //replication_dregree
                );
            case STORED:
                return new StoredMessage(
                        message_header.get(0), //version
                        message_header.get(2), //sender_id
                        message_header.get(3), //file_id
                        Integer.parseInt(message_header.get(4)) //chunk_no
                        //message without a body
                );
            case GETCHUNK:
                return new GetChunkMessage(
                        message_header.get(0), //version
                        message_header.get(2), //sender_id
                        message_header.get(3), //file_id
                        Integer.parseInt(message_header.get(4)) //chunk_no
                        //message without a body
                );
            case CHUNK:
                return new ChunkMessage(
                        message_header.get(0), //version
                        message_header.get(2), //sender_id
                        message_header.get(3), //file_id
                        Integer.parseInt(message_header.get(4)), //chunk_no
                        message //TODO only send body
                );
            case DELETE:
                return new DeleteMessage(
                        message_header.get(0), //version
                        message_header.get(2), //sender_id
                        message_header.get(3) //file_id
                        //message without a body
                );
            case REMOVED:
                return new RemovedMessage(
                        message_header.get(0), //version
                        message_header.get(2), //sender_id
                        message_header.get(3), //file_id
                        Integer.parseInt(message_header.get(4)) //chunk_no
                        //message without a body
                );
            default:
                    throw new InvalidMessageException();

        }

    }


    public byte[][] splitInChunks(byte[] file_data) throws InvalidFileException {

        if(file_data.length > Macros.FILE_MAX_SIZE) {
            throw new InvalidFileException();
        }

        int needed_chunks = (int) Math.ceil((float) file_data.length / Macros.CHUNK_MAX_SIZE);
        boolean needs_0_size_chunk = false;
        byte[][] chunks_data;

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

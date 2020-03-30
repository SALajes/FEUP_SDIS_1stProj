package project.message;

import project.InvalidFileException;
import project.Macros;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.copyOfRange;

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

    /**
     *
     * @param header
     * @return
     * @throws InvalidMessageException
     */
    public List<String> getMessageHeaderFields( String header) throws InvalidMessageException {
        List<String> header_fields = Arrays.asList(header.split(" "));

        //Shorter header is "<Version> DELETE <SenderId> <FileId> <CRLF><CRLF>"
        if (header_fields.size() <= 4) {
            throw new InvalidMessageException();
        }
        return header_fields;
    }

    /**
     *
     * @param message
     * @param message_length
     * @param first_CRLF_position
     * @return
     */
    public byte[] getMessageBody(byte[] message, int message_length, int first_CRLF_position){
        int second_CRLF_position = getCRLFPosition(message, message_length, first_CRLF_position + 2);
        //if CRLF is not found Message is Invalid
        if (second_CRLF_position == -1 ) {
            try {
                throw new InvalidMessageException();
            } catch (InvalidMessageException e) {
                e.printStackTrace();
            }
        }

        //public static int[] copyOfRange(int[] original_array, int from_index, int to_index)
        return copyOfRange(message, first_CRLF_position + 2, second_CRLF_position);
    }

    /**
     *
     * @param message
     * @param message_length
     * @return
     * @throws InvalidMessageException
     */
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
                        getMessageBody(message, message_length, first_CRLF_position), //only send body
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
                        getMessageBody(message, message_length, first_CRLF_position) //only send body
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


}
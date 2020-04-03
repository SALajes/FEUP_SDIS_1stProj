package project.message;

import project.Macros;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.copyOfRange;

public class MessageParser {
    /**
     *
     * @param header
     * @return
     * @throws InvalidMessageException
     */
    public static List<String> getMessageHeaderFields(String header) throws InvalidMessageException {
        List<String> header_fields = Arrays.asList(header.split(" "));

        //Shortest header is "<Version> DELETE <SenderId> <FileId> <CRLF><CRLF>"
        if (header_fields.size() <= 4) {
            throw new InvalidMessageException();
        }
        return header_fields;
    }

    /**
     *
     * @param message
     * @return
     * @throws InvalidMessageException
     */
    public static BaseMessage parseMessage(String message, Message_type type) throws InvalidMessageException {
        message = message.trim();

        String terminal = "" + Macros.CR + Macros.LF + Macros.CR + Macros.LF;
        String[] header_body = message.split(terminal);

        List<String> message_header = getMessageHeaderFields(header_body[0]);

        if(Double.parseDouble(message_header.get(0)) != Macros.VERSION) {
            throw new InvalidMessageException();
        }

        type = Message_type.valueOf(message_header.get(1));

        switch (type) {
            case PUTCHUNK:
                return new PutChunkMessage(
                        Double.parseDouble(message_header.get(0).trim()), //version
                        Integer.parseInt(message_header.get(2).trim()), //sender_id
                        message_header.get(3).trim(), //file_id
                        Integer.parseInt(message_header.get(4).trim()), //chunk_no
                        Integer.parseInt(message_header.get(5).trim()), //replication degree
                        header_body[1].trim().getBytes() //chunk
                );
            case STORED:
                return new StoredMessage(
                        Double.parseDouble(message_header.get(0).trim()), //version
                        Integer.parseInt(message_header.get(2).trim()), //sender_id
                        message_header.get(3).trim(), //file_id
                        Integer.parseInt(message_header.get(4).trim()) //chunk_no
                );
            case GETCHUNK:
                return new GetChunkMessage(
                        Double.parseDouble(message_header.get(0).trim()), //version
                        Integer.parseInt(message_header.get(2).trim()), //sender_id
                        message_header.get(3), //file_id
                        Integer.parseInt(message_header.get(4)) //chunk_no
                        //message without a body
                );
            case CHUNK:
                return new ChunkMessage(
                        Double.parseDouble(message_header.get(0).trim()), //version
                        Integer.parseInt(message_header.get(2).trim()), //sender_id
                        message_header.get(3).trim(), //file_id
                        Integer.parseInt(message_header.get(4).trim()), //chunk_no
                        header_body[1].trim().getBytes() //chunk
                );
            case DELETE:
                return new DeleteMessage(
                        Double.parseDouble(message_header.get(0).trim()), //version
                        Integer.parseInt(message_header.get(2).trim()), //sender_id
                        message_header.get(3).trim() //file_id
                );
            case REMOVED:
                return new RemovedMessage(
                        Double.parseDouble(message_header.get(0).trim()), //version
                        Integer.parseInt(message_header.get(2).trim()), //sender_id
                        message_header.get(3).trim(), //file_id
                        Integer.parseInt(message_header.get(4).trim()) //chunk_no
                );
            default:
                    throw new InvalidMessageException();

        }

    }
}

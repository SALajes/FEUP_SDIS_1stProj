package project.message;

public class ChunkMessage extends BaseMessage{
    private final int chunk_no;

    public ChunkMessage(String version, String sender_id, String file_id, int chunk_no, byte[] chunk) {
        super(version, Message_type.CHUNK, sender_id, file_id);

        this.chunk_no = chunk_no;
        this.chunk = new String(chunk, 0, chunk.length);
    }

    @Override
    public String get_header(){
        return super.get_header() + " " + chunk_no;
    }
}

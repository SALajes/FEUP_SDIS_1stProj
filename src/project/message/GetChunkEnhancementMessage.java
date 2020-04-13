package project.message;


import java.net.InetAddress;
import java.net.UnknownHostException;

public class GetChunkEnhancementMessage extends BaseMessage {

    private final Integer chunk_no;
    private final Integer port;
    private final String address;

    public GetChunkEnhancementMessage(double version, int sender_id, String file_id, Integer chunk_no, Integer port, InetAddress address) {
        super(version, Message_Type.GETCHUNK, sender_id, file_id);

        this.chunk_no = chunk_no;
        this.port = port;
        this.address = address.toString();
    }

    public GetChunkEnhancementMessage(Double version, int sender_id, String file_id, int chunk_no, int port, String address) {
        super(version, Message_Type.GETCHUNK, sender_id, file_id);

        this.chunk_no = chunk_no;
        this.port = port;
        this.address = address;

    }

    public Integer get_chunk_no() {
        return chunk_no;
    }

    public Integer get_port() {
        return port;
    }

    public String get_address() {
        return address;
    }

    @Override
    public String getHeader(){
        return super.getHeader() + " " + chunk_no + " " + port + " " + address;
    }
}

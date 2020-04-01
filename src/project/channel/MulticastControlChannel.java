package project.channel;

import project.message.*;
import project.protocols.BackupProtocol;
import project.protocols.DeleteProtocol;
import project.protocols.ReclaimProtocol;
import project.protocols.RestoreProtocol;

import java.net.DatagramPacket;

public class MulticastControlChannel extends Channel {

    public MulticastControlChannel(String address, int port) {
        super(address, port);
    }

    @Override
    public void readable_message(DatagramPacket packet) {
        String raw_message = new String(packet.getData(), 0, packet.getData().length);
        Message_type type = Message_type.NO_TYPE;

        try {
            BaseMessage message = MessageParser.parseMessage(raw_message, type);

            switch (type) {
                case STORED:
                    BackupProtocol.receive_store(message);
                    break;
                case GETCHUNK:
                    RestoreProtocol.receive_getchunk(message);
                    break;
                case DELETE:
                    DeleteProtocol.receive_delete(message);
                    break;
                case REMOVED:
                    ReclaimProtocol.receive_removed(message);
                    break;
            }
        } catch (InvalidMessageException e) {
            e.printStackTrace();
        }
    }
}
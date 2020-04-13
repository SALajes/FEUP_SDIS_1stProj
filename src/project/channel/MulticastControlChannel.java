package project.channel;

import project.Macros;
import project.message.*;
import project.peer.Peer;
import project.protocols.*;

import java.net.DatagramPacket;

public class MulticastControlChannel extends Channel {

    public MulticastControlChannel(String address, int port) {
        super(address, port);
    }

    @Override
    protected void readableMessage(DatagramPacket packet) {

        try {
            byte [] raw_message = packet.getData();
            BaseMessage message = MessageParser.parseMessage(raw_message, raw_message.length);

            if(message.getSender_id() == Peer.id){
                return;
            }

            switch (message.getMessage_type()) {
                case STORED:
                    BackupProtocol.receiveStored((StoredMessage) message);
                    break;
                case GETCHUNK:
                    RestoreProtocol.receiveGetchunk((GetChunkMessage) message);
                    break;
                case DELETE:
                    if(message.getVersion() == Macros.VERSION){
                        DeleteProtocol.receiveDelete((DeleteMessage) message);
                    }
                    else{
                        DeleteEnhancementProtocol.receiveDelete((DeleteMessage) message);
                    }
                    break;
                case RECEIVEDELETE:
                    DeleteEnhancementProtocol.receiveReceiveDelete((ReceiveDeleteMessage) message);
                    break;
                case REMOVED:
                    ReclaimProtocol.receiveRemoved((RemovedMessage) message);
                    break;
                default:
                    System.out.println("Invalid message type for Control Channel: " + message.getMessage_type());
                    break;
            }
        } catch (InvalidMessageException e) {
            e.printStackTrace();

        }
    }
}
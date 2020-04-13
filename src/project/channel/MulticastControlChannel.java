package project.channel;

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

            if(message.getSenderId() == Peer.id){
                return;
            }

            switch (message.getMessageType()) {
                case STORED:
                    BackupProtocol.receiveStored((StoredMessage) message);
                    break;
                case GETCHUNK:
                   RestoreProtocol.receiveGetchunk((GetChunkMessage) message);
                   break;
                case GETCHUNKENHANCED:
                    RestoreProtocol.receiveGetchunkEnhacement((GetChunkEnhancementMessage) message);
                case DELETE:
                    DeleteProtocol.receiveDelete((DeleteMessage) message);
                    break;
                case RECEIVEDELETE:
                    DeleteProtocol.receiveReceiveDelete((ReceiveDeleteMessage) message);
                    break;
                case REMOVED:
                    ReclaimProtocol.receiveRemoved((RemovedMessage) message);
                    break;
                default:
                    System.out.println("Invalid message type for Control Channel: " + message.getMessageType());
                    break;
            }
        } catch (InvalidMessageException e) {
            e.printStackTrace();

        }
    }
}
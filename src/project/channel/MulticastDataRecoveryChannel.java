package project.channel;

import project.Macros;
import project.message.*;
import project.peer.Peer;
import project.protocols.RestoreProtocol;

import java.net.DatagramPacket;

public class MulticastDataRecoveryChannel extends Channel {

    public MulticastDataRecoveryChannel(String address, int port) {
        super(address, port);
    }

    @Override
    protected void readableMessage(DatagramPacket packet) {
        try {
            byte [] raw_message = packet.getData();
            BaseMessage message = MessageParser.parseMessage(raw_message, packet.getLength());

            if(message.getSender_id() == Peer.id)
                return;

            if(message.getMessage_type() == Message_Type.CHUNK) {
                RestoreProtocol.receiveChunk((ChunkMessage) message);
            }

            else System.out.println("Invalid message type for Control Channel: " + message.getMessage_type());


        } catch (InvalidMessageException e) {
            e.getMessage();
        }
    }
}
package project.channel;

import project.message.*;
import project.peer.Peer;
import project.protocols.BackupProtocol;

import java.net.DatagramPacket;

public class MulticastDataBackupChannel extends Channel {

    public MulticastDataBackupChannel(String address, int port) {
        super(address, port);
    }

    @Override
    protected void readableMessage(DatagramPacket packet) {
        try {

            final byte[] raw_message = packet.getData();

            BaseMessage message = MessageParser.parseMessage(raw_message, packet.getLength());

            if(message.getSenderId() == Peer.id){
                return;
            }

            if(message.getMessageType() == Message_Type.PUTCHUNK){
                BackupProtocol.receivePutchunk((PutChunkMessage) message);
            }
            else System.out.println("Invalid message type for Control Channel: " + message.getMessageType());

        } catch (InvalidMessageException e) {
            e.getMessage();
        }
    }
}
package project.channel;

import project.message.*;
import project.protocols.BackupProtocol;

import java.net.DatagramPacket;

public class MulticastDataBackupChannel extends Channel {

    public MulticastDataBackupChannel(String address, int port) {
        super(address, port);
    }

    @Override
    public void readable_message(DatagramPacket packet) {
        String raw_message = new String(packet.getData(), 0, packet.getData().length);
        Message_type type = Message_type.NO_TYPE;

        try {
            BaseMessage message = MessageParser.parseMessage(raw_message, type);

            if(type == Message_type.PUTCHUNK)
                BackupProtocol.receive_putchunk();

        } catch (InvalidMessageException e) {
            e.printStackTrace();
        }
    }
}
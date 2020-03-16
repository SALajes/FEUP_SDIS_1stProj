package Channels;

import java.net.UnknownHostException;

public class MulticastDataBackupChannel extends Channel {

    public MulticastDataBackupChannel(String address, int port) throws UnknownHostException {
        super(address, port);
    }

    @Override
    public void run() {

    }
}
package Channels;

import java.net.UnknownHostException;

public class MulticastDataRecoveryChannel extends Channel {

    public MulticastDataRecoveryChannel(String address, int port) throws UnknownHostException {
        super(address, port);
    }
    @Override
    public void run() {

    }
}
package Channels;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class Channel implements Runnable {
    public String address;
    public int port;
    public InetAddress InetAddress;

    public Channel(String address, int port ) throws UnknownHostException {
        this.address = address;
        this.port = port;
        InetAddress = InetAddress.getByName(address);
    }
    @Override
    public abstract void run();
}

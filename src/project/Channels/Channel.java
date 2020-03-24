package project.Channels;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class Channel implements Runnable {
    public String address;
    public int port;
    public InetAddress InetAddress;

    public Channel(String address, int port ) {
        this.address = address;
        this.port = port;

        try {
            InetAddress = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    @Override
    public abstract void run();
}

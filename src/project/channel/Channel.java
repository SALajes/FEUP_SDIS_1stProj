package project.channel;

import project.message.BaseMessage;
import project.message.InvalidMessageException;

import java.io.IOException;
import java.net.*;

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

    public void send_message(byte[] message){
        try{
            DatagramSocket socket = new DatagramSocket();

            DatagramPacket packet = new DatagramPacket(message, message.length, this.InetAddress, this.port);

            socket.send(packet);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void readable_message(DatagramPacket packet);

    @Override
    public void run(){
        try {
            byte[] buffer = new byte[1024];

            MulticastSocket socket = new MulticastSocket(this.port);

            socket.joinGroup(this.InetAddress);

            while(true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);

                readable_message(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

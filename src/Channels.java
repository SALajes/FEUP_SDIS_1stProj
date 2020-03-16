import java.net.InetAddress;
import java.rmi.UnknownHostException;

public class Channels{
    public static String MC_address;
    public static int MC_port;
    public static InetAddress MC_InetAddr;

    public static String MDB_address;
    public static int MDB_port;
    public static InetAddress MDB_InetAddress;

    public static String MDR_address;
    public static int MDR_port;
    public static InetAddress MDR_InetAddress;

    public Channels(String[] args) throws UnknownHostException, java.net.UnknownHostException {


        MC_InetAddr = InetAddress.getByName(MC_address);
        MDB_InetAddress = InetAddress.getByName(MDB_address);
        MDR_InetAddress = InetAddress.getByName(MDR_address);
    }
}
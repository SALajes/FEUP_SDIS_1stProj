public class Channels{
    public static String MC_address;
    public static int MC_port;
    public static InetAddress MC_InetAddr;

    public static String MDB_address;
    public static int MDB_address;
    public static InetAddress MC_InetAddr;

    public static String MDR_address;
    public static int MDR_port;
    public static InetAddress MC_InetAddr;

    public Channels(String[] args) throws UnknownHostException{
        this.MC_address = args[3];
        this.MC_port = Integer.parseInt(args[4]);

        this.MDB_address = args[5];
        this.MDB_port = Integer.parseInt(args[6]);

        this.MDR_address = args[7];
        this.MDR_port = Integer.parseInt(args[8]);

        MC_InetAddr = InetAddress.getByName(MC_address);
        MDB_InetAddr = InetAddress.getByName(MDB_address);
        MDR_InetAddr = InetAddress.getByName(MDR_address);
    }
}
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Peer implements RemoteInterface {
    private final int RegistryPort = 1099;

    private static int id;
    private static double protocol_version;

    private static String service_access_point;

    //Addresses, ports and InetAdress of each channel
    private static Channels channels;


    //class methods
    public static void main(String[] args){
        if(args.length != 9){
            System.out.println("Usage: java Peer format must be: Peer <protocol_version> <peer_id> <service_access_point> " +
                    "<MC_address> <MC_port> <MDB_address> <MDB_port> <MDR_address> <MDR_port>");
            return;
        }

        try{
            this.protocol_version = Double.parseDouble(args[0]);
            this.id = Integer.parseInt(args[1]);

            //since we are using RMI transport protocol, then the access_point is <remote_object_name>
            this.service_access_point = args[2];

            this.channels = new Channels(args);

            Peer object_peer = new Peer();
            RemoteInterface stub = (RemoteInterface) UnicastRemoteObject.exportObject(object_peer, 0);

            Registry registry = LocateRegistry.createRegistry(this.RegistryPort);
            registry.rebind(this.service_access_point, stub);

            System.out.println("Peer ready");

        } catch (Exception e) {
            System.err.println("Peer exception: " + e.toString());
            e.printStackTrace();
            return;
        }
    } //TO-DO: THREAD POOL PARA MC, MDB E MDR


    void backup(String file_path) throws RemoteException{

    }
}
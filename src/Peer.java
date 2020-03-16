import java.io.File;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import Channels.ControlChannel;
import Channels.MulticastDataBackupChannel;
import Channels.MulticastDataRecoveryChannel;

public class Peer implements RemoteInterface {
    private static final int RegistryPort = 1099;

    private static int id;
    private static double protocol_version;

    private static String service_access_point;

    //Addresses, ports and InetAdress of each channel
    private static ControlChannel MC;
    private static MulticastDataBackupChannel MDB;
    private static MulticastDataRecoveryChannel MDR;

    //class methods
    public static void main(String[] args){
        if(args.length != 9){
            System.out.println("Usage: java Peer format must be: Peer <protocol_version> <peer_id> <service_access_point> " +
                    "<MC_address> <MC_port> <MDB_address> <MDB_port> <MDR_address> <MDR_port>");
            return;
        }

        try{
            protocol_version = Double.parseDouble(args[0]);
            id = Integer.parseInt(args[1]);

            //since we are using RMI transport protocol, then the access_point is <remote_object_name>
            service_access_point = args[2];

            MC = new ControlChannel(args[3], Integer.parseInt(args[4]));
            MDB = new MulticastDataBackupChannel(args[5], Integer.parseInt(args[6]));
            MDR = new MulticastDataRecoveryChannel(args[7], Integer.parseInt(args[8]));

            Peer object_peer = new Peer();
            RemoteInterface stub = (RemoteInterface) UnicastRemoteObject.exportObject(object_peer, 0);

            Registry registry = LocateRegistry.createRegistry(RegistryPort);
            registry.rebind(service_access_point, stub);

            System.out.println("Peer ready");

        } catch (Exception e) {
            System.err.println("Peer exception: " + e.toString());
            e.printStackTrace();
            return;
        }
    } //TO-DO: THREAD POOL PARA MC, MDB E MDR


    public void backup(String file_path, int replication_degree) throws RemoteException{
        final String file_name = new File(file_path).getName();
    }

    @Override
    public void restore(String file_path) throws RemoteException {

    }

    @Override
    public void delete(String file_path) throws RemoteException {

    }
}
package project.peer;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import project.Macros;

import project.channel.ControlChannel;
import project.channel.MulticastDataBackupChannel;
import project.channel.MulticastDataRecoveryChannel;

public class Peer implements RemoteInterface {
    private static final int RegistryPort = 1099;

    private static int id;

    private static String service_access_point;

    //Addresses, ports and InetAdress of each channel
    private static ControlChannel MC;
    private static MulticastDataBackupChannel MDB;
    private static MulticastDataRecoveryChannel MDR;

    public Peer(String MC_address, int MC_port, String MDB_address, int MDB_port, String MDR_address, int MDR_port) throws RemoteException {
        MC = new ControlChannel(MC_address, MC_port);
        MDB = new MulticastDataBackupChannel(MDB_address, MDB_port);
        MDR = new MulticastDataRecoveryChannel(MDR_address, MDR_port);
    }

    //class methods
    public static void main(String[] args){
        if(args.length != 9){
            System.out.println("Usage: [project.]Peer <protocol_version> <peer_id> <service_access_point> " +
                    "<MC_address> <MC_port> <MDB_address> <MDB_port> <MDR_address> <MDR_port>");
            return;
        }

        try{
            if( Double.parseDouble(args[0]) != Macros.VERSION) {
                System.out.println("version not recognize");
            }

            id = Integer.parseInt(args[1]);

            //since we are using RMI transport protocol, then the access_point is <remote_object_name>
            service_access_point = args[2];

            Peer object_peer = new Peer(args[3], Integer.parseInt(args[4]), args[5], Integer.parseInt(args[6]), args[7], Integer.parseInt(args[8]));
            RemoteInterface stub = (RemoteInterface) UnicastRemoteObject.exportObject(object_peer, 0);

            Registry registry = LocateRegistry.createRegistry(RegistryPort);
            registry.rebind(service_access_point, stub);

            System.out.println("project.Peer ready");

        } catch (Exception e) {
            System.err.println("project.Peer exception: " + e.toString());
            e.printStackTrace();
            return;
        }
    } //TO-DO: THREAD POOL PARA MC, MDB E MDR


    /**
     *
     * @param file_path
     * @param replication_degree
     * @throws RemoteException
     * The client shall specify the file pathname and the desired replication degree.
     */
    public int backup(String file_path, int replication_degree) throws RemoteException{
        File file = new File(file_path);
        String file_name = file.getName();

        

        System.out.println("It's communicating");

        //encoded file name uses the file.lastModified() that ensures that a modified file has a different fileId
        String file_name_to_encode = file_name + file.lastModified();

        //identifier is obtained by applying SHA256, a cryptographic hash function, to some bit string.
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash = digest.digest(file_name_to_encode.getBytes(StandardCharsets.UTF_8));


        return 0;
    }


    /**
     *
     * @param file_path
     * @throws RemoteException
     * The client shall specify the file to restore by its pathname.
     */
    @Override
    public int restore(String file_path) throws RemoteException {
        return 0;
    }

    /**
     *
     * @param file_path
     * @throws RemoteException
     * The client shall specify the file to delete by its pathname.
     */
    @Override
    public int delete(String file_path) throws RemoteException {
        return 0;
    }

    /**
     *
     * @param max_disk_space
     * The client shall specify the maximum disk space in KBytes (1KByte = 1000 bytes) that can be used for storing chunks.
     * It must be possible to specify a value of 0, thus reclaiming all disk space previously allocated to the service.
     */
    @Override
    public int manage(int max_disk_space) {
        if(max_disk_space < 0) {
            System.err.println("Invalid maximum disk space");
            System.exit(-1);
        }
        return 0;
    }

    /**
     * This operation allows to observe the service state.
     */
    @Override
    public String retrieve_state() {
        return "retrieve state successful";
    }
}
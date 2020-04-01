package project.peer;

import java.io.File;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


import project.Macros;

import project.channel.*;
import project.chunk.ChunkFactory;
import project.store.Store;

public class Peer implements RemoteInterface {
    private static final int RegistryPort = 1099;

    public static int id;

    private static String service_access_point;

    //Addresses, ports and InetAdress of each channel
    public static MulticastControlChannel MC;
    public static MulticastDataBackupChannel MDB;
    public static MulticastDataRecoveryChannel MDR;

    public Peer(String MC_address, int MC_port, String MDB_address, int MDB_port, String MDR_address, int MDR_port) throws RemoteException {
        MC = new MulticastControlChannel(MC_address, MC_port);
        MDB = new MulticastDataBackupChannel(MDB_address, MDB_port);
        MDR = new MulticastDataRecoveryChannel(MDR_address, MDR_port);
    }

    //class methods
    public static void main(String[] args){
        if(args.length != 9){
            System.out.println("Usage: [package]Peer <protocol_version> <peer_id> <service_access_point> " +
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

        } catch (Exception e) {
            System.err.println("Peer exception: " + e.toString());
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
        System.out.println(file_path);
        File file = new File(file_path);

        ChunkFactory chunkFactory = new ChunkFactory(file, replication_degree);

        Store.getStore().addFile(file.getName(), Store.createFileId(file));



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

        final String file_name = new File(file_path).getName();

        if(Store.getStore().getFiles(file_name) == null) {
            System.err.println("A file with that name wasn't found, cannot restore a file that was't been backup by this peer");
            return -1;
        }

        //Restore

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

        //a peer should remove from its backing store all chunks belonging to the specified file.
        final String file_name = new File(file_path).getName();

        //gets the file_id from the entry with key file_name form allFiles
        final String file_id = Store.getStore().getFiles(file_name);

        if (file_id == null) {
            System.err.println("File name was't find, cannot delete file that wasn't been backup");
            System.exit(-1);
        }

        //TODO delete chunks

        // Remove entry with the file_name and correspond file_id from allFiles
        Store.getStore().removeFile(file_name);


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
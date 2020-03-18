import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestApp {
    public static void main(String[] args) {
        //minimum args is 4 to retrieve the internal state of the peer
        if (args.length < 4) {
            System.err.println("usage: java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>");
            System.exit(1);
        }

        //normally 1923
        String peer_ap = args[2];
        String sub_protocol = args[3];
        //opnd_1 is either a file_path or a maximum_disk_space
        String  opnd_1 = null;
        //opnd_2 is a replication_degree always!
        int opnd_2 = 0;
        if(args.length > 4)
            opnd_1 = args[4];
        if(args.length > 5)
            opnd_2 = Integer.parseInt(args[5]);

        try {
            Registry reg = LocateRegistry.getRegistry();
            Peer peer = (Peer) reg.lookup(peer_ap);

            switch (sub_protocol) {
                case "BACKUP":
                    if (args.length != 6) {
                        System.err.println("Expected 6 arguments, given" + args.length);
                        System.err.println("Usage: java TestApp <peer_ap> BACKUP <file_path> <file_replication_degree>");
                        System.exit(-2);
                    }
                    if(opnd_2 <= 0) {
                        System.err.println("Usage: java TestApp <peer_ap> BACKUP <file_path> <file_replication_degree>");
                        System.err.println("Expected file_replication_degree to be greater than 0 but is " + opnd_2);
                        System.exit(-2);
                    }
                    //triggers the backup of file in opnd_1 with a replication degree of opnd_2
                    peer.backup(opnd_1, opnd_2);
                    break;
                case "RESTORE":
                    if (args.length != 5) {
                        System.err.println("Expected 5 arguments, given" + args.length);
                        System.err.println("Usage: java TestApp <peer_ap> RESTORE <file_path>");
                        System.exit(-3);
                    }
                    //triggers the restoration of the previously replicated file in opnd_1
                    peer.restore(opnd_1);
                    break;
                case "DELETE":
                    if (args.length != 5) {
                        System.err.println("Expected 5 arguments, given" + args.length);
                        System.err.println("Usage: java TestApp <peer_ap> DELETE <file_path>");
                        System.exit(-4);
                    }
                    //delete that file
                    peer.delete(opnd_1);
                    break;
                case "RECLAIM":
                    if (args.length != 5) {
                        System.err.println("Expected 5 arguments, given" + args.length);
                        System.err.println("Usage: java TestApp <peer_ap> RECLAIM <maximum_disk_space_in_KBytes>");
                        System.exit(-5);
                    }
                    // reclaim all the disk space being used by the service,
                    peer.manage(Integer.parseInt(opnd_1));
                    break;
                case "STATE":
                    if (args.length != 4) {
                        System.err.println("Expected 4 arguments, given" + args.length);
                        System.err.println("Usage: java TestApp <peer_ap> STATE");
                        System.exit(-6);
                    }
                    //retrieve the internal state of the peer
                    peer.retrieve_state();
                    break;
                default:
                    System.err.println("Expected sub_protocol to be BACKUP, RESTORE, DELETE, RECLAIM, STATE.");
                    System.err.println("But sub_protocol given "+ sub_protocol);
                    System.exit(-7);
            }
        } catch (AccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}

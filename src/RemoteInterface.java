import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote{
    void backup(String file_path, int replication_degree) throws RemoteException;
    void restore(String file_path) throws RemoteException;
    void delete(String file_path) throws RemoteException;
    //reclaim
}
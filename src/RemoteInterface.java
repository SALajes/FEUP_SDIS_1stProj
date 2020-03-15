import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote{
    void backup(String file_path) throws RemoteException;
    //restore
    //delete
    //reclaim
}
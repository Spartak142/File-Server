package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Client extends Remote {
    void recieveNotification(String notification)throws RemoteException;
}

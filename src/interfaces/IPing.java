package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPing extends Remote {
    String ping() throws RemoteException;
}

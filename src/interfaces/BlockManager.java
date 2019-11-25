package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BlockManager extends Remote {
    Block getBlock(Id indexId) throws RemoteException;
    Block newBlock(byte[] b) throws RemoteException;
    Id getName() throws RemoteException;
}

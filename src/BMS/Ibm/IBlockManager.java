package BMS.Ibm;

import BMS.ACK;
import BMS.MessageBlk;
import BMS.MessageCheck;
import interfaces.Block;
import interfaces.Id;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBlockManager extends Remote {

    MessageBlk readBlock(Id indexId) throws RemoteException;
    ACK writeBlock(MessageBlk message) throws RemoteException;
    ACK isBlockChanged(MessageCheck checkFromClient) throws RemoteException;
    long getBlockSize() throws RemoteException;
}

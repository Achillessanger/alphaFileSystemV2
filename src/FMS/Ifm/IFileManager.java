package FMS.Ifm;

import FMS.FMACK;
import FMS.Message;
import interfaces.Id;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IFileManager extends Remote {
    Message getFileMeta(Id fileId) throws RemoteException;
    FMACK updateFileMeta(Message newMeta, Id fileId) throws RemoteException;
    Message newFileMeta(Id fileId) throws RemoteException;
}

package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IHello extends Remote {
    String hello(String name) throws RemoteException;
    int divide(int a, int b) throws RemoteException;
}

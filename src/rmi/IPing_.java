package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPing_ extends Remote {
    String ping() throws RemoteException;
}

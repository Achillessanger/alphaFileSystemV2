package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PingImpl extends UnicastRemoteObject implements IPing_ {

    public PingImpl() throws RemoteException {
        super();
    }

    @Override
    public String ping() {
        return "pong";
    }
}

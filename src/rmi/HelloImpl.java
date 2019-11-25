package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class HelloImpl extends UnicastRemoteObject implements IHello {

    String id;

    public HelloImpl(String id) throws RemoteException {
        super();
        this.id = id;
    }

    @Override
    public String hello(String name) {
        return String.format("Hello %s, I am %s!", name, this.id);
    }

    @Override
    public int divide(int a, int b) {
        if (b == 0) {

        }
        return a / b;
    }

}

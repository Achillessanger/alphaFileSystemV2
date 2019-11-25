package rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class Server {

    public static void main(String[] args) throws MalformedURLException, RemoteException {
        HelloImpl[] hellos = new HelloImpl[]{
                new HelloImpl("Alice"),
                new HelloImpl("Bob"),
                new HelloImpl("Candy")
        };

        for (HelloImpl hello : hellos) {
            Naming.rebind("rmi://localhost:10000/" + hello.id, hello);
        }

        System.out.println("done");
    }

}

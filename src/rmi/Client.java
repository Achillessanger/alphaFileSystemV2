package rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
    public static void main(String[] args) {

        try {
            IHello[] hellos = new IHello[]{
                    (IHello) Naming.lookup("rmi://localhost:10000/Alice"),
                    (IHello) Naming.lookup("rmi://localhost:10000/Bob"),
                    (IHello) Naming.lookup("rmi://localhost:10000/Candy")
            };
            for (IHello h : hellos) {
                System.out.println(h.hello("Wayne"));
            }

            try {
                IHello h = hellos[0];
                System.out.println("===");
                System.out.println(h.divide(5, 2));
                System.out.println("---");
                System.out.println(h.divide(5, 0));
                System.out.println("===");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (NotBoundException | RemoteException | MalformedURLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Done!  :)");

    }
}

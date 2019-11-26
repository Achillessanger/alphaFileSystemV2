package FMS_START;

import FMS.Ifm.IFileManager;
import FMS.MyFileManagerServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;

public class FMS2 {
    public static void main(String[] args) throws RemoteException, MalformedURLException {
        int port = 10065;
        System.out.println("Starting fm-02 server...");

        try {
            LocateRegistry.createRegistry(port);
        }catch (ExportException e){

        }

        String arg = "fm-02";
        IFileManager fm_s = new MyFileManagerServer("./path/to/"+arg+"/");
        Naming.rebind("rmi://localhost:"+port+"/"+arg,fm_s);

        System.out.println("Done.");
    }
}

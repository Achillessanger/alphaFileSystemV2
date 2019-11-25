import BMS.Ibm.IBlockManager;
import BMS.MyBlockManagerServer;
import FMS.Ifm.IFileManager;
import FMS.MyFileManagerServer;
import Impl.StringId;

import java.net.BindException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;

public class AllFMServer {
    public static void main(String[] args) throws RemoteException, MalformedURLException {
        int port = 10065;
        System.out.println("Starting all fm servers together...");

        try {
            LocateRegistry.createRegistry(port);
        }catch (ExportException e){

        }

        for(String arg : args){
            IFileManager fm_s = new MyFileManagerServer("./path/to/"+arg+"/");
            Naming.rebind("rmi://localhost:"+port+"/"+arg,fm_s);
        }
        System.out.println("Done.");
    }

}

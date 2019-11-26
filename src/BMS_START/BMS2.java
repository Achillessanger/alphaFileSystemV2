package BMS_START;

import BMS.Ibm.IBlockManager;
import BMS.MyBlockManagerServer;
import Impl.StringId;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;

public class BMS2 {
    public static void main(String[] args) throws RemoteException, MalformedURLException {
        int port = 10065;
        System.out.println("Starting bm-02 server ...");

        try {
            LocateRegistry.createRegistry(port);
        }catch (ExportException e){

        }
        String arg = "bm-02";
        IBlockManager bm_s = new MyBlockManagerServer("./path/to/"+arg+"/",new StringId(arg));
        Naming.rebind("rmi://localhost:"+port+"/"+arg,bm_s);

        System.out.println("Done.");

    }
}

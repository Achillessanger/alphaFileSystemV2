import BMS.Ibm.IBlockManager;
import BMS.MyBlockManagerServer;
import Impl.StringId;
import sun.jvm.hotspot.debugger.cdbg.Sym;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;

public class AllBMServer {
    public static void main(String[] args) throws RemoteException, MalformedURLException{
        int port = 10065;
        System.out.println("Starting all bm servers together...");

        try {
            LocateRegistry.createRegistry(port);
        }catch (ExportException e){

        }
        for(String arg : args){
            IBlockManager bm_s = new MyBlockManagerServer("./path/to/"+arg+"/",new StringId(arg));
            Naming.rebind("rmi://localhost:"+port+"/"+arg,bm_s);
        }
        System.out.println("Done.");

    }
}

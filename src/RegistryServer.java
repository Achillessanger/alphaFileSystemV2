import BMS.Ibm.IBlockManager;
import BMS.MyBlockManagerServer;
import FMS.Ifm.IFileManager;
import FMS.MyFileManagerServer;
import Impl.StringId;


import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RegistryServer {
    public static void main(String[] args) throws RemoteException, MalformedURLException{
        System.out.println("registry starting ...");
        int port = 10065;
        LocateRegistry.createRegistry(port);
        System.out.println("2");

        IBlockManager bm1_s = new MyBlockManagerServer("./path/to/bm-01/",new StringId("bm-01"));
        System.out.println("3");
        IBlockManager bm2_s = new MyBlockManagerServer("./path/to/bm-02/",new StringId("bm-02"));
        IBlockManager bm3_s = new MyBlockManagerServer("./path/to/bm-03/",new StringId("bm-03"));
        System.out.println("4");
        Naming.rebind("rmi://localhost:"+port+"/bm-01",bm1_s);
        System.out.println("5");
        Naming.rebind("rmi://localhost:"+port+"/bm-02",bm2_s);
        System.out.println("6");
        Naming.rebind("rmi://localhost:"+port+"/bm-03",bm3_s);
        System.out.println("7");

        IFileManager fm1_s = new MyFileManagerServer("./path/to/fm-01/");
        System.out.println("8");
        IFileManager fm2_s = new MyFileManagerServer("./path/to/fm-02/");
        System.out.println("9");
        Naming.rebind("rmi://localhost:"+port+"/fm-01",fm1_s);
        System.out.println("10");
        Naming.rebind("rmi://localhost:"+port+"/fm-02",fm2_s);
        System.out.println("11");
        System.out.println("registry started ...");
    }
}

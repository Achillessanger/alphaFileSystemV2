import BMS.Ibm.IBlockManager;
import BMS.MyBlockManagerServer;
import Impl.StringId;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class BMServerStart {
    public static void main(String[] args)throws RemoteException, MalformedURLException {
        int port = 10065;
//        IBlockManager bm1_s = new MyBlockManagerServer("./path/to/bm-01/",new StringId("bm-01"));
//        IBlockManager bm2_s = new MyBlockManagerServer("./path/to/bm-02/",new StringId("bm-02"));
//        IBlockManager bm3_s = new MyBlockManagerServer("./path/to/bm-03/",new StringId("bm-03"));
//        Naming.rebind("rmi://localhost:"+port+"/bm-01",bm1_s);
//        Naming.rebind("rmi://localhost:"+port+"/bm-02",bm2_s);
//        Naming.rebind("rmi://localhost:"+port+"/bm-03",bm3_s);
        System.out.println("BM servers started ...");
    }
}

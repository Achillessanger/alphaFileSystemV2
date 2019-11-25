import FMS.Ifm.IFileManager;
import FMS.MyFileManagerServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class FMServerStart {
    public static void main(String[] args)throws RemoteException, MalformedURLException{
        int port = 10065;
//        IFileManager fm1_s = new MyFileManagerServer("./path/to/fm-01/");
//        IFileManager fm2_s = new MyFileManagerServer("./path/to/fm-02/");
//        Naming.rebind("rmi://localhost:"+port+"/fm-01",fm1_s);
//        Naming.rebind("rmi://localhost:"+port+"/fm-02",fm2_s);
        System.out.println("FM servers started ...");
    }
}

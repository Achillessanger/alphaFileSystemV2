import FMC.MyFileManagerClient;
import FMS.Ifm.IFileManager;
import FMS.MyFileManagerServer;
import Impl.StringId;
import Impl.mContext;
import interfaces.File;

import java.rmi.RemoteException;

public class main {
    public static void main(String[] args) throws RemoteException {
        MyFileManagerClient fm_client = mContext.myFileManagerClientMap.get(new StringId("fm-01"));
        fm_client.reConnect();
        if(!fm_client.isConnect()){
            System.out.println("fm server not connect");
        }else {
            System.out.println("fm server connecting ...");
        }
        StringId sid = new StringId("test");
//        IFileManager fm1_s = new MyFileManagerServer("./path/to/fm-01/");
//        fm1_s.newFileMeta(sid);
        File file = fm_client.newFile(sid);
        file.write("helloworld".getBytes());
        int debug = 0;
    }
}

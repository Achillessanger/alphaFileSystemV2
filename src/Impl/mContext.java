package Impl;

import BMC.MyBlockManagerClient;
import FMC.MyFileManagerClient;
import interfaces.Id;

import java.util.HashMap;
import java.util.Map;

public class mContext {
    public static Map<Id, Id> fileEmpytMap;
//    public static final long BLOCK_SIZE = 512;
    public static Map<StringId, MyBlockManagerClient> myBlockManagerClientMap;
    public static Map<StringId, MyFileManagerClient> myFileManagerClientMap;
    static {
//        myBlockManagerClientMap = new HashMap<>();
//        MyBlockManagerClient bmc1 = new MyBlockManagerClient("./path/to/bm-01/",new StringId("bm-01"));
//        MyBlockManagerClient bmc2 = new MyBlockManagerClient("./path/to/bm-01/",new StringId("bm-02"));
//        MyBlockManagerClient bmc3 = new MyBlockManagerClient("./path/to/bm-01/",new StringId("bm-03"));
//
//        myBlockManagerClientMap.put(new StringId("bm-01"),bmc1);
//        myBlockManagerClientMap.put(new StringId("bm-02"),bmc2);
//        myBlockManagerClientMap.put(new StringId("bm-03"),bmc3);

        myFileManagerClientMap = new HashMap<>();
        MyFileManagerClient fmc1 = new MyFileManagerClient(new StringId("fm-01"));
//        MyFileManagerClient fmc2 = new MyFileManagerClient(new StringId("fm-02"));

        myFileManagerClientMap.put(new StringId("fm-01"),fmc1);
//        myFileManagerClientMap.put(new StringId("fm-02"),fmc2);


        fileEmpytMap = new HashMap<>();
        fileEmpytMap.put(new StringId("FILE_EMPTY"),new StringId("FILE_EMPTY"));
    }
}

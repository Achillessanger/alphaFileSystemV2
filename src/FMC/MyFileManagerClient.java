package FMC;

import FMS.FMACK;
import FMS.Ifm.IFileManager;
import FMS.Message;
import Impl.ErrorCode;
import Impl.StringId;
import Impl.mContext;
import interfaces.File;
import interfaces.FileManager;
import interfaces.IPing;
import interfaces.Id;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;

public class MyFileManagerClient implements FileManager {
    private static int port = 10065;
    private String name;
    private IFileManager fm_server;
    Message retMessage = null;


    public MyFileManagerClient(Id sid){
        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "500");
        System.setProperty("sun.rmi.transport.tcp.readTimeout", "500");
        System.setProperty("sun.rmi.transport.connectionTimeout", "500");
        System.setProperty("sun.rmi.transport.proxy.connectTimeout", "500");
        System.setProperty("sun.rmi.transport.tcp.handshakeTimeout", "500");
        name = "";
        if(sid instanceof StringId)
            name = ((StringId) sid).getId();

        try {
            fm_server = (IFileManager)Naming.lookup("rmi://localhost:" + port + "/" + name);
        }catch (NotBoundException | RemoteException | MalformedURLException e){
            fm_server = null;
//            throw new ErrorCode(ErrorCode.CANNOT_CONNECT_TO_FMSERVER);
        }catch (ErrorCode errorCode){
            fm_server = null;
//            throw errorCode;
        }
    }

    public int reConnect(){
        System.out.println("trying to reconnect " + name + " server...");
        try {
            fm_server = (IFileManager)Naming.lookup("rmi://localhost:" + port + "/" + name);
        }catch (NotBoundException | RemoteException | MalformedURLException e){
            System.out.println("cannot connect to "+ name + " server...");
            fm_server = null;
            return 0;
        }catch (ErrorCode errorCode){
            System.out.println("cannot connect to "+ name + " server...");
            fm_server = null;
            return 0;
        }
        System.out.println("connected to "+ name + " server...");
        return 1;
    }
    public boolean isConnect(){
        if(fm_server == null)
            return false;
        else
            return true;
    }

    @Override
    public File getFile(Id fileId){
        if(checkConnect() == 0)
            throw new ErrorCode(ErrorCode.CANNOT_CONNECT_TO_FMSERVER);

        retMessage = null;

        try {
            retMessage = fm_server.getFileMeta(fileId);
        }catch (RemoteException e){
            throw new ErrorCode(ErrorCode.FM_SERVER_CONNECT_FAIL);
        }

        //server端抛出了一个异常
        if(retMessage.getIsValid() == 0)
            throw new ErrorCode(retMessage.getErrorCode());
        //server端没有抛出异常
        File myFile = new MyFile(fileId,this,name,retMessage.getFileSize(),retMessage.getBlockSize(),retMessage.getLogicBlockList());
        return myFile;
    }


    @Override
    public File newFile(Id fileId){
        if(checkConnect() == 0)
            throw new ErrorCode(ErrorCode.CANNOT_CONNECT_TO_FMSERVER);

        retMessage = null;

        try {
            retMessage = fm_server.newFileMeta(fileId);
        }catch (RemoteException e){
            throw new ErrorCode(ErrorCode.FM_SERVER_CONNECT_FAIL);
        }

        if(retMessage.getIsValid() == 0)
            throw new ErrorCode(retMessage.getErrorCode());
        ArrayList<Map<Id, Id>> LogicBlockList_new = new ArrayList<>();
        LogicBlockList_new.add(mContext.fileEmpytMap);
        File myFile = new MyFile(fileId,this,name,0,retMessage.getBlockSize(),LogicBlockList_new);
        return myFile;
    }

    public void updateInode(File newfile){
        if(checkConnect() == 0)
            throw new ErrorCode(ErrorCode.CANNOT_CONNECT_TO_FMSERVER);

        FMACK fmack;
        Message message = new Message(((MyFile) newfile).getLogicBlockList(),1,((MyFile) newfile).getFileSize(),((MyFile) newfile).getBlockSize());
        try {
            fmack = fm_server.updateFileMeta(message,newfile.getFileId());
        }catch (RemoteException e){
            throw new ErrorCode(ErrorCode.FM_SERVER_CONNECT_FAIL);
        }
        if(fmack.getIsValid() == 0){
            throw new ErrorCode(retMessage.getErrorCode());
        }
    }

    public int checkConnect(){
        try {
            if(fm_server != null)
                ((IPing)fm_server).ping();
            else
                return reConnect();
        }catch (RemoteException e){
            fm_server = null;
            return reConnect();
        }
        return 1;
    }
}

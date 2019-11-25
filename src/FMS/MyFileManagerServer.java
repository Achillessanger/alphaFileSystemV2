package FMS;

import FMS.Ifm.IFileManager;
import FMC.MyFile;
import Impl.ErrorCode;
import Impl.StringId;
import Impl.mContext;
import interfaces.File;
import interfaces.Id;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyFileManagerServer extends UnicastRemoteObject implements IFileManager {
    private final long BLOCK_SIZE = 512;
    private String path;
    private BufferFMS fmBuffer;

    public String getPath() {
        return path;
    }
    public MyFileManagerServer(String path) throws RemoteException {
        super();
        this.path = path;
        fmBuffer = new BufferFMS();
    }

    @Override
    public Message getFileMeta(Id fileId) throws RemoteException {
        long fileNameHash = fmBuffer.inodeHash(fileId);
        int bufFMIndex = (int)fileNameHash % BufferFMS.FM_BUFFER_LINES;

        //在inode的buffer里找
        for(int i = 0; i < fmBuffer.getInode_cache().get(bufFMIndex).size(); i++){
            BufferInode inode = fmBuffer.getInode_cache().get(bufFMIndex).get(i);
            if(inode.equals(fileId))
                return new Message(inode.getInode_data(),1,inode.getFileSize(),inode.getBlockSize());
        }
        //inode的buffer里没找到，从磁盘读取
        BufferInode newInode = fmBuffer.findFreeInode();
        fmBuffer.deleteFromCache(newInode);
        fmBuffer.change_place_on_free_list(newInode);

        StringId sid = (StringId)fileId;
        String id = sid.getId();
        String filePath = path + id + ".meta";
        java.io.File file = new java.io.File(filePath);
        if(!file.exists()){
            return new Message(0,ErrorCode.NO_SUCH_FILE);
        }else {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String tmp;
                tmp = br.readLine();
                long fileSize = Long.parseLong(tmp.split(":")[1]);
                tmp = br.readLine();
                long blockSize = Long.parseLong(tmp.split(":")[1]);
                br.readLine();
                tmp = br.readLine();
                ArrayList<Map<Id,Id>> LogicBlockList = new ArrayList<>();

                while (tmp != null && !"".equals(tmp)){
                    boolean debug = "".equals(tmp);
                    Map<Id,Id> map = new HashMap<>();
                    if(tmp.split(":").length == 1){
                        map = mContext.fileEmpytMap;
                    }else {
                        tmp = tmp.split(":")[1];

                        for(String s : tmp.split(" ")){
                            String s1 = s.split(",")[0];
                            String s2 = s.split(",")[1];
                            map.put(new StringId(s1.substring(s1.indexOf("\"")+1,s1.lastIndexOf("\""))),new StringId(s2.substring(0,s2.indexOf("]"))));
                        }
                    }
                    LogicBlockList.add(map);
                    tmp = br.readLine();
                }

                //读入buffer
                newInode.setInodesId(sid);
                newInode.setInode_data(LogicBlockList);
                newInode.setFileSize(fileSize);
                newInode.setBlockSize(blockSize);

                return new Message(LogicBlockList,1,fileSize,blockSize);

            }catch (IOException e){
                return new Message(0,ErrorCode.IO_EXCEPTION);
            }
        }
    }

    @Override
    public FMACK updateFileMeta(Message newMeta, Id fileId) throws RemoteException {
        ArrayList<Map<Id,Id>> LogicBlockList = newMeta.getLogicBlockList();
        long fileSize = newMeta.getFileSize();
        long blockSize = newMeta.getBlockSize();

        long fileNameHash = fmBuffer.inodeHash(fileId);
        int bufFMIndex = (int)fileNameHash % BufferFMS.FM_BUFFER_LINES;

        BufferInode inode = fmBuffer.findFreeInode();

        //在inode的buffer里找
        for(int i = 0; i < fmBuffer.getInode_cache().get(bufFMIndex).size(); i++){
            BufferInode bufferInode = fmBuffer.getInode_cache().get(bufFMIndex).get(i);
            if(bufferInode.equals(fileId)) {
                inode = bufferInode;
                break;
            }
        }
        fmBuffer.change_place_on_free_list(inode);
        //TODO 记录事务
        ArrayList<Map<Id,Id>> LogicBlockList_record = inode.getInode_data();
        StringId inodesId_record = inode.getInodesId();
        long fileSize_record = inode.getFileSize();
        long blockSize_record = inode.getBlockSize();

        inode.setInode_data(LogicBlockList);
        inode.setBlockSize(blockSize);
        inode.setFileSize(fileSize);
        inode.setInodesId((StringId) fileId);

        String file_meta_path = path + ((StringId)fileId).getId() + ".meta";
        java.io.File file_meta_file = new java.io.File(file_meta_path);
        file_meta_file.delete();
        try {
            file_meta_file.createNewFile();
            RandomAccessFile raf = new RandomAccessFile(file_meta_file,"rw");
            String str = "size:"+fileSize+"\nblock size:"+blockSize+"\nlogic block:\n";
            for(int k = 0; k < LogicBlockList.size(); k++){
                str += k+":";
                if(LogicBlockList.get(k).containsKey(new StringId("FILE_EMPTY"))){
                    //TODO 检查一下这个条件
                }else {
                    for(Map.Entry<Id,Id> entry : LogicBlockList.get(k).entrySet()){
                        str += "[\""+((StringId)entry.getKey()).getId()+"\","+((StringId)entry.getValue()).getId()+"] ";
                    }
                }
                str += "\n";
            }
            raf.seek(0);
            raf.write(str.getBytes());
            raf.close();
        }catch (IOException e){
            //这个情况感觉……救不了啊
            inode.setInode_data(LogicBlockList_record);
            inode.setInodesId(inodesId_record);
            inode.setFileSize(fileSize_record);
            inode.setBlockSize(blockSize_record);
            return new FMACK(ErrorCode.IO_EXCEPTION);
        }
        return new FMACK();
    }

    @Override
    public Message newFileMeta(Id fileId) throws RemoteException {
        String filePath = path + ((StringId)fileId).getId() + ".meta";
        java.io.File newFile = new java.io.File(filePath);
        if(newFile.exists())
            throw new ErrorCode(ErrorCode.FILE_ALREADY_EXISTED);

        BufferInode newInode = fmBuffer.findFreeInode();
        fmBuffer.deleteFromCache(newInode);
        fmBuffer.change_place_on_free_list(newInode);

        //记录inode之前存的内容
        ArrayList<Map<Id,Id>> LogicBlockList_record = newInode.getInode_data();
        StringId inodesId_record = newInode.getInodesId();
        long fileSize_record = newInode.getFileSize();
        long blockSize_record = newInode.getBlockSize();

        //写入buffer
        newInode.setInode_data(new ArrayList<>());
        newInode.setInodesId((StringId)fileId);
        newInode.setFileSize(0);
        newInode.setBlockSize(BLOCK_SIZE);

        try{
            //写入文件
            newFile.createNewFile();
            RandomAccessFile raf = new RandomAccessFile(newFile,"rw");
            String str = "size:0\nblock size:"+BLOCK_SIZE+"\nlogic block:\n0:\n";
            raf.seek(0);
            raf.write(str.getBytes());
            raf.close();
        }catch (IOException e){
            newInode.setInode_data(LogicBlockList_record);
            newInode.setInodesId(inodesId_record);
            newInode.setFileSize(fileSize_record);
            newInode.setBlockSize(blockSize_record);

            return new Message(0,ErrorCode.IO_EXCEPTION);
        }
        return new Message(newInode.getInode_data(),1,0,newInode.getBlockSize());
    }
}

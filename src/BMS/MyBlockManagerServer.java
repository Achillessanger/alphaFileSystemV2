package BMS;

import BMS.Ibm.IBlockManager;
import Impl.ErrorCode;
import Impl.MD5Util;
import Impl.StringId;
import Impl.mContext;
import interfaces.IPing;
import interfaces.Id;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class MyBlockManagerServer extends UnicastRemoteObject implements IBlockManager, IPing {
    private final long BLOCK_SIZE = 512;
    private String path;
    private String name;
    private StringId BM_sid;
    Buffer buffer;


    public MyBlockManagerServer(String path, Id id) throws RemoteException{
        super();
        this.path = path;
        this.BM_sid = (StringId) id;
        this.name = ((StringId)id).getId();
        buffer = new Buffer();
    }
    @Override
    public MessageBlk readBlock(Id indexId) throws RemoteException { //OK
        int blkId = Integer.parseInt(((StringId)indexId).getId());
        int bufIndex = blkId %(Buffer.BUFFER_LINES);

        //在cache里找
        for(int i = 0; i < buffer.getCache().get(bufIndex).size(); i++){
            //！注意这里没有讨论块忙的情况!
            BufferBlk blk = buffer.getCache().get(bufIndex).get(i);
            if(Integer.parseInt(blk.getBufBlkId().getId()) == blkId
                    && !blk.isBusy()){
                return new MessageBlk(blk.getData(),1,blk.getBufBlkId());
            }
        }

        //从磁盘上读
        //找新缓冲区
        BufferBlk newBlk = buffer.findFreeBlk();

        if(newBlk == null){
            //空闲缓冲区已空,暂时认为不可能
            return new MessageBlk(0, ErrorCode.OPEN_TOO_MANY_FILES);

        }else {

            buffer.deleteFromCache(newBlk);
            buffer.makeBusy(newBlk,bufIndex);

            String dataPath = path + blkId + ".data";
            String metaPath = path + blkId + ".meta";

            java.io.File dataFile = new java.io.File(dataPath);
            java.io.File metaFile = new java.io.File(metaPath);
            if(!dataFile.exists() || !metaFile.exists()){
                buffer.makeFree(newBlk);
                return new MessageBlk(0,ErrorCode.NO_SUCH_BLOCK);
            }
            byte[] data;
            String checkSum;
            try {
                RandomAccessFile raf = new RandomAccessFile(metaFile,"r");
                String tmp = raf.readLine();
                long blockSize = Long.parseLong(tmp.split(":")[1]);
                tmp = raf.readLine();
                checkSum = tmp.split(":")[1];

                data = new byte[(int)blockSize];
                RandomAccessFile raf_data = new RandomAccessFile(dataFile,"r");
                raf_data.read(data);
                raf.close();
                raf_data.close();

            }catch (IOException e){
                buffer.makeFree(newBlk);
                return new MessageBlk(0,ErrorCode.IO_EXCEPTION);
            }
            //检查磁盘上读的block数据是否正确
            boolean check;
            try{
                check = MD5Util.checkPassword(data,checkSum);
            }catch (ErrorCode errorCode){
                buffer.makeFree(newBlk);
                return new MessageBlk(0,errorCode.getErrorCode());
            }
            if(check) {
                //写入buffer
                newBlk.setData(data);
                newBlk.setBufBlkId((StringId)indexId);
                buffer.makeFree(newBlk);
                return new MessageBlk(data,1,(StringId)indexId);
            } else {
                buffer.makeFree(newBlk);
                return new MessageBlk(0,ErrorCode.CHECKSUM_CHECK_FAILED);
            }
        }

    }

    @Override
    public ACK writeBlock(MessageBlk message) throws RemoteException { //OK
//        try {
//            Thread.sleep(10000);
//        }catch (InterruptedException e){
//
//        }

        byte[] b = message.getData();
        String idPath = path + "id.count";
        java.io.File file = new java.io.File(idPath);
        if(!file.exists()){
            return new ACK(0,ErrorCode.INITFILE_ERROR);
        }
        try {
            RandomAccessFile raf_id = new RandomAccessFile(file,"rw");
            String str = raf_id.readLine();
            long newIndex = Long.parseLong(str);
            int bufIndex = (int) newIndex % buffer.BUFFER_LINES;

            BufferBlk newBlk = buffer.findFreeBlk();
            if (newBlk == null) {
                //空闲缓冲区已空,暂时认为不可能
                return new ACK(0,ErrorCode.OPEN_TOO_MANY_FILES);
            } else {
                //写入磁盘
                String dataPath = path + newIndex + ".data";
                String metaPath = path + newIndex + ".meta";
                java.io.File dataFile = new java.io.File(dataPath);
                java.io.File metaFile = new java.io.File(metaPath);
                if(dataFile.exists() || metaFile.exists())
                    return new ACK(0,ErrorCode.INITFILE_ERROR);

                dataFile.createNewFile();
                metaFile.createNewFile();

                RandomAccessFile raf_data = new RandomAccessFile(dataFile,"rw");
                raf_data.seek(0);
                raf_data.write(b);
                raf_data.close();

                RandomAccessFile raf_meta = new RandomAccessFile(metaFile,"rw");
                String md5 = MD5Util.getMD5String(b);
                String str_meta = "size:" + b.length + "\nchecksum:" + md5 + "\n";
                raf_meta.seek(0);
                raf_meta.write(str_meta.getBytes());
                raf_meta.close();

                long futureIndex = newIndex + 1;
                raf_id.seek(0);
                raf_id.write((futureIndex+"").getBytes());
                raf_id.close();

                /*已经写入磁盘成功
                  准备存入服务器端buffer，这个顺序是为了一致性*/
                buffer.deleteFromCache(newBlk);
                buffer.makeBusy(newBlk, bufIndex);
                newBlk.setData(b);
                StringId sid = new StringId(newIndex + "");
                newBlk.setBufBlkId(sid);
                buffer.makeFree(newBlk);

                //服务器端写入缓冲成功，发送ACK,告知客户端新分配的块号
                return new ACK(1,sid);
            }

        }catch (IOException e){
            return new ACK(0,ErrorCode.IO_EXCEPTION);
        }
    }

    @Override
    public String ping() throws RemoteException {
        System.out.println("log:From BM server("+ name +"):pong");
        return null;
    }
}

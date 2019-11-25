package BMS;

import BMS.Ibm.IBlockManager;
import Impl.ErrorCode;
import Impl.MD5Util;
import Impl.StringId;
import Impl.mContext;
import interfaces.Id;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class MyBlockManagerServer extends UnicastRemoteObject implements IBlockManager {
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
    public MessageBlk readBlock(Id indexId) throws RemoteException {
        int blkId = Integer.parseInt(((StringId)indexId).getId());
        int bufIndex = blkId %(Buffer.BUFFER_LINES);

        //在cache里找
        for(int i = 0; i < buffer.getCache().get(bufIndex).size(); i++){
            //！注意这里没有讨论块忙的情况!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            BufferBlk blk = buffer.getCache().get(bufIndex).get(i);
            if(Integer.parseInt(blk.getBufBlkId().getId()) == blkId
                    && !blk.isBusy()){
                return new MessageBlk(blk.getData(),1,BM_sid,blk.getBufBlkId());
            }
        }

        //从磁盘上读
        //找新缓冲区
        BufferBlk newBlk = buffer.findFreeBlk();
        if(buffer.getDelyBufBlks().size() != 0 && newBlk == null){
            try {
                delayWrite();
            }catch (ErrorCode errorCode){
                return new MessageBlk(0,errorCode.getErrorCode(),BM_sid);
            }

            newBlk = buffer.findFreeBlk();
        }

        if(newBlk == null){
            //空闲缓冲区已空,暂时认为不可能
            return new MessageBlk(0, ErrorCode.OPEN_TOO_MANY_FILES,BM_sid);

        }else {
            try {
                delayWrite();
            }catch (ErrorCode errorCode){
                return new MessageBlk(0,errorCode.getErrorCode(),BM_sid);
            }

            buffer.deleteFromCache(newBlk);
            buffer.makeBusy(newBlk,bufIndex);

            String dataPath = path + blkId + ".data";
            String metaPath = path + blkId + ".meta";

            java.io.File dataFile = new java.io.File(dataPath);
            java.io.File metaFile = new java.io.File(metaPath);
            if(!dataFile.exists() || !metaFile.exists()){
                buffer.makeFree(newBlk);
                return new MessageBlk(0,ErrorCode.NO_SUCH_BLOCK,BM_sid);
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
                return new MessageBlk(0,ErrorCode.IO_EXCEPTION,BM_sid);
            }
            //检查磁盘上读的block数据是否正确
            boolean check;
            try{
                check = MD5Util.checkPassword(data,checkSum);
            }catch (ErrorCode errorCode){
                buffer.makeFree(newBlk);
                return new MessageBlk(0,errorCode.getErrorCode(),BM_sid);
            }
            if(check) {
                newBlk.setData(data);
                newBlk.setBufBlkId((StringId)indexId);
                buffer.makeFree(newBlk);
                return new MessageBlk(data,1,BM_sid,(StringId)indexId);
            } else {
                buffer.makeFree(newBlk);
                return new MessageBlk(0,ErrorCode.CHECKSUM_CHECK_FAILED,BM_sid);
            }
        }

    }

    @Override
    public ACK writeBlock(MessageBlk message) throws RemoteException {
        byte[] b = message.getData();
        long newIndex = Long.parseLong(message.getBlk_sid().getId());

        int bufIndex = (int) newIndex % (buffer.BUFFER_LINES);
        BufferBlk newBlk = buffer.findFreeBlk();

        if(buffer.getDelyBufBlks().size() != 0 && newBlk == null){
            try {
                delayWrite();
            }catch (ErrorCode errorCode){
                return new ACK(0,errorCode.getErrorCode());
            }
            newBlk = buffer.findFreeBlk();
        }
        if (newBlk == null) {
            //空闲缓冲区已空,暂时认为不可能
            return new ACK(0,ErrorCode.OPEN_TOO_MANY_FILES);
        } else {
            try {
                delayWrite();
            }catch (ErrorCode errorCode){
                return new ACK(0,errorCode.getErrorCode());
            }
            buffer.deleteFromCache(newBlk);
            newBlk.setData(b);
            newBlk.setDelay(true);
            StringId sid = new StringId(newIndex + "");
            newBlk.setBufBlkId(sid);
            buffer.makeBusy(newBlk, bufIndex);
            buffer.makeFree(newBlk);


            //服务器端写入缓冲成功，发送ACK
            return new ACK(1,sid);
        }

    }

    @Override
    public ACK isBlockChanged(MessageCheck checkFromClient) throws RemoteException {
        StringId check_blk_Id = checkFromClient.getBlkId();
        MessageBlk readResult = readBlock(check_blk_Id);
        if(readResult.getIsValid() == 0){
            return new ACK(0,readResult.getErrorCode());
        }else {
            boolean check = MD5Util.checkPassword(readResult.getData(),checkFromClient.getChecksum());
            if(check)
                return new ACK(1,check_blk_Id);
            else
                return new ACK(0,ErrorCode.CONTENT_CHANGED_ON_SERVER);
        }
    }

    @Override
    public long getBlockSize() throws RemoteException {
        return BLOCK_SIZE;
    }


    private void delayWrite(){
        ArrayList<BufferBlk> delyBufBlks = buffer.getDelyBufBlks();
        if(delyBufBlks.size() == 0)
            return;

        try {
            for(BufferBlk blk : delyBufBlks){
                writeBlk2file(blk);
            }
        }catch (ErrorCode errorCode){
            throw errorCode;
        }
    }
    public void writeBlk2file(BufferBlk blk){
        String dataPath = path + blk.getBufBlkId().getId() + ".data";
        String metaPath = path + blk.getBufBlkId().getId() + ".meta";
        java.io.File dataFile = new java.io.File(dataPath);
        java.io.File metaFile = new java.io.File(metaPath);


        try {
            dataFile.createNewFile();
            metaFile.createNewFile();
            int bufIndex = Integer.parseInt(blk.getBufBlkId().getId()) % (buffer.BUFFER_LINES);
            buffer.makeBusy(blk, bufIndex);

            RandomAccessFile raf_data = new RandomAccessFile(dataFile,"rw");
            raf_data.write(blk.getData());
            raf_data.close();

            RandomAccessFile raf_meta = new RandomAccessFile(metaFile,"rw");
            String md5 = MD5Util.getMD5String(blk.getData());
            String str_meta = "size:" + blk.getData().length + "\nchecksum:" + md5 + "\n";
            raf_meta.write(str_meta.getBytes());
            raf_meta.close();

            //delay块write back成功，重置delay位并从list里清除
            buffer.delayWriteFin(blk);

            buffer.makeFree(blk);

        }catch (IOException e){
            throw new ErrorCode(ErrorCode.IO_EXCEPTION);
        }

    }
}

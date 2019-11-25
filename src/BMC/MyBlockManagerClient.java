package BMC;


import BMS.ACK;
import BMS.Ibm.IBlockManager;
import BMS.MessageBlk;
import BMS.MessageCheck;
import Impl.ErrorCode;
import Impl.MD5Util;
import Impl.StringId;
import interfaces.Block;
import interfaces.BlockManager;
import interfaces.Id;

import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class MyBlockManagerClient implements BlockManager {
    private final long BLOCK_SIZE = 512;
    private static int port = 10065;
    private String path;
    private StringId sid;
    private String name;
    private IBlockManager bm_server;
    private BufferBMC buffer;

    public String getPath() {
        return path;
    }
    public MyBlockManagerClient(String path, Id id){
        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "500");
        System.setProperty("sun.rmi.transport.tcp.readTimeout", "500");
        System.setProperty("sun.rmi.transport.connectionTimeout", "500");
        System.setProperty("sun.rmi.transport.proxy.connectTimeout", "500");
        System.setProperty("sun.rmi.transport.tcp.handshakeTimeout", "500");
        this.sid = (StringId)id;
        this.path = path;
        this.name = sid.getId();
        buffer = new BufferBMC();

        try {
            bm_server = (IBlockManager) Naming.lookup("rmi://localhost:" + port + "/" + name);
        }catch (NotBoundException | RemoteException | MalformedURLException e){
            bm_server = null;
//            throw new ErrorCode(ErrorCode.CANNOT_CONNECT_TO_BMSERVER);
        }catch (ErrorCode errorCode){
            bm_server = null;
            throw errorCode;
        }
    }


    public int reConnect(){
        try {
            bm_server = (IBlockManager) Naming.lookup("rmi://localhost:" + port + "/" + name);
        }catch (NotBoundException | RemoteException | MalformedURLException e){
            bm_server = null;
            return 0;
        }catch (ErrorCode errorCode){
            bm_server = null;
            return 0;
        }
        return 1;
    }

    public boolean isConnect(){
        if(bm_server == null)
            return false;
        else
            return true;
    }


    @Override
    public Id getName() {
        return sid;
    }

    @Override
    public Block getBlock(Id indexId){
        int blkId = Integer.parseInt(((StringId)indexId).getId());
        int bufIndex =blkId %(BufferBMC.BUFFER_LINES);

        BufferBlkC newblkC = buffer.findFreeBlkC();

        //在cache里找
        for(int i = 0; i < buffer.getCache().get(bufIndex).size(); i++){
            BufferBlkC blkC = buffer.getCache().get(bufIndex).get(i);
            if(Integer.parseInt(blkC.getBlk_sid().getId()) == blkId
                && !blkC.isBusy()){
                //在cache里找到了，需要验证远程的版本和cache的版本是否一致
                //客户端标过delay的块还没有写入服务端，所以不验证
                //这里只传checksum进行验证
                if(!blkC.isDelay()){
                    MessageCheck check_message = new MessageCheck((StringId) indexId, MD5Util.getMD5String(blkC.getData()));
                    ACK ackReply;
                    try {
                        ackReply = bm_server.isBlockChanged(check_message);
                    }catch (RemoteException e){
                        throw new ErrorCode(ErrorCode.BM_SERVER_ERROR_IN_CHECKING);
                    }
                    //check过程中远程io出错
                    if(ackReply.getIsValid() == 0 && ackReply.getErrorCode() != ErrorCode.CONTENT_CHANGED_ON_SERVER){
                        throw new ErrorCode(ErrorCode.BM_SERVER_ERROR_IN_CHECKING);
                    }
                    //check过程中发现远程内容被更改过
                    if(ackReply.getIsValid() == 0 && ackReply.getErrorCode() == ErrorCode.CONTENT_CHANGED_ON_SERVER){
                        newblkC = blkC;
                    }else {
                        //本地缓存与远端一致
                        return new MyBlock(indexId,this,blkC.getData(),BLOCK_SIZE);
                    }
                }else
                    return new MyBlock(indexId,this,blkC.getData(),BLOCK_SIZE);

            }
        }
        if(buffer.getDelyBufBlkCs().size() != 0 && newblkC == null){
            try{
                delayWrite();
            }catch (ErrorCode errorCode){
                throw errorCode;
            }
            newblkC = buffer.findFreeBlkC();
        }
        //从远程读block并放入client自己的缓存
        if(newblkC == null){
            throw new ErrorCode(ErrorCode.OPEN_TOO_MANY_FILES);
        }else {
            try{
                delayWrite();
            }catch (ErrorCode errorCode){
                throw errorCode;
            }
            buffer.deleteFromCache(newblkC);
            buffer.makeBusy(newblkC,bufIndex);

            //向客户端要求数据
            MessageBlk retMessage;
            try {
                retMessage = bm_server.readBlock(indexId);
            }catch (RemoteException e){
                throw new ErrorCode(ErrorCode.BM_SERVER_CONNECT_FAIL);
            }
            //远程过程抛出过异常
            if(retMessage.getIsValid() == 0)
                throw new ErrorCode(retMessage.getErrorCode());
            //远程过程正常返回
            //写入缓冲区
            newblkC.setData(retMessage.getData());
            newblkC.setBlk_sid(retMessage.getBlk_sid());
            buffer.makeFree(newblkC);

            return new MyBlock(indexId,this,retMessage.getData(),BLOCK_SIZE);
        }
    }

    @Override
    public Block newBlock(byte[] b){
        String idPath = path + "id.count";
        java.io.File file = new java.io.File(idPath);
        if(!file.exists()){
            throw new ErrorCode(ErrorCode.INITFILE_ERROR);
        }
        try {
            RandomAccessFile raf_id = new RandomAccessFile(file,"rw");
            String str = raf_id.readLine();
            long newIndex = Long.parseLong(str);
            int bufCIndex = (int) newIndex % buffer.BUFFER_LINES;
            BufferBlkC newBlkC = buffer.findFreeBlkC();

            if(buffer.getDelyBufBlkCs().size() != 0 && newBlkC == null){
                try{
                    delayWrite();
                }catch (ErrorCode errorCode){
                    throw errorCode;
                }
                newBlkC = buffer.findFreeBlkC();
            }
            if(newBlkC == null){
                throw new ErrorCode(ErrorCode.OPEN_TOO_MANY_FILES);
            }else {
                try {
                    delayWrite();
                }catch (ErrorCode errorCode){
                    throw errorCode;
                }
                buffer.deleteFromCache(newBlkC);
                newBlkC.setData(b);
                newBlkC.setDelay(true);
                StringId newBlkC_sid = new StringId(newIndex + "");
                newBlkC.setBlk_sid(newBlkC_sid);
                buffer.makeBusy(newBlkC,bufCIndex);
                buffer.makeFree(newBlkC);

                newIndex++;

                raf_id.seek(0);
                raf_id.write((newIndex+"").getBytes());
                raf_id.close();

                return new MyBlock(newBlkC_sid,this,b,BLOCK_SIZE);
            }
        } catch (IOException e){
            throw new ErrorCode(ErrorCode.IO_EXCEPTION);
        }
    }

    private void delayWrite(){
        ArrayList<BufferBlkC> delay_list = buffer.getDelyBufBlkCs();
        if(delay_list.size() == 0)
            return;
        try {
            for(BufferBlkC blkC : delay_list){
                writeBlkC2Server(blkC);
            }
        }catch (ErrorCode errorCode){
            throw errorCode;
        }
    }
    private void writeBlkC2Server(BufferBlkC blkC){
        ACK reply;
        buffer.makeBusy(blkC,Integer.parseInt(blkC.getBlk_sid().getId())%buffer.BUFFER_LINES);
        try {
            MessageBlk blkC_send_to_server = new MessageBlk(blkC.getData(),1,sid,blkC.getBlk_sid());
            reply = bm_server.writeBlock(blkC_send_to_server);
        }catch (RemoteException e){
            throw new ErrorCode(ErrorCode.BM_SERVER_CONNECT_FAIL);
        }

        //即使远程写入出错了，客户端的这一块也等于写完了，但是其实这块废了
        buffer.delayWriteFin(blkC);
        buffer.makeFree(blkC);

        //远程写入出错
        if(reply.getIsValid() == 0){
            throw new ErrorCode(reply.getErrorCode());
        }
    }

    public StringId getSid() {
        return sid;
    }
}

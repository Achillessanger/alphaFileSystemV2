package BMC;


import BMS.ACK;
import BMS.Ibm.IBlockManager;
import BMS.MessageBlk;
import BMS.MessageCheck;
import FMS.Message;
import Impl.ErrorCode;
import Impl.MD5Util;
import Impl.StringId;
import interfaces.Block;
import interfaces.BlockManager;
import interfaces.IPing;
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
//            throw errorCode;
        }
    }


    public int reConnect(){
        System.out.println("trying to reconnect " + name + " server...");
        try {
            bm_server = (IBlockManager) Naming.lookup("rmi://localhost:" + port + "/" + name);
        }catch (NotBoundException | RemoteException | MalformedURLException e){
            System.out.println("cannot connect to "+ name + " server...");
            bm_server = null;
            return 0;
        }catch (ErrorCode errorCode){
            System.out.println("cannot connect to "+ name + " server...");
            bm_server = null;
            return 0;
        }
        System.out.println("connected to "+ name + " server...");
        return 1;
    }

    public boolean isConnect(){
        if(checkConnect() == 1)
            return true;
        else
            return false;
    }


    @Override
    public Id getName() {
        return sid;
    }

    @Override
    public Block getBlock(Id indexId){//OK
        int blkId = Integer.parseInt(((StringId)indexId).getId());
        int bufIndex =blkId %(BufferBMC.BUFFER_LINES);

        BufferBlkC newblkC = buffer.findFreeBlkC();

        //在cache里找
        for(int i = 0; i < buffer.getCache().get(bufIndex).size(); i++){
            BufferBlkC blkC = buffer.getCache().get(bufIndex).get(i);
            if(Integer.parseInt(blkC.getBlk_sid().getId()) == blkId
                && !blkC.isBusy()){
                return new MyBlock(indexId,this,blkC.getData(),BLOCK_SIZE);
            }
        }

        if(checkConnect() == 0)
            throw new ErrorCode(ErrorCode.CANNOT_CONNECT_TO_BMSERVER);

        //从远程读block并放入client自己的缓存
        if(newblkC == null){
            throw new ErrorCode(ErrorCode.OPEN_TOO_MANY_FILES);
        }else {

            buffer.deleteFromCache(newblkC);
            buffer.makeBusy(newblkC,bufIndex);

            //向客户端要求数据
            MessageBlk retMessage;
            try {
                retMessage = bm_server.readBlock(indexId);
            }catch (RemoteException e){
                throw new ErrorCode(ErrorCode.BM_SERVER_CONNECT_FAIL);
            }
            //远程操作过程抛出过异常
            if(retMessage.getIsValid() == 0)
                throw new ErrorCode(retMessage.getErrorCode());
            //远程过程正常返回
            //写入client缓冲区
            newblkC.setData(retMessage.getData());
            newblkC.setBlk_sid(retMessage.getBlk_sid());
            buffer.makeFree(newblkC);

            return new MyBlock(indexId,this,retMessage.getData(),BLOCK_SIZE);
        }
    }

    @Override
    public Block newBlock(byte[] b){//OK
        if(checkConnect() == 0)
            throw new ErrorCode(ErrorCode.CANNOT_CONNECT_TO_BMSERVER);


        MessageBlk writeRequest = new MessageBlk(b,1);
        ACK reply;
        /* write through
          先向服务器发送写入请求*/
        try {
            reply = bm_server.writeBlock(writeRequest);
        }catch (RemoteException e){
            throw new ErrorCode(ErrorCode.BM_SERVER_CONNECT_FAIL);
        }
        if(reply.getIsValid() == 0)
            throw new ErrorCode(reply.getErrorCode());

        /*服务器成功将新块写入磁盘与服务器buffer
          从服务器端返回数据得到服务器端分配的新块号
          将新块写入客户端buffer*/
        long newIndex = Long.parseLong(reply.getBlk_id().getId());
        int bufCIndex = (int) newIndex % buffer.BUFFER_LINES;

        BufferBlkC newBlkC = buffer.findFreeBlkC();
        if(newBlkC == null){
            throw new ErrorCode(ErrorCode.OPEN_TOO_MANY_FILES);
        }else {
            buffer.deleteFromCache(newBlkC);
            buffer.makeBusy(newBlkC,bufCIndex);
            newBlkC.setData(b);
            newBlkC.setBlk_sid(reply.getBlk_id());
            buffer.makeFree(newBlkC);

            return new MyBlock(newBlkC.getBlk_sid(),this,b,BLOCK_SIZE);
        }

    }

    public StringId getSid() {
        return sid;
    }

    public int checkConnect(){
        try {
            if(bm_server != null)
                ((IPing)bm_server).ping();
            else
                return reConnect();
        }catch (RemoteException e){
            bm_server = null;
            return reConnect();
        }
        return 1;
    }
}

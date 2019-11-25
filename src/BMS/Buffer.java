package BMS;

import Impl.ErrorCode;
import Impl.MD5Util;
import Impl.StringId;
import Impl.mContext;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Buffer {
    public static final int BUFFER_LINES = 4;
    private ArrayList<ArrayList<BufferBlk>> cache;
    private BufferBlk freeHead;

    Buffer(){
        freeHead = new BufferBlk();
        BufferBlk tmp = freeHead;
        cache = new ArrayList<>();

        for(int i = 0; i < BUFFER_LINES; i++){
            ArrayList<BufferBlk> al = new ArrayList<>();
            cache.add(al);
            for(int j = 0; j < 4; j++){
                BufferBlk bufferBlk = new BufferBlk();
                bufferBlk.setBusy(false);
                bufferBlk.setBufBlkId(new StringId("-1"));
                bufferBlk.setPreFreeBufBlk(tmp);
                tmp.setNextFreeBufBlk(bufferBlk);
                tmp = bufferBlk;
            }
        }
    }

    public ArrayList<ArrayList<BufferBlk>> getCache() {
        return cache;
    }

    public void makeBusy(BufferBlk tmpBlk, int bufIndex){
        BufferBlk tmpPre = tmpBlk.getPreFreeBufBlk();
        BufferBlk tmpNext = tmpBlk.getNextFreeBufBlk();
        if(tmpPre != null && tmpNext != null){
            tmpPre.setNextFreeBufBlk(tmpNext);
            tmpNext.setPreFreeBufBlk(tmpPre);
        }else if(tmpPre != null && tmpNext == null){
            tmpPre.setNextFreeBufBlk(null);
        }
        if(!cache.get(bufIndex).contains(tmpBlk))
            cache.get(bufIndex).add(tmpBlk);
        tmpBlk.setBusy(true);
    }
    public void makeFree(BufferBlk tmpBlk){
        BufferBlk tmp = freeHead.getNextFreeBufBlk();
        while (tmp.getNextFreeBufBlk() != null){
            tmp = tmp.getNextFreeBufBlk();
        }
        tmpBlk.setBusy(false);
        tmp.setNextFreeBufBlk(tmpBlk);
        tmpBlk.setPreFreeBufBlk(tmp);
        tmpBlk.setNextFreeBufBlk(null);
    }
    public void deleteFromCache(BufferBlk tmpBlk){
        int blkId = Integer.parseInt((tmpBlk.getBufBlkId()).getId());
        int oldBufIndex = blkId %(BUFFER_LINES);

        if(blkId != -1){
            cache.get(oldBufIndex).remove(tmpBlk);
        }
    }
    public BufferBlk findFreeBlk(){
        BufferBlk tmpBlk = freeHead.getNextFreeBufBlk();
        return tmpBlk;
    }


}

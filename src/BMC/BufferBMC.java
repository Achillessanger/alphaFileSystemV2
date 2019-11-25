package BMC;

import Impl.StringId;

import java.util.ArrayList;

public class BufferBMC {
    public static final int BUFFER_LINES = 4;
    private ArrayList<ArrayList<BufferBlkC>> cache;
    private BufferBlkC freeHead;

    BufferBMC(){
        freeHead = new BufferBlkC();
        BufferBlkC tmp = freeHead;
        cache = new ArrayList<>();

        for(int i = 0; i < BUFFER_LINES; i++){
            ArrayList<BufferBlkC> al = new ArrayList<>();
            cache.add(al);
            for(int j = 0; j < 4; j++){
                BufferBlkC bufferBlkC = new BufferBlkC();
                bufferBlkC.setBusy(false);
                bufferBlkC.setBlk_sid(new StringId("-1"));
//                bufferBlkC.setBM_sid(new StringId("-1"));
                bufferBlkC.setPreFreeBufBlkC(tmp);
                tmp.setNextFreeBufBlkC(bufferBlkC);
                tmp = bufferBlkC;
            }
        }
    }

    public static int getBufferLines() {
        return BUFFER_LINES;
    }

    public ArrayList<ArrayList<BufferBlkC>> getCache() {
        return cache;
    }

    public BufferBlkC getFreeHead() {
        return freeHead;
    }


    public void makeBusy(BufferBlkC blkC, int bufIndex){
        BufferBlkC tmpPre = blkC.getPreFreeBufBlkC();
        BufferBlkC tmpNext = blkC.getNextFreeBufBlkC();
        if(tmpPre != null && tmpNext != null){
            tmpPre.setNextFreeBufBlkC(tmpNext);
            tmpNext.setPreFreeBufBlkC(tmpPre);
        }else if(tmpPre != null && tmpNext == null){
            tmpPre.setNextFreeBufBlkC(null);
        }
        if(!cache.get(bufIndex).contains(blkC))
            cache.get(bufIndex).add(blkC);
        blkC.setBusy(true);
    }
    public void makeFree(BufferBlkC blkC){
        BufferBlkC tmp = freeHead.getNextFreeBufBlkC();
        while (tmp.getNextFreeBufBlkC() != null){
            tmp = tmp.getNextFreeBufBlkC();
        }
        blkC.setBusy(false);
        tmp.setNextFreeBufBlkC(blkC);
        blkC.setPreFreeBufBlkC(tmp);
        blkC.setNextFreeBufBlkC(null);
    }
    public void deleteFromCache(BufferBlkC blkC){
        int blkCId = Integer.parseInt(blkC.getBlk_sid().getId());
        int oldBufCIndex = blkCId % BUFFER_LINES;
        if(blkCId != -1){
            cache.get(oldBufCIndex).remove(blkC);
        }
    }
    public BufferBlkC findFreeBlkC(){
        BufferBlkC tmpBlkC = freeHead.getNextFreeBufBlkC();
        return tmpBlkC;
    }

}

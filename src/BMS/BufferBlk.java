package BMS;

import Impl.StringId;

public class BufferBlk {
    private StringId bufBlkId;
    private byte[] data;
    private boolean isBusy;
    private boolean isDelay = false; //这个值还是要的
    private BufferBlk preFreeBufBlk;
    private BufferBlk nextFreeBufBlk;
//    private StringId fromWhichBM;

//    public StringId getFromWhichBM() {
//        return fromWhichBM;
//    }

//    public void setFromWhichBM(StringId fromWhichBM) {
//        this.fromWhichBM = fromWhichBM;
//    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public BufferBlk getPreFreeBufBlk() {
        return preFreeBufBlk;
    }

    public void setPreFreeBufBlk(BufferBlk preFreeBufBlk) {
        this.preFreeBufBlk = preFreeBufBlk;
    }

    public BufferBlk getNextFreeBufBlk() {
        return nextFreeBufBlk;
    }

    public void setNextFreeBufBlk(BufferBlk nextFreeBufBlk) {
        this.nextFreeBufBlk = nextFreeBufBlk;
    }


    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public boolean isDelay() {
        return isDelay;
    }

    public void setDelay(boolean delay) {
        isDelay = delay;
    }

    public StringId getBufBlkId() {
        return bufBlkId;
    }

    public void setBufBlkId(StringId bufBlkId) {
        this.bufBlkId = bufBlkId;
    }
}

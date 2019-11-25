package BMC;

import Impl.StringId;

public class BufferBlkC {
//    private StringId BM_sid;
    private StringId Blk_sid;
    private byte[] data;

    private boolean isBusy;
    private boolean isDelay = false;
    private BufferBlkC preFreeBufBlkC;
    private BufferBlkC nextFreeBufBlkC;

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

    public BufferBlkC getPreFreeBufBlkC() {
        return preFreeBufBlkC;
    }

    public void setPreFreeBufBlkC(BufferBlkC preFreeBufBlkC) {
        this.preFreeBufBlkC = preFreeBufBlkC;
    }

    public BufferBlkC getNextFreeBufBlkC() {
        return nextFreeBufBlkC;
    }

    public void setNextFreeBufBlkC(BufferBlkC nextFreeBufBlkC) {
        this.nextFreeBufBlkC = nextFreeBufBlkC;
    }

//    public StringId getBM_sid() {
//        return BM_sid;
//    }

//    public void setBM_sid(StringId BM_sid) {
//        this.BM_sid = BM_sid;
//    }

    public StringId getBlk_sid() {
        return Blk_sid;
    }

    public void setBlk_sid(StringId blk_sid) {
        Blk_sid = blk_sid;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}

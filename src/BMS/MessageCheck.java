package BMS;

import Impl.StringId;

import java.io.Serializable;

public class MessageCheck implements Serializable {
    private String checksum;
    private StringId blkId;
    public MessageCheck(StringId blkId, String checksum){
        this.blkId = blkId;
        this.checksum = checksum;
    }

    public String getChecksum() {
        return checksum;
    }

    public StringId getBlkId() {
        return blkId;
    }
}

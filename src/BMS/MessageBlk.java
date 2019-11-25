package BMS;

import Impl.StringId;

import java.io.Serializable;

public class MessageBlk implements Serializable {
    private byte[] data;
    private int isValid;
    private int errorCode;
    private StringId fromWhichBM;
    private StringId blk_sid;
    public MessageBlk(byte[] data, int isValid, StringId fromWhichBM, StringId blk_sid){
        this.data = data;
        this.isValid = isValid;
        this.fromWhichBM = fromWhichBM;
        this.blk_sid = blk_sid;
    }
    public MessageBlk(int isValid,int errorCode, StringId fromWhichBM){
        this.isValid = isValid;
        this.errorCode = errorCode;
        this.fromWhichBM = fromWhichBM;
    }

    public byte[] getData() {
        return data;
    }

    public int getIsValid() {
        return isValid;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public StringId getFromWhichBM() {
        return fromWhichBM;
    }

    public StringId getBlk_sid() {
        return blk_sid;
    }
}

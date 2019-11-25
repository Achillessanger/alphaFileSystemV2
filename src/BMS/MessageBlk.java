package BMS;

import Impl.StringId;

import java.io.Serializable;

public class MessageBlk implements Serializable {
    private byte[] data;
    private int isValid;
    private int errorCode;
    private StringId blk_sid;
    public MessageBlk(byte[] data, int isValid, StringId blk_sid){
        this.data = data;
        this.isValid = isValid;
        this.blk_sid = blk_sid;
    }
    public MessageBlk(int isValid,int errorCode){
        this.isValid = isValid;
        this.errorCode = errorCode;
    }
    public MessageBlk(byte[] data, int isValid){
        this.data = data;
        this.isValid = isValid;
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

    public StringId getBlk_sid() {
        return blk_sid;
    }
}

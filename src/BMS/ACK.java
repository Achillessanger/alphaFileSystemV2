package BMS;

import Impl.StringId;

import java.io.Serializable;

public class ACK implements Serializable {
    private int isValid;
    private int errorCode;
    private StringId blk_id;
    public ACK(int isValid, int errorCode){
        this.isValid = isValid;
        this.errorCode = errorCode;
    }
    public ACK(int isValid,StringId blk_id){
        this.isValid = isValid;
        this.blk_id = blk_id;
    }

    public int getIsValid() {
        return isValid;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public StringId getBlk_id() {
        return blk_id;
    }
}

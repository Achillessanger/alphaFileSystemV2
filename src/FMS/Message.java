package FMS;

import interfaces.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class Message implements Serializable {
    private ArrayList<Map<Id,Id>> LogicBlockList;
    private int isValid;
    public int errorCode;
    private long fileSize;
    private long blockSize;
    public Message(ArrayList<Map<Id,Id>> LogicBlockList, int isValid, long fileSize, long blockSize){
        this.LogicBlockList = LogicBlockList;
        this.isValid = isValid;
        this.fileSize = fileSize;
        this.blockSize = blockSize;
    }
    public Message(int isValid, int errorCode){
        this.isValid = isValid;
        this.errorCode = errorCode;
    }

    public ArrayList<Map<Id, Id>> getLogicBlockList() {
        return LogicBlockList;
    }

    public int getIsValid() {
        return isValid;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getBlockSize() {
        return blockSize;
    }
}

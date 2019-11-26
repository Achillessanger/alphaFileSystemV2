package FMC;

import BMC.MyBlockManagerClient;
import BMS.Buffer;
import BMS.BufferBlk;
import Impl.ErrorCode;
import Impl.StringId;
import Impl.mContext;
import interfaces.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyFile implements File{
    private int startNum = 0;

    private long fileSize;
    private long blockSize;
    private String fmName;
    private FileManager fileManager;
    ArrayList<Map<Id, Id>> LogicBlockList;
    private Id fileId;
    private long cursor = 0;
    public MyFile(Id id,FileManager fileManager, String fmName, long fileSize, long blockSize, ArrayList<Map<Id, Id>> list){
        this.fileId = id;
        this.fileSize = fileSize;
        this.blockSize = blockSize;
        this.LogicBlockList = list;
        this.fmName = fmName;
        this.fileManager = fileManager;

        startNum = (int)(Math.random()*3);
    }

    public Id getFileId(){
        return fileId;
    };
    public FileManager getFileManager(){
        return fileManager;
    };

    public byte[] read(int length){
        if(length < 0)
            throw new ErrorCode(ErrorCode.READ_LENGTH_ERROR);
        if(length == 0)
            return "".getBytes();
        if(length > fileSize)
            length = (int)fileSize;
        if(cursor < 0 || (cursor >= fileSize && fileSize != 0) || (fileSize == 0 && cursor != 0))
            throw new ErrorCode(ErrorCode.CURSOR_ERROR);

        long indexBegin = cursor/blockSize;
        long indexEnd = (cursor+length > fileSize)?fileSize/blockSize:(cursor+length)/blockSize ;
        byte[] retBytes = new byte[length];
        int retBytesIndex = 0;

        try{
            byte[] bytes;
            int i = (int)indexBegin;
            bytes = chooseDuplication(LogicBlockList.get(i));
            for(int j = (int)(cursor%blockSize); j < bytes.length; j++){
                retBytes[retBytesIndex] = bytes[j];
                retBytesIndex++;
                if(retBytesIndex == length)
                    break;
            }
            i++;
            for(; i < indexEnd; i++){
                bytes = chooseDuplication(LogicBlockList.get(i));
                for(int j = 0; j < bytes.length; j++){
                    retBytes[retBytesIndex] = bytes[j];
                    retBytesIndex++;
                }
            }

            if(retBytesIndex != length){
                bytes = chooseDuplication(LogicBlockList.get((int)indexEnd));
                for(int j = 0; j < bytes.length & retBytesIndex < length; j++){
                    retBytes[retBytesIndex] = bytes[j];
                    retBytesIndex++;
                    if(retBytesIndex == length)
                        break;
                }
            }

            cursor += length;
            return retBytes;
        }catch (ErrorCode errorCode){
            throw errorCode;
        }
    };
    public void write(byte[] b){
        if(cursor < 0)
            throw new ErrorCode(ErrorCode.CURSOR_ERROR);
        if(cursor > fileSize && fileSize != 0){
            byte[] n = new byte[(int)(cursor - fileSize)+b.length];
            for(int i = 0; i < cursor-fileSize; i++){
                n[i] = 0x00;
            }
            for(int i = 0; i < b.length; i++){
                n[i] = b[i];
            }
            b = n;
        }
        int indexBegin = (int)(cursor/blockSize);
        int indexEnd = (int)((cursor+b.length)/blockSize);
        int writeIndex = 0;
        byte[] newBytes = new byte[(int)blockSize];
        Map<Id, Id> map = null;
        try {
            map = LogicBlockList.get(indexBegin);
        }catch (IndexOutOfBoundsException e){
            map = null;
        }
        byte[] oldBytes = chooseDuplication(map);
        int s = LogicBlockList.size();
        for(int i = indexBegin; i < s; i++){
            LogicBlockList.remove(indexBegin);
        }
        if(indexBegin != indexEnd){
            int i = 0;
            for(; i < cursor%blockSize;i++){//??????????????????????
                newBytes[i] = oldBytes[i];
            }
            for(;i < blockSize;i++){
                newBytes[i] = b[writeIndex];
                writeIndex++;
            }
            LogicBlockList.add(writeDuplication(newBytes));

            for(i = indexBegin+1; i < indexEnd; i++){
                newBytes = new byte[(int)blockSize];
                for(int j = 0; j < blockSize; j++){
                    newBytes[j] = b[writeIndex];
                    writeIndex++;
                }
                LogicBlockList.add(writeDuplication(newBytes));
            }
            newBytes = new byte[b.length - writeIndex];
            for(int j = 0; j < newBytes.length; j++){//////////////////////
                newBytes[j] = b[writeIndex];
                writeIndex++;
            }
            if(newBytes.length != 0)
                LogicBlockList.add(writeDuplication(newBytes));
        }else {
            newBytes = new byte[(int)(cursor%blockSize) + b.length];
            int i = 0;
            for(; i < cursor%blockSize;i++){//??????????????????????
                newBytes[i] = oldBytes[i];
            }
            for(;i < newBytes.length;i++){
                newBytes[i] = b[writeIndex];
                writeIndex++;
            }
            if(newBytes.length != 0)
                LogicBlockList.add(writeDuplication(newBytes));
        }




        //更新file.meta
        this.fileSize = cursor+b.length;
        ((MyFileManagerClient)fileManager).updateInode(this);
//
//        long newFileSize = cursor+b.length;
//        try {
//            updateFileMeta(newFileSize);
//        }catch (ErrorCode errorCode){
//            throw errorCode;
//        }
        //先newfilesize后再set filesize是为了一致性
        cursor += b.length;

    }

    public long move(long offset, int where){
        switch (where){
            case MOVE_CURR:
                cursor += offset;
                break;
            case MOVE_HEAD:
                cursor = offset;
                break;
            case MOVE_TAIL:
                cursor = fileSize - offset;
                break;
        }
        return 0;
    }
    public void close(){
    }
    public long size(){
        return fileSize;
    }
    public void setSize(long newSize){
        if(newSize < 0)
            throw new ErrorCode(ErrorCode.NEWSIZE_ERROR);
        long oldSize = this.fileSize;

        if(newSize > oldSize){
            int oldBlockEndIndex = (int)(oldSize/blockSize);
            int newBlockEndIndex = (int)(newSize/blockSize);

            byte[] oldBlockEnd = chooseDuplication(LogicBlockList.get(oldBlockEndIndex));
            int s = LogicBlockList.size();
            for(int i = oldBlockEndIndex; i < s; i++){
                LogicBlockList.remove(oldBlockEndIndex);
            }
            int index = 0;
            byte[] newBlock;
            if(newBlockEndIndex == oldBlockEndIndex){
                newBlock = new byte[(int)(newSize%blockSize)];
            }else {
                newBlock = new byte[(int)blockSize];
            }

            for(int i = 0; i < oldBlockEnd.length; i++){
                newBlock[index] = oldBlockEnd[i];
                index++;
            }

            for(;index < blockSize; index++){
                newBlock[index] = 0x00;
                if(index == newBlock.length - 1)
                    break;
            }

            LogicBlockList.add(writeDuplication(newBlock));
            for(int i = oldBlockEndIndex+1; i < newBlockEndIndex; i++){
                LogicBlockList.add(writeDuplication(new byte[(int)blockSize]));
            }

            if(oldBlockEndIndex != newBlockEndIndex)
                LogicBlockList.add(writeDuplication(new byte[(int)(newSize%blockSize)]));

            this.fileSize = newSize;
            ((MyFileManagerClient)fileManager).updateInode(this);
//            updateFileMeta(newSize);
        }else if(newSize < oldSize){
            int newBlockEndIndex = (int)(newSize/blockSize); //newIndex oldIndex
            byte[] newBlockEnd = chooseDuplication(LogicBlockList.get(newBlockEndIndex));
            int s = LogicBlockList.size();
            for(int i = newBlockEndIndex; i < s; i++){
                LogicBlockList.remove(newBlockEndIndex);
            }

            byte[] newBlock = new byte[(int)(newSize%blockSize)];
            for(int i = 0; i < newBlock.length; i++){
                newBlock[i] = newBlockEnd[i];
            }
            LogicBlockList.add(writeDuplication(newBlock));

            this.fileSize = newSize;
            ((MyFileManagerClient)fileManager).updateInode(this);
//            updateFileMeta(newSize);
        }

    }

    private byte[] chooseDuplication(Map<Id, Id> map){
        byte[] blockData = null;
        if(map == null){
            blockData = new byte[(int)blockSize];
        }else {
            for(Map.Entry<Id,Id> entry : map.entrySet()){
                if(entry.getKey().equals(new StringId("FILE_EMPTY"))){
                    blockData = new byte[(int)blockSize];
                    break;
                }else {
                    MyBlockManagerClient bm = mContext.myBlockManagerClientMap.get(entry.getKey());
//                    //如果bmserver没有上线/又下线了
//                    if(bm.checkConnect() == 0)
//                        continue;
                    try {
                        Block block = bm.getBlock(entry.getValue());
                        blockData = block.read();
                        break;
                    }catch (ErrorCode errorCode){
                        continue;
                    }
                }
            }
        }

        if(blockData == null)
            throw new ErrorCode(ErrorCode.MEMORY_ERROR);
        else
            return blockData;
    }


    private Map<Id, Id> writeDuplication(byte[] b){
        Map<Id, Id> ret = new HashMap<>();
        boolean isEmpty1 = true;
        for(byte b1 : b){
            if(b1 != 0x00) {
                isEmpty1 = false;
                break;
            }
        }
        if(isEmpty1){
            return mContext.fileEmpytMap;
        }else {
            //计算当前连接bmserver数
            int count = 0;
            ArrayList<MyBlockManagerClient> avaliableBMC = new ArrayList<>();
            for(Map.Entry<StringId, MyBlockManagerClient> entry : mContext.myBlockManagerClientMap.entrySet()){
                if(entry.getValue().isConnect()) {
                    count++;
                    avaliableBMC.add(entry.getValue());
                }else {

                }
            }
            if(count == 0){
                throw new ErrorCode(ErrorCode.NO_BMSERVER_ONLINE);
            }
            int dupliaction_number = 2; //可更改
            dupliaction_number = (count < dupliaction_number)?count:dupliaction_number;

            for(int i = 0; i < dupliaction_number; i++){
                int index = (startNum+i)%(avaliableBMC.size());
                Block block = writeBlock(avaliableBMC.get(index),b);
                ret.put(avaliableBMC.get(index).getSid(),block.getIndexId());
            }
            startNum = (startNum + 1)%3;

            return ret;
        }
    }
    private Block writeBlock(MyBlockManagerClient bmc, byte[] b){
        Block newBlock = bmc.newBlock(b);
        return newBlock;
    }


    public ArrayList<Map<Id, Id>> getLogicBlockList() {
        return LogicBlockList;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getBlockSize() {
        return blockSize;
    }
}

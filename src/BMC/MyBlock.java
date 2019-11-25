package BMC;

import BMS.Buffer;
import BMS.BufferBlk;
import Impl.MD5Util;
import interfaces.Block;
import interfaces.BlockManager;
import interfaces.Id;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MyBlock implements Block {
    private Id indexId;
    private BlockManager blockManagerC;
    private byte[] data;
    private long blockSize;

    public MyBlock(Id indexId, BlockManager blockManagerC,byte[] data, long blockSize){
        this.indexId = indexId;
        this.data = data;
        this.blockSize = blockSize;
        this.blockManagerC = blockManagerC;
    }

    @Override
    public Id getIndexId() {
        return indexId;
    }

    @Override
    public BlockManager getBlockManager() {
        return blockManagerC;
    }

    @Override
    public byte[] read() {
        return data;
    }

    @Override
    public int blockSize() {
        return (int) blockSize;
    }
}

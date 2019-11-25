package FMS;

import Impl.StringId;
import interfaces.Id;

import java.util.ArrayList;
import java.util.Map;

public class BufferInode {
    private StringId inodesId;
    private ArrayList<Map<Id,Id>> inode_data;
    private long fileSize;
    private long blockSize;
    private BufferInode preFreeBufInode;
    private BufferInode nextFreeBufInode;

    public long getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(long blockSize) {
        this.blockSize = blockSize;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public StringId getInodesId() {
        return inodesId;
    }

    public void setInodesId(StringId inodesId) {
        this.inodesId = inodesId;
    }

    public ArrayList<Map<Id, Id>> getInode_data() {
        return inode_data;
    }

    public void setInode_data(ArrayList<Map<Id, Id>> inode_data) {
        this.inode_data = inode_data;
    }

    public BufferInode getPreFreeBufInode() {
        return preFreeBufInode;
    }

    public void setPreFreeBufInode(BufferInode preFreeBufInode) {
        this.preFreeBufInode = preFreeBufInode;
    }

    public BufferInode getNextFreeBufInode() {
        return nextFreeBufInode;
    }

    public void setNextFreeBufInode(BufferInode nextFreeBufInode) {
        this.nextFreeBufInode = nextFreeBufInode;
    }
}

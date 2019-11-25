package FMS;

import BMS.Buffer;
import Impl.ErrorCode;
import Impl.StringId;
import interfaces.Id;

import java.util.ArrayList;

public class BufferFMS {
    public final static int FM_BUFFER_LINES = 4;
    private ArrayList<ArrayList<BufferInode>> inode_cache;
    private BufferInode freeHead_inode;
    BufferFMS(){
        freeHead_inode = new BufferInode();
        inode_cache = new ArrayList<>();
        BufferInode tmp = freeHead_inode;

        for(int i = 0; i < FM_BUFFER_LINES; i++){
            ArrayList<BufferInode> al = new ArrayList<>();
            inode_cache.add(al);
            for(int j = 0; j < 4; j++){
                BufferInode bufInode = new BufferInode();
                bufInode.setInodesId(new StringId("-1"));
                bufInode.setFileSize(0);
                bufInode.setBlockSize(0);
                bufInode.setPreFreeBufInode(tmp);
                tmp.setNextFreeBufInode(bufInode);
                tmp = bufInode;
            }
        }
    }

    public static int getFmBufferLines() {
        return FM_BUFFER_LINES;
    }

    public ArrayList<ArrayList<BufferInode>> getInode_cache() {
        return inode_cache;
    }

    public BufferInode getFreeHead_inode() {
        return freeHead_inode;
    }

    public long inodeHash(Id fileId){
        String name = ((StringId)fileId).getId();
        if(name.equals("-1"))
            return -1;
        //以下hash策略可以随意修改
        long hash = 0;
        for(int i = 0; i < name.length(); i++){
            hash =+ (int)name.charAt(i);
        }
        return hash;
    }

    public BufferInode findFreeInode(){
        return freeHead_inode.getNextFreeBufInode();
    }
    public void change_place_on_free_list(BufferInode inode){
        BufferInode tmp = freeHead_inode.getNextFreeBufInode();
        BufferInode tmpPre = inode.getPreFreeBufInode();
        BufferInode tmpNext = inode.getNextFreeBufInode();
        if(tmpPre != null && tmpNext != null){
            tmpPre.setNextFreeBufInode(tmpNext);
            tmpNext.setPreFreeBufInode(tmpPre);
        }else if(tmpPre != null && tmpNext == null){
            tmpPre.setNextFreeBufInode(null);
        }
        while (tmp.getNextFreeBufInode() != null){
            tmp = tmp.getNextFreeBufInode();
        }
        tmp.setNextFreeBufInode(inode);
        inode.setPreFreeBufInode(tmp);
        inode.setNextFreeBufInode(null);
    }
    public void deleteFromCache(BufferInode inode){
        int inodeId = (int)inodeHash(inode.getInodesId());
        int oldInodeIndex = inodeId % FM_BUFFER_LINES;
        if(inodeId != -1){
            //这里比bufferblk的地方少写了个东西，但是感觉没问题
            inode_cache.get(oldInodeIndex).remove(inode);
        }
    }
}

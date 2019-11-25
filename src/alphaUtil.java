import BMC.MyBlockManagerClient;
import FMS.MyFileManagerServer;
import Impl.ErrorCode;
import Impl.StringId;
import Impl.mContext;
import interfaces.Block;
import interfaces.BlockManager;
import interfaces.File;
import interfaces.FileManager;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class alphaUtil {
    private static File operatingFile;
    public static void alphaCat(String fmName, String fileName){
        try {
            FileManager fm = mContext.myFileManagerClientMap.get(new StringId(fmName));
            File file = fm.getFile(new StringId(fileName));
            operatingFile = file;
            file.move(0, File.MOVE_HEAD);
            System.out.println(new String(file.read((int)file.size())));
        }catch (ErrorCode errorCode){
//            System.out.println(errorCode.getErrorText(errorCode.getErrorCode()));
            throw errorCode;
        }
    }
    public static void alphaHex(String bmName, int blockIndex){
        try {
            MyBlockManagerClient bm = mContext.myBlockManagerClientMap.get(new StringId(bmName));
            Block block = bm.getBlock(new StringId(blockIndex+""));
            byte[] bytes = block.read();
            for(int i = 0; i < bytes.length;i++){
                System.out.print("0x"+Integer.toHexString(bytes[i])+" ");
                if(i%16 == 15)
                    System.out.print("\n");
            }
            System.out.print("\n");
        }catch (ErrorCode errorCode){
//            System.out.println(errorCode.getErrorText(errorCode.getErrorCode()));
            throw errorCode;
        }
    }
    public static void alphaWrite(int offset, String where, String content){
        try {
            int cursorWhere = 0;
            if(operatingFile == null)
                throw new ErrorCode(ErrorCode.NO_OPERATING_FILE);
            if("cur".equals(where))
                cursorWhere = File.MOVE_CURR;
            else if("head".equals(where))
                cursorWhere = File.MOVE_HEAD;
            else if("tail".equals(where))
                cursorWhere = File.MOVE_TAIL;
            else
                throw new ErrorCode(ErrorCode.NO_THIS_CURSOR_TYPE);

            operatingFile.move(offset,cursorWhere);
            operatingFile.write(content.getBytes());

        }catch (ErrorCode errorCode){
//            System.out.println(errorCode.getErrorText(errorCode.getErrorCode()));
            throw errorCode;
        }
    }


    public static void alphaCreate(String fmName, String fileName){
        try {
            FileManager fm = mContext.myFileManagerClientMap.get(new StringId(fmName));
            File file = fm.newFile(new StringId(fileName));
            operatingFile = file;
        }catch (ErrorCode errorCode){
//            System.out.println(errorCode.getErrorText(errorCode.getErrorCode()));
            throw errorCode;
        }
    }

    public static void alphaSetSize(int newSize){
        try {
            if(operatingFile == null)
                throw new ErrorCode(ErrorCode.NO_OPERATING_FILE);
            operatingFile.setSize(newSize);
        }catch (ErrorCode errorCode){
            throw errorCode;
        }

    }



}

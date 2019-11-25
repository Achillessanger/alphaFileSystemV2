//import FMS.MyFileManagerServer;
//import Impl.ErrorCode;
//import Impl.StringId;
//import interfaces.Block;
//import interfaces.BlockManager;
//import interfaces.File;
//import interfaces.FileManager;
//
//import java.io.BufferedReader;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//
//public class alphaUtil {
//    private static ArrayList<File> openedFile = new ArrayList<>();
//    private static File operatingFile;
//    public static void alphaCat(String fmName, String fileName){
//        try {
//            FileManager fm = Impl.mContext.myFileManagerMap.get(new StringId(fmName));
//            File file = fm.getFile(new StringId(fileName));
//            operatingFile = file;
//            file.move(0, File.MOVE_HEAD);
//            System.out.println(new String(file.read((int)file.size())));
//        }catch (ErrorCode errorCode){
////            System.out.println(errorCode.getErrorText(errorCode.getErrorCode()));
//            throw errorCode;
//        }
//    }
//    public static void alphaHex(String bmName, int blockIndex){
//        try {
//            BlockManager bm = Impl.mContext.myBlockManagerMap.get(new StringId(bmName));
//            Block block = bm.getBlock(new StringId(blockIndex+""));
//            byte[] bytes = block.read();
//            for(int i = 0; i < bytes.length;i++){
//                System.out.print("0x"+Integer.toHexString(bytes[i])+" ");
//                if(i%16 == 15)
//                    System.out.print("\n");
//            }
//            System.out.print("\n");
//        }catch (ErrorCode errorCode){
////            System.out.println(errorCode.getErrorText(errorCode.getErrorCode()));
//            throw errorCode;
//        }
//    }
//    public static void alphaWrite(int offset, String where, String content){
//        try {
//            int cursorWhere = 0;
//            if(operatingFile == null)
//                throw new ErrorCode(ErrorCode.NO_OPERATING_FILE);
//            if("cur".equals(where))
//                cursorWhere = File.MOVE_CURR;
//            else if("head".equals(where))
//                cursorWhere = File.MOVE_HEAD;
//            else if("tail".equals(where))
//                cursorWhere = File.MOVE_TAIL;
//            else
//                throw new ErrorCode(ErrorCode.NO_THIS_CURSOR_TYPE);
//
//            operatingFile.move(offset,cursorWhere);
//            operatingFile.write(content.getBytes());
//            if(!openedFile.contains(operatingFile))
//                openedFile.add(operatingFile);
//        }catch (ErrorCode errorCode){
////            System.out.println(errorCode.getErrorText(errorCode.getErrorCode()));
//            throw errorCode;
//        }
//    }
//    public static void alphaCopy(String fmNameOld, String fileNameOld, String fmNameNew, String fileNameNew){
//        try{
//            FileManager fmOld = Impl.mContext.myFileManagerMap.get(new StringId(fmNameOld));
////            File fileOld = fmOld.getFile(new Impl.StringId(fileNameOld));
//            FileManager fmNew = Impl.mContext.myFileManagerMap.get(new StringId(fmNameNew));
////            File fileNew = fmNew.newFile(new Impl.StringId(fileNameNew));
//
//            java.io.File oldFile = new java.io.File(((MyFileManagerServer) fmOld).getPath()+fileNameOld+".meta");
//            java.io.File newFile = new java.io.File(((MyFileManagerServer) fmNew).getPath()+fileNameNew+".meta");
//
//            if(newFile.exists())
//                throw new ErrorCode(ErrorCode.NO_SUCH_FILE);
//            newFile.createNewFile();
//            FileOutputStream out = new FileOutputStream(newFile,true);
//            StringBuffer sb = new StringBuffer();
//            BufferedReader br = new BufferedReader(new FileReader(oldFile));
//            String tmp;
//            tmp = br.readLine();
//            while (tmp != null){
//                sb.append(tmp+"\n");
//                tmp = br.readLine();
//            }
//            out.write(sb.toString().getBytes("utf-8"));
//            out.close();
//
////            fileNew.write(fileOld.read((int)fileOld.size()));
//        }catch (ErrorCode errorCode){
////            System.out.println(errorCode.getErrorText(errorCode.getErrorCode()));
//            throw errorCode;
//        }catch (IOException e){
//            throw new ErrorCode(ErrorCode.IO_EXCEPTION);
//        }
//    }
//
//    public static void finish(){
//        for(File f : openedFile){
//            try {
//                f.close();
//            }catch (ErrorCode errorCode){
////                System.out.println(errorCode.getErrorText(errorCode.getErrorCode()));
//                throw errorCode;
//            }
//        }
//    }
//
//    public static void alphaCreate(String fmName, String fileName){
//        try {
//            FileManager fm = Impl.mContext.myFileManagerMap.get(new StringId(fmName));
//            File file = fm.newFile(new StringId(fileName));
//            operatingFile = file;
//        }catch (ErrorCode errorCode){
////            System.out.println(errorCode.getErrorText(errorCode.getErrorCode()));
//            throw errorCode;
//        }
//    }
//
//    public static void alphaSetSize(int newSize){
//        try {
//            if(operatingFile == null)
//                throw new ErrorCode(ErrorCode.NO_OPERATING_FILE);
//            operatingFile.setSize(newSize);
//        }catch (ErrorCode errorCode){
//            throw errorCode;
//        }
//
//    }
//    public static void alphaClose(){
//        try {
//            if(operatingFile == null)
//                throw new ErrorCode(ErrorCode.NO_OPERATING_FILE);
//            operatingFile.close();
//            if(openedFile.contains(operatingFile))
//                openedFile.remove(operatingFile);
//        }catch (ErrorCode errorCode){
//            throw errorCode;
//        }
//    }
//
//
//}

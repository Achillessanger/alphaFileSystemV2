import BMC.MyBlockManagerClient;
import BMS.MyBlockManagerServer;
import FMC.MyFileManagerClient;
import FMS.Ifm.IFileManager;
import FMS.MyFileManagerServer;
import Impl.ErrorCode;
import Impl.StringId;
import Impl.mContext;
import interfaces.File;

import java.rmi.RemoteException;
import java.util.Scanner;

public class main {
    public static void main(String[] args) throws RemoteException {
//        MyFileManagerClient fm_client = mContext.myFileManagerClientMap.get(new StringId("fm-01"));
//        fm_client.reConnect();
//        if(!fm_client.isConnect()){
//            System.out.println("fm server not connect");
//        }else {
//            System.out.println("fm server connecting ...");
//        }
//        StringId sid = new StringId("test");
////        IFileManager fm1_s = new MyFileManagerServer("./path/to/fm-01/");
////        fm1_s.newFileMeta(sid);
//        File file = fm_client.getFile(sid);
//        MyBlockManagerClient bm_client = mContext.myBlockManagerClientMap.get(new StringId("bm-01"));
//        MyBlockManagerServer bm_server = new MyBlockManagerServer("./path/to/bm-01/",new StringId("bm-01"));
////        bm_server.readBlock(new StringId("2"));
//
//        System.out.println("-----------------");
//        System.out.println(new String(file.read(4126)));
//        System.out.println("-----------------");
//        file.setSize(2050);

        Scanner scanner = new Scanner(System.in);
        System.out.println("supported commands:cat,hex,write,copy,create,setsize,c,q:");
        while (true){
            System.out.println("$");
            String command = scanner.nextLine();
            String op = command.split(" ")[0];
            String[] commandArr;
            try{
                switch (op){
                    case "cat":
                        commandArr = command.split(" ",3);
                        if(commandArr.length == 3 && isFm(commandArr[1]))
                            alphaUtil.alphaCat(commandArr[1],commandArr[2]);
                        else
                            throw new ErrorCode(ErrorCode.COMMAND_PARAMETER_ERROE);

                        break;
                    case "hex":
                        commandArr = command.split(" ",3);
                        if(commandArr.length == 3 && isNumber(commandArr[2]) && isBm(commandArr[1]))
                            alphaUtil.alphaHex(commandArr[1],Integer.parseInt(commandArr[2]));
                        else
                            throw new ErrorCode(ErrorCode.COMMAND_PARAMETER_ERROE);
                        break;
                    case "write":
                        commandArr = command.split(" ",4);
                        if(commandArr.length == 4 && isNumber(commandArr[1]))
                            alphaUtil.alphaWrite(Integer.parseInt(commandArr[1]),commandArr[2],commandArr[3]);
                        else
                            throw new ErrorCode(ErrorCode.COMMAND_PARAMETER_ERROE);
                        break;
                    case "q":
                        System.exit(0);
                        break;
                    case "create":
                        commandArr = command.split(" ",3);
                        if(commandArr.length == 3)
                            alphaUtil.alphaCreate(commandArr[1],commandArr[2]);
                        else
                            throw new ErrorCode(ErrorCode.COMMAND_PARAMETER_ERROE);
                        break;
                    case "setsize":
                        commandArr = command.split(" ",2);
                        if(commandArr.length == 2 && isNumber(commandArr[1]))
                            alphaUtil.alphaSetSize(Integer.parseInt(commandArr[1]));
                        else
                            throw new ErrorCode(ErrorCode.COMMAND_PARAMETER_ERROE);
                        break;
                    default:
                        throw new ErrorCode(ErrorCode.INVALID_COMMAND);
                }
            }catch (ErrorCode errorCode){
                System.out.println(errorCode.getErrorText(errorCode.getErrorCode()));
            }

        }

    }
    private static boolean isNumber(String str){
        String reg = "^[0-9]+(.[0-9]+)?$";
        return str.matches(reg);
    }
    private static boolean isFm(String str){
        return mContext.myFileManagerClientMap.containsKey(new StringId(str));
    }
    private static boolean isBm(String str){
        return mContext.myBlockManagerClientMap.containsKey(new StringId(str));
    }

}

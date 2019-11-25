//import Impl.ErrorCode;
//import Impl.StringId;
//
//import java.util.Scanner;
//
//public class test {
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("supported commands:cat,hex,write,copy,create,setsize,c,q:");
//        while (true){
//            System.out.println("$");
//            String command = scanner.nextLine();
//            String op = command.split(" ")[0];
//            String[] commandArr;
//            try{
//                switch (op){
//                    case "c":
//                        alphaUtil.alphaClose();
//                        break;
//                    case "cat":
//                        commandArr = command.split(" ",3);
//                        if(commandArr.length == 3 && isFm(commandArr[1]))
//                            alphaUtil.alphaCat(commandArr[1],commandArr[2]);
//                        else
//                            throw new ErrorCode(ErrorCode.COMMAND_PARAMETER_ERROE);
//
//                        break;
//                    case "hex":
//                        commandArr = command.split(" ",3);
//                        if(commandArr.length == 3 && isNumber(commandArr[2]) && isBm(commandArr[1]))
//                            alphaUtil.alphaHex(commandArr[1],Integer.parseInt(commandArr[2]));
//                        else
//                            throw new ErrorCode(ErrorCode.COMMAND_PARAMETER_ERROE);
//                        break;
//                    case "write":
//                        commandArr = command.split(" ",4);
//                        if(commandArr.length == 4 && isNumber(commandArr[1]))
//                            alphaUtil.alphaWrite(Integer.parseInt(commandArr[1]),commandArr[2],commandArr[3]);
//                        else
//                            throw new ErrorCode(ErrorCode.COMMAND_PARAMETER_ERROE);
//                        break;
//                    case "copy":
//                        commandArr = command.split(" ",5);
//                        if(commandArr.length == 5 && isFm(commandArr[1]) && isFm(commandArr[3]))
//                            alphaUtil.alphaCopy(commandArr[1],commandArr[2],commandArr[3],commandArr[4]);
//                        else
//                            throw new ErrorCode(ErrorCode.COMMAND_PARAMETER_ERROE);
//                        break;
//                    case "q":
//                        alphaUtil.finish();
//                        System.exit(0);
//                        break;
//                    case "create":
//                        commandArr = command.split(" ",3);
//                        if(commandArr.length == 3)
//                            alphaUtil.alphaCreate(commandArr[1],commandArr[2]);
//                        else
//                            throw new ErrorCode(ErrorCode.COMMAND_PARAMETER_ERROE);
//                        break;
//                    case "setsize":
//                        commandArr = command.split(" ",2);
//                        if(commandArr.length == 2 && isNumber(commandArr[1]))
//                            alphaUtil.alphaSetSize(Integer.parseInt(commandArr[1]));
//                        else
//                            throw new ErrorCode(ErrorCode.COMMAND_PARAMETER_ERROE);
//                        break;
//                    default:
//                        throw new ErrorCode(ErrorCode.INVALID_COMMAND);
//                }
//            }catch (ErrorCode errorCode){
//                System.out.println(errorCode.getErrorText(errorCode.getErrorCode()));
//            }
//
//        }
//    }
//    private static boolean isNumber(String str){
//        String reg = "^[0-9]+(.[0-9]+)?$";
//        return str.matches(reg);
//    }
//    private static boolean isFm(String str){
//        return Impl.mContext.myFileManagerMap.containsKey(new StringId(str));
//    }
//    private static boolean isBm(String str){
//        return Impl.mContext.myBlockManagerMap.containsKey(new StringId(str));
//    }
//}

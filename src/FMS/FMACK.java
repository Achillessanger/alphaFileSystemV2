package FMS;

import java.io.Serializable;

public class FMACK implements Serializable {
    private int isValid;
    private int errorCode;
    public FMACK(int errorCode){
        this.isValid = 0;
        this.errorCode = errorCode;
    }
    public FMACK(){
        this.isValid = 1;
        this.errorCode = 0;
    }

    public int getIsValid() {
        return isValid;
    }

    public int getErrorCode() {
        return errorCode;
    }
}

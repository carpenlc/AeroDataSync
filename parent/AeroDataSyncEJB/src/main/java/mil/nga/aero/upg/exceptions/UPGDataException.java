package mil.nga.aero.upg.exceptions;

import java.io.Serializable;

/**
 * Custom exception thrown if an error was encountered during processing
 * of the UPG data.
 * 
 * @author L. Craig Carpenter
 */
public class UPGDataException extends Exception implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -9061259698313395640L;

    /**
     * The error code to raise to the caller.
     */
    private int ID = 0;
    
    /**
     * The text associated with the input error code.
     */
    private String messageText = null;
    
    /** 
     * Default constructor requiring clients to supply a String identifying
     * why a UPG data request failed.
     *  
     * @param msg String identifying what went wrong with the UPG data request.
     */
    public UPGDataException(ErrorCodes errorCode) {
        setErrorCode(errorCode.getID());
        setMessageText(errorCode.getMessage());
    }
    
    /**
     * Getter method for the error code ID number.
     * @return The ID associated with the error code.
     */
    public int getErrorCode() {
        return this.ID;
    }
    
    /**
     * Getter method for the error code message.
     * @return The message associated with the error code.
     */
    public String getMessageText() {
        return this.messageText;
    }
    
    /**
     * Get the string error message concatenating the error code and
     * error text.
     */
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Error Code => [ ");
        sb.append(getErrorCode());
        sb.append(" ], Error Text => [ ");
        sb.append(getMessageText());
        sb.append(" ].");
        return sb.toString();
    }
    
    /**
     * Setter method for the error code ID number.
     * @param value The ID associated with the error code.
     */
    public void setErrorCode(int value) {
        this.ID = value;
    }
    
    /**
     * Setter method for the error code message.
     * @param value The message associated with the error code.
     */
    public void setMessageText(String value) {
        this.messageText = value;
    }
}

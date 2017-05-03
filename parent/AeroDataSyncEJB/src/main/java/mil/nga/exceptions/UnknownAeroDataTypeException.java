package mil.nga.exceptions;

/**
 * Exception raised when an unsupported archive format is requested.
 * 
 * @author carpenlc
 */
public class UnknownAeroDataTypeException extends Exception {
    
    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 1685272697353150213L;

    /** 
     * Default constructor requiring a message String.
     * @param msg Information identifying why the exception was raised.
     */
    public UnknownAeroDataTypeException(String msg) {
        super(msg);
    }
    
}

package mil.nga.types;

import mil.nga.exceptions.UnknownAeroDataTypeException;

/**
 * Aero data types to be synchronized with external organizations.
 * 
 * @author L. Craig Carpenter
 */
public enum AeroDataType {
    UPG("upg"),
    JEPP("jepp");

    /**
     * The text field.
     */
    private final String text;
    
    /**
     * Default constructor.
     * 
     * @param text Text associated with the enumeration value.
     */
    private AeroDataType(String text) {
        this.text = text;
    }
    
    /**
     * Getter method for the text associated with the enumeration value.
     * 
     * @return The text associated with the instanced enumeration type.
     */
    public String getText() {
        return this.text;
    }
    
    /**
     * Convert an input String to it's associated enumeration type.  There
     * is no default type, if an unknown value is supplied an exception is
     * raised.
     * 
     * @param text Input text information
     * @return The appropriate AeroDataType enum value.
     * @throws UnknownAeroDataTypeException Thrown if the caller submitted a String 
     * that did not match one of the existing AeroDataType. 
     */
    public static AeroDataType fromString(String text) 
            throws UnknownAeroDataTypeException {
        if (text != null) {
            for (AeroDataType type : AeroDataType.values()) {
                if (text.trim().equalsIgnoreCase(type.getText())) {
                    return type;
                }
            }
        }
        throw new UnknownAeroDataTypeException("Unknown aero data type requested!  " 
                + "Data type requested [ " 
                + text
                + " ].");
    }        
}

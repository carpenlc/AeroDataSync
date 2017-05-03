package mil.nga.aero.upg.exceptions;

public enum ErrorCodes {
    APPLICATION_EXCEPTION(
            1000,
            "Application exception encountered.  Please see error logs for "
            + "more information."),
    PROPERTIES_NOT_LOADED(
            1005,
            "Unable to load the target properties file."),
    PROPERTIES_NOT_DEFINED(
            1010,
            "Required properties are not defined. "),
    INVALID_HTTP_STATUS_CODE(
            1015,
            "The GET request executed for the target URL returned an invalid "
            + "HTTP status code."),
    CLIENT_PROTOCOL_EXCEPTION(
            1020,
            "Unexpected ClientProtocolException trying to execute the GET "
            + "request to retrieve the UPG data."),
    IO_EXCEPTION(
            1025,
            "Unexpected IOException trying to execute the GET "
            + "request to retrieve the UPG data."),
    JSON_IO_EXCEPTION(
            1030,
            "Unexpected IOException trying to unmarshall the JSON data "
            + "to a Java POJO."),
    JSON_PARSER_EXCEPTION(
            1035,
            "Unexpected JsonParseException trying to unmarshall the JSON data "
            + "to a Java POJO.  Something is wrong with the JSON retrieved "
            + "from the target URL."),
    JSON_MAPPING_EXCEPTION(
            1040,
            "Unexpected JsonMappingException trying to unmarshall the JSON data "
            + "to a Java POJO.  Something is wrong with the JSON retrieved "
            + "from the target URL."),
    MALFORMED_URL (
            1045,
            "Unexpected MalformedURLException while trying to download UPG "
            + "products from the target URL."),
    NO_DATA_RETRIEVED (
            1050,
            "No UPG data retrieved from the target URL."),
    FILESYSTEM_EXCEPTION (
            1055,
            "An exception was raised while interacting with the local file "
            + "system."),
    UNSUPPORTED_PRODUCT_EXCEPTION (
            1060,
            "An unsupported product was requested. "),
    INVALID_PRODUCT_EXCEPTION (
            1065,
            "An invalid product was requested (most likely NULL.)"),
    DATA_SOURCE_EXCEPTION (
            1070,
            "An exception was raised while attempting to interact with the "
            + "back end data store.");
    
    /**
     * Error code ID
     */
    private int ID = 0;
    
    /**
     * Error message 
     */
    private String message = null;
    
    /**
     * Private constructor setting the ID and error message string associated 
     * with the enum type.
     * 
     * @param id The error code ID.
     * @param msg The error code message.
     */
    private ErrorCodes(int id, String msg) {
        setID(id);
        setMessage(msg);
    }
    
    /**
     * Getter method for the error code message.
     * @return The message associated with the error code.
     */
    public String getMessage() {
        return this.message;
    }
    
    /**
     * Getter method for the error code ID number.
     * @return The ID associated with the error code.
     */
    public int getID() {
        return this.ID;
    }
    
    /**
     * Setter method for the error code ID number.
     * @param value The ID associated with the error code.
     */
    public void setID(int value) {
        this.ID = value;
    }
    
    /**
     * Setter method for the error code message.
     * @param value The message associated with the error code.
     */
    public void setMessage(String value) {
        this.message = value;
    }
}

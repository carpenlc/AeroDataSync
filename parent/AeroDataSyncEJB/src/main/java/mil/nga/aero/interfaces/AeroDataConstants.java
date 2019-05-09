package mil.nga.aero.interfaces;

/**
 * Interface containing constants used within the UPG synchronization process.
 * 
 * @author L. Craig Carpenter
 */
public interface AeroDataConstants {

    /**
     * The file containing the system properties required for keeping the UPG
     * data up to date.  This is the only thing hard-coded in the application.
     */
    public static String PROPERTIES_FILE = "config.properties";
    
    /**
     * The target URL from which to retrieve the JSON-formatted UPG data.
     */
    public static String UPG_TARGET_URL = "UPG.source.URL";
    
    /**
     * File system location in which to store the downloaded UPG data.
     */
    public static String UPG_DOWNLOAD_DIR = "UPG.download.dir";
    
    /**
     * The base URL used by clients who will download the data from NGA.
     */
    public static String UPG_BASE_URL = "UPG.base.URL";
    
    /**
     * The target URL from which to retrieve the JSON-formatted UPG data.
     */
    public static String JEPP_TARGET_URL = "JEPP.source.URL";
    
    /**
     * File system location in which to store the downloaded UPG data.
     */
    public static String JEPP_DOWNLOAD_DIR = "JEPP.download.dir";
    
    /**
     * The base URL used by clients who will download the data from NGA.
     */
    public static String JEPP_BASE_URL = "JEPP.base.URL";
    
    /**
     * Default user agent for the HTTP Get request.
     */
    public static String DEFAULT_USER_AGENT = "Mozilla/5.0";
    
    /**
     * Property containing the Oracle driver class (varies by Oracle client 
     * version)
     */
    public static final String ORACLE_DRIVER_CLASS_PROPERTY = "ORACLE_DRIVER_CLASS";
    
    /**
     * Property containing the DBC URL to the target database
     */
    public static final String ORACLE_DB_URL_PROPERTY = "ORACLE_DB_URL";
    
    /**
     * Property containing the Database user
     */
    public static final String ORACLE_DB_USER_PROPERTY = "ORACLE_USER";
    
    /**
     * Property containing the Database user password
     */
    public static final String ORACLE_PASSWORD_PROPERTY = "ORACLE_PASSWORD";
    
    /**
     * Default file permissions for the destination files.
     */
    public static final String DEFAULT_FILE_PERMISSIONS = "rw-rw-rw-";
    
}

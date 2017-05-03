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
	public static String PROPERTIES_FILE = "/mnt/eng2/gateway/upgdownload/config.properties";
	
	/**
	 * The target URL from which to retrieve the JSON-formatted UPG data.
	 */
	public static String UPG_TARGET_URL = "UPG.source.url";
	
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
	public static String JEPP_TARGET_URL = "JEPP.source.url";
	
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
	
	
}

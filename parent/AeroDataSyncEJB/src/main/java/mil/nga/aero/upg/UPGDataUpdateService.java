package mil.nga.aero.upg;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.aero.DataUpdateService;
import mil.nga.aero.interfaces.AeroDataConstants;
import mil.nga.aero.interfaces.AeroDataUpdateServiceI;
import mil.nga.exceptions.PropertiesNotLoadedException;
import mil.nga.types.AeroDataType;

/**
 * Session Bean implementation class UPGDataUpdateService
 * 
 * This class contains the business logic used to know where 
 * 
 */
@Stateless
@LocalBean
public class UPGDataUpdateService 
        extends DataUpdateService 
        implements AeroDataConstants, AeroDataUpdateServiceI, Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -7313711867339844388L;
    
    /**
     * Set up the logging system for use throughout the class
     */        
    private static final Logger LOGGER = LoggerFactory.getLogger(
            UPGDataUpdateService.class);
    
    /**
     * The base directory where the JEPP data will be stored on the local
     * filesystem.
     */
    private String baseDir;
    
    /**
     * The base URL for the JEPP data on the local system.
     */
    private String baseURL;
    
    /**
     * Eclipse-generated default constructor. 
     */
    public UPGDataUpdateService() { }

    /**
     * Initialization method used to load the required properties.
     */
    @PostConstruct
    public void initialize() {
        try {
            setBaseDirectory(super.getProperty(AeroDataConstants.UPG_DOWNLOAD_DIR));
            setBaseURL(super.getProperty(AeroDataConstants.UPG_BASE_URL));
        }
        catch (PropertiesNotLoadedException pnl) {
            LOGGER.error("Unexpected PropertiesNotLoadedException raised "
                    + "while populating the required internal properties.  "
                    + "Exception message [ "
                    + pnl.getMessage()
                    + " ].");
        }
    }

    /**
     * Setter method for the base URL.
     * @param value The base URL.
     */
    public String getBaseURL() {
        return baseURL;
    }
    
    /**
     * Setter method for the download location.
     * @param value The download location
     */
    public String getBaseDirectory() {
        return baseDir;
    }
    
    /**
     * Setter method for the base URL.
     * @param value The base URL.
     */
    public AeroDataType getDataType() {
        return AeroDataType.UPG;
    }
    
    /**
     * Setter method for the base URL.
     * @param value The base URL.
     */
    public void setBaseURL(String value) {
        baseURL = value;
    }
    
    /**
     * Setter method for the download location.
     * @param value The download location
     */
    public void setBaseDirectory(String value) {
        baseDir = value;
    }
}

package mil.nga.aero;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.aero.interfaces.AeroDataServiceI;
import mil.nga.aero.interfaces.AeroDataUpdateServiceI;
import mil.nga.types.AeroDataType;

/**
 * Class following both the Singleton and Factory creation pattern used to
 * create the concrete implementation of the classes used to ensure that the 
 * back end data store is kept synchronized.
 * 
 * @author L. Craig Carpenter
 */
public class AeroDataUpdateFactory {

    /**
     * Set up the LogBack system for use throughout the class
     */        
    static final Logger LOGGER = LoggerFactory.getLogger(
            AeroDataUpdateFactory.class);
    
    /**
     * Private constructor enforcing the singleton design pattern.
     */
    private AeroDataUpdateFactory() { }
    
    /**
     * Private method used to obtain a reference to the target EJB. 
     *  
     * @return Reference to the JDBCUPGDataStore EJB.
     */
    private AeroDataUpdateServiceI getUPGDataUpdateService() {
        return EJBClientUtilities
                .getInstance()
                .getUPGDataUpdateService();
    }
    
    /**
     * Private method used to obtain a reference to the target EJB.  
     * 
     * @return Reference to the JEPPDataUpdateService EJB.
     */
    private AeroDataUpdateServiceI getJEPPDataUpdateService() {
        return EJBClientUtilities
                .getInstance()
                .getJEPPDataUpdateService();
    }
    
    /**
     * Method constructing the requested concrete implementations of the
     * objects implementing the AeroDataUpdateServiceI interface. 
     * 
     * @param type The AeroDataType we want to persist.
     * @return A concrete implementation that will be used to keep the back-end
     * data stores updated.
     */
    public AeroDataUpdateServiceI construct(AeroDataType type) {
        
        AeroDataUpdateServiceI dataStore = null;
        
        if (type != null) {
            switch(type) {
                case UPG:
                    dataStore = getUPGDataUpdateService();
                    break;
                case JEPP:
                    dataStore = getJEPPDataUpdateService();
                    break;
                default:
                    LOGGER.warn("Unsupported data type requested.  Client "
                            + "requested data service associated with data "
                            + "type [ "
                            + type.getText()
                            + " ] which is not implemented.  Returned data "
                            + "store will be null.");
                    break;
            }
        }
        else {
            LOGGER.error("Null data type requested.  Returned data update "
                    + "service will also be null.");
        }
        return dataStore;
    }
    
    /**
     * Accessor method for the singleton instance of the AeroDataStoreFactory 
     * class.
     * 
     * @return The singleton instance of the AeroDataStoreFactory class.
     */
    public static AeroDataUpdateFactory getInstance() {
        return AeroDataUpdateFactoryHolder.getSingleton();
    }    
    
    /**
     * Static inner class used to construct the Singleton object.  This class
     * exploits the fact that classes are not loaded until they are referenced
     * therefore enforcing thread safety without the performance hit imposed
     * by the <code>synchronized</code> keyword.
     * 
     * @author L. Craig Carpenter
     */
    public static class AeroDataUpdateFactoryHolder {
        
        /**
         * Reference to the Singleton instance of the ClientUtility
         */
        private static AeroDataUpdateFactory _instance = new AeroDataUpdateFactory();
    
        /**
         * Accessor method for the singleton instance of the ClientUtility.
         * @return The Singleton instance of the client utility.
         */
        public static AeroDataUpdateFactory getSingleton() {
            return _instance;
        }
        
    }
}

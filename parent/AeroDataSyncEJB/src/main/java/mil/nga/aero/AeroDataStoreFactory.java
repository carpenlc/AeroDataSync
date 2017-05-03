package mil.nga.aero;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.aero.interfaces.AeroDataStoreI;
import mil.nga.types.AeroDataType;

/**
 * Class following both the Singleton and Factory creation pattern used to
 * create the concrete implementation of the classes used to ensure that the 
 * back end data store is kept synchronized.
 * 
 * @author L. Craig Carpenter
 */
public class AeroDataStoreFactory {

    /**
     * Set up the LogBack system for use throughout the class
     */        
    static final Logger LOGGER = LoggerFactory.getLogger(
            AeroDataStoreFactory.class);
    
    /**
     * Private constructor enforcing the singleton design pattern.
     */
    private AeroDataStoreFactory() { }
    
    /**
     * Private method used to obtain a reference to the target EJB. 
     *  
     * @return Reference to the JDBCUPGDataStore EJB.
     */
    private AeroDataStoreI getUPGDataStore() {
        return EJBClientUtilities
                .getInstance()
                .getJDBCUPGDataService();
    }
    
    /**
     * Private method used to obtain a reference to the target EJB.  
     * 
     * @return Reference to the JDBCJEPPDataStore EJB.
     */
    private AeroDataStoreI getJEPPDataStore() {
        return EJBClientUtilities
                .getInstance()
                .getJDBCJEPPDataService();
    }
    
    /**
     * Method constructing the requested concrete implementations of the
     * objects implementing the AeroDataStoreI interface. 
     * 
     * @param type The AeroDataType we want to persist.
     * @return A concrete implementation that will be used to keep the back-end
     * data stores updated.
     */
    public AeroDataStoreI construct(AeroDataType type) {
        
        AeroDataStoreI dataStore = null;
        
        if (type != null) {
            switch(type) {
                case UPG:
                    dataStore = getUPGDataStore();
                    break;
                case JEPP:
                    dataStore = getJEPPDataStore();
                    break;
                default:
                    LOGGER.warn("Unsupported data type requested.  Client "
                            + "requested data store associated with data "
                            + "type [ "
                            + type.getText()
                            + " ] which is not implemented.  Returned data "
                            + "store will be null.");
                    break;
            }
        }
        else {
            LOGGER.error("Null data type requested.  Returned data store will "
                    + "also be null.");
        }
        return dataStore;
    }
    
    /**
     * Accessor method for the singleton instance of the AeroDataStoreFactory 
     * class.
     * 
     * @return The singleton instance of the AeroDataStoreFactory class.
     */
    public static AeroDataStoreFactory getInstance() {
        return AeroDataStoreFactoryHolder.getSingleton();
    }    
    
    /**
     * Static inner class used to construct the Singleton object.  This class
     * exploits the fact that classes are not loaded until they are referenced
     * therefore enforcing thread safety without the performance hit imposed
     * by the <code>synchronized</code> keyword.
     * 
     * @author L. Craig Carpenter
     */
    public static class AeroDataStoreFactoryHolder {
        
        /**
         * Reference to the Singleton instance of the ClientUtility
         */
        private static AeroDataStoreFactory _instance = new AeroDataStoreFactory();
    
        /**
         * Accessor method for the singleton instance of the ClientUtility.
         * @return The Singleton instance of the client utility.
         */
        public static AeroDataStoreFactory getSingleton() {
            return _instance;
        }
        
    }
}

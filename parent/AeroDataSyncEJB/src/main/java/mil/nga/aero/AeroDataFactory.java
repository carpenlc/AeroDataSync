package mil.nga.aero;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.aero.interfaces.AeroDataServiceI;
import mil.nga.types.AeroDataType;

/**
 * Class following both the Singleton and Factory creation pattern used to
 * create the concrete implementation of the classes used to ensure that the 
 * back end data store is kept synchronized.
 * 
 * @author L. Craig Carpenter
 */
public class AeroDataFactory {

	/**
	 * Set up the LogBack system for use throughout the class
	 */		
	static final Logger LOGGER = LoggerFactory.getLogger(
			AeroDataFactory.class);
	
	/**
	 * Private constructor enforcing the singleton design pattern.
	 */
	private AeroDataFactory() { }
	
	/**
     * Private method used to obtain a reference to the target EJB. 
     *  
     * @return Reference to the JDBCUPGDataStore EJB.
     */
	private AeroDataServiceI getUPGDataService() {
		return EJBClientUtilities
				.getInstance()
				.getUPGDataService();
	}
	
	/**
     * Private method used to obtain a reference to the target EJB.  
     * 
     * @return Reference to the JDBCJEPPDataService EJB.
     */
	private AeroDataServiceI getJEPPDataService() {
		return EJBClientUtilities
				.getInstance()
				.getJEPPDataService();
	}
	
	/**
	 * Method constructing the requested concrete implementations of the
	 * objects implementing the AeroDataServiceI interface. 
	 * 
	 * @param type The AeroDataType we want to persist.
	 * @return A concrete implementation that will be used to keep the back-end
	 * data stores updated.
	 */
	public AeroDataServiceI construct(AeroDataType type) {
		
		AeroDataServiceI dataService = null;
		
		if (type != null) {
			switch(type) {
				case UPG:
					dataService = getUPGDataService();
					break;
				case JEPP:
					dataService = getJEPPDataService();
					break;
				default:
					LOGGER.warn("Unsupported data type requested.  Client "
							+ "requested data service associated with data "
							+ "type [ "
							+ type.getText()
							+ " ] which is not implemented.  Returned data "
							+ "service reference will be null.");
					break;
			}
		}
		else {
			LOGGER.error("Null data type requested.  Returned data store will "
					+ "also be null.");
		}
		return dataService;
	}
	
	/**
	 * Accessor method for the singleton instance of the AeroDataStoreFactory 
	 * class.
	 * 
	 * @return The singleton instance of the AeroDataStoreFactory class.
	 */
	public static AeroDataFactory getInstance() {
		return AeroDataFactoryHolder.getSingleton();
	}	
	
	/**
	 * Static inner class used to construct the Singleton object.  This class
	 * exploits the fact that classes are not loaded until they are referenced
	 * therefore enforcing thread safety without the performance hit imposed
	 * by the <code>synchronized</code> keyword.
	 * 
	 * @author L. Craig Carpenter
	 */
	public static class AeroDataFactoryHolder {
		
		/**
		 * Reference to the Singleton instance of the ClientUtility
		 */
		private static AeroDataFactory _instance = new AeroDataFactory();
	
		/**
		 * Accessor method for the singleton instance of the ClientUtility.
		 * @return The Singleton instance of the client utility.
		 */
	    public static AeroDataFactory getSingleton() {
	    	return _instance;
	    }
	    
	}
}

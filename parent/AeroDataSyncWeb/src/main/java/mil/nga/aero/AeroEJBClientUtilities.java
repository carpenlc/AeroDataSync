package mil.nga.aero;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.aero.interfaces.AeroDataMetricsStoreI;
import mil.nga.aero.interfaces.AeroDataStoreI;
import mil.nga.aero.upg.model.Metrics;
import mil.nga.aero.upg.model.UPGData;
import mil.nga.types.AeroDataType;

public class AeroEJBClientUtilities {

    /**
     * Static logger for use throughout the class.
     */
    static final Logger LOGGER = 
            LoggerFactory.getLogger(AeroEJBClientUtilities.class);
	
    /**
     * The type of Aeronautical data to display.
     */
    private AeroDataType type;
    
    /**
     * Constructor requiring clients to supply the type of aero data that
     * we're interested in displaying
     */
    public AeroEJBClientUtilities(AeroDataType type) {
    	this.type = type;
    }
    
    /**
     * Method used to look up the appropriate bean used to interface with 
     * the correct data store.
     * 
     * @return The target bean implementing the AeroDataStoreI interface.
     */
    protected AeroDataStoreI getAeroDataStore() {
    	AeroDataStoreI service = null;
    	switch (this.type) {
    		case JEPP:
    			service = EJBClientUtilities
    						.getInstance()
    						.getJDBCJEPPDataService();
    		case UPG:
    			service = EJBClientUtilities
    						.getInstance()
    						.getJDBCUPGDataService();
    		default:
    			LOGGER.error("Unknown Aero type requested.  Returned service "
    					+ "will be null.");
    	}
    	return service;

    }
    
    /**
     * Method used to look up the appropriate bean used to interface with 
     * the correct metrics data store.
     * 
     * @return The target bean implementing the AeroDataMetricsStoreI 
     * interface.
     */
    protected AeroDataMetricsStoreI getAeroDataMetricsStore() {
    	AeroDataMetricsStoreI service = null;
    	switch (this.type) {
    		case JEPP:
    			service = EJBClientUtilities
    						.getInstance()
    						.getJDBCJEPPMetricsService();
    		case UPG:
    			service = EJBClientUtilities
    						.getInstance()
    						.getJDBCUPGMetricsService();
    		default:
    			LOGGER.error("Unknown Aero type requested.  Returned service "
    					+ "will be null.");
    	}
    	return service;
    }
    
    /**
     * Utilize the EJB session beans to look up the entire list of products.
     * 
     * @return The list of available products.
     */
    protected List<UPGData> loadAllProducts() {
        List<UPGData> products = null;
        if (getAeroDataStore() != null) {
        	Map<String, UPGData> map = getAeroDataStore().getData();
        	if ((map != null) && (map.keySet().size() > 0)) {
        		products = new ArrayList<UPGData>(map.values());
        		if (products != null) {
        			LOGGER.debug("Loaded [ "
        					+ products.size()
        					+ " ] products of type [ "
        					+ type.getText()
        					+ " ] from the data store.");
        		}
        	}
        	else {
        		LOGGER.error("No records returned by the database call.");
        	}
        }
        else {
            LOGGER.error("Unable to obtain a reference to the target EJB.  "
                    + "The returned list of products will be empty.");
        }
        return products;
    }
    
    /**
     * Utilize the EJB session beans to look up the list of distinct ICAO 
     * codes.
     * 
     * @return The list of ICAO codes.
     */
    protected List<String> loadICAOs() {
        List<String> icaos = null;
        if (getAeroDataStore() != null) {
        	icaos = getAeroDataStore().getICAOList();
        	if (icaos != null) {
        		LOGGER.debug("Loaded [ "
        				+ icaos.size()
        				+ " ] distinct ICAO codes from the data "
        				+ "store.");
        	}
        	else {
        		LOGGER.error("No records returned by the database call.");
        	}
        }
        else {
            LOGGER.error("Unable to obtain a reference to the target EJB.  "
                    + "The returned list of ICAOs will be empty.");
        }
        return icaos;
    }
    
    /**
     * Utilize the EJB session beans to look up a finite number of 
     * synchronization runs.
     * 
     * @return The list of available products.
     */
    protected List<Metrics> loadMetrics() {
    	List<Metrics> metrics = null;
        if (getAeroDataMetricsStore() != null) {
        	metrics = getAeroDataMetricsStore().select();
        	if (metrics != null) {
        		LOGGER.debug("Loaded [ "
        				+ metrics.size()
        				+ " ] distinct synchronization events from the data "
        				+ "store.");
        	}
        	else {
        		LOGGER.error("No records returned by the database call.");
        	}
        }
        else {
            LOGGER.error("Unable to obtain a reference to the target EJB.  "
                    + "The returned list of metrics will be empty.");
        }
        return metrics;
    }
    
    /**
     * Utilize the EJB session beans to look up the entire list of products.
     * 
     * @return The list of available products.
     */
    protected List<String> loadTypes() {
        List<String> types = null;
        if (getAeroDataStore() != null) {
        	types = getAeroDataStore().getTypeList();
        	if (types != null) {
        		LOGGER.debug("Loaded [ "
        				+ types.size()
        				+ " ] distinct TYPE definitions from the data "
        				+ "store.");
        	}
        	else {
        		LOGGER.error("No records returned by the database call.");
        	}
        }
        else {
            LOGGER.error("Unable to obtain a reference to the target EJB.  "
                    + "The returned list of types will be empty.");
        }
        return types;
    }
}

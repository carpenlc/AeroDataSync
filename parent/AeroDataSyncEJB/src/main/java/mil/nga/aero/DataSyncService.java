package mil.nga.aero;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.PropertyLoader;
import mil.nga.aero.interfaces.AeroDataConstants;
import mil.nga.aero.interfaces.AeroDataMetricsStoreI;
import mil.nga.aero.interfaces.AeroDataServiceI;
import mil.nga.aero.interfaces.AeroDataStoreI;
import mil.nga.aero.interfaces.AeroDataUpdateServiceI;
import mil.nga.aero.upg.exceptions.ErrorCodes;
import mil.nga.aero.upg.exceptions.UPGDataException;
import mil.nga.aero.upg.model.IntermediateUPGData;
import mil.nga.aero.upg.model.Metrics;
import mil.nga.aero.upg.model.Metrics.MetricsBuilder;
import mil.nga.aero.upg.model.RawUPGData;
import mil.nga.aero.upg.model.UPGData;
import mil.nga.aero.upg.model.UPGDataSetOperations;
import mil.nga.aero.upg.model.ProductBuilder;
import mil.nga.types.AeroDataType;
import mil.nga.util.FileUtils;

/**
 * Session Bean implementation class UPGDataSyncService
 * 
 * The class encapsulates the logic required to determine what products
 * need to be added, remove, and/or updated in the local holdings to ensure 
 * that the local holdings are synchronized with the source holdings.
 */
@Stateless
@LocalBean
public class DataSyncService 
        extends PropertyLoader 
        implements Serializable, AeroDataConstants {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 634298521644859506L;

    /**
     * Set up the logging system for use throughout the class
     */        
    private static final Logger LOGGER = LoggerFactory.getLogger(
            DataSyncService.class);
    
    /**
     * The data type to synchronize.  The construction of handles to all
     * of the required EJBs is based on the value for the data type.
     */
    private AeroDataType type;
    
    /**
     * Internal handle to the interface responsible for retrieving the 
     * holdings from the source.
     */
    private AeroDataServiceI dataService;
    
    /**
     * Internal handle to the interface responsible for updating the 
     * local holdings from the source.
     */
    private AeroDataUpdateServiceI dataUpdateService;
    
    /**
     * Internal handle to the interface responsible for updating the 
     * local holdings from the source.
     */
    private AeroDataStoreI dataStoreService;
    
    /**
     * Internal handle to the interface responsible for updating the 
     * metrics information for the synchronization operation.
     */
    private AeroDataMetricsStoreI metricsStoreService;
    
    /**
     * Handle to the MetricsBuilder object used to track metrics associated 
     * with the synchronization run.
     */
    private MetricsBuilder metricsBuilder;
    
    /**
     * Default constructor. 
     */
    public DataSyncService() {
        super(PROPERTIES_FILE);
    }
    
    /**
     * Getter method for the AeroDataServiceI interface.
     * @return Reference to the proper AeroDataServiceI interface.
     */
    private AeroDataServiceI getAeroDataService() {
        if (dataService == null) {
            dataService = AeroDataFactory.getInstance()
                    .construct(getType());
        }
        return dataService;
    }
    
    /**
     * Getter method for the AeroDataStoreI interface.
     * @param type The aeronautical data type to synchronize.
     * @return Reference to the proper AeroDataStoreI interface.
     */
    private AeroDataStoreI getAeroDataStoreService() {
        if (dataStoreService == null) {
            dataStoreService = AeroDataStoreFactory.getInstance()
                .construct(getType());
        }
        return dataStoreService;
    }
    
    /**
     * Getter method for the AeroDataUpdateServiceI interface.
     * @return Reference to the proper AeroDataUpdateServiceI interface.
     */
    private AeroDataUpdateServiceI getAeroDataUpdateService() {
        if (dataUpdateService == null) {
            dataUpdateService = AeroDataUpdateFactory.getInstance()
                .construct(getType());
        }
        return dataUpdateService;
    }
   
    /**
     * Getter method for the AeroDataServiceI interface.
     * @return Reference to the proper AeroDataServiceI interface.
     */
    private AeroDataMetricsStoreI getAeroMetricsService() {
        if (metricsStoreService == null) {
            metricsStoreService = AeroMetricsStoreFactory.getInstance()
                    .construct(getType());
        }
        return metricsStoreService;
    }
    
    /**
     * Getter method for the aero data type to synchronize.
     * @return The aeronautical data type.
     */
    private AeroDataType getType() {
        return this.type;
    }
    
    /**
     * This method will return a list of products that exist in the data 
     * retrieved from the UPG data source but do not exist in the current 
     * NGA holdings.  These are the products that we need to add to the 
     * NGA holdings.
     * 
     * @param intermediate List of products downloaded from the UPG data 
     * source. 
     * @param holdings The NGA holdings.
     * @return products that need to be added to the NGA holdings.
     */
    private List<String> getProductsToAdd(
            Map<String, IntermediateUPGData> intermediate, 
            Map<String, UPGData>             holdings) {
        return UPGDataSetOperations.getInstance()
                        .subtract(
                                intermediate.keySet(), 
                                holdings.keySet());
    }
    
    /**
     * This method will return a list of products that exist in the NGA  
     * holdings but no longer exist at the UPG data source.  These are the 
     * products that we need to remove from local holdings.  
     * 
     * @param intermediate List of products downloaded from the UPG data 
     * source. 
     * @param holdings The NGA holdings.
     * @return products that need to be removed from the NGA holdings.
     */
    private List<String> getProductsToRemove(
            Map<String, IntermediateUPGData> intermediate, 
            Map<String, UPGData>             holdings) {
        return UPGDataSetOperations.getInstance()
                    .subtract(
                            holdings.keySet(),
                            intermediate.keySet());
    }
    
    /**
     * This method will return a list of products that exist in the NGA  
     * holdings but need to be updated.  We are going to assume that the 
     * last modified date is accurate.    
     * 
     * @param intermediate List of products downloaded from the UPG data 
     * source. 
     * @param holdings The NGA holdings.
     * @return products that need to be removed from the NGA holdings.
     */
    private List<String> getProductsToUpdate(
            Map<String, IntermediateUPGData> intermediate, 
            Map<String, UPGData>             holdings) { 
        
        List<String> products = new ArrayList<String>();
           List<String> intersection = UPGDataSetOperations.getInstance()
                    .intersection(
                            intermediate.keySet(), 
                            holdings.keySet());

        if ((intersection != null) && (intersection.size() > 0)) {
            for (String UUID : intersection) {
                
                IntermediateUPGData sourceHoldings = intermediate.get(UUID);
                UPGData             localHoldings  = holdings.get(UUID);
                
                if (getAeroDataUpdateService()
                        .isUpdateRequired(
                                sourceHoldings, 
                                localHoldings)) { 
                    products.add(UUID);
                }

            }
        }
        else {
            LOGGER.warn("The calculation of the intersection between source "
                    + "holdings and local holdings resulted in [ 0 ] "
                    + "elements.  This is only valid if we're completely "
                    + "re-generating the NGA holdings.");
        }
        return products;
    }
    
    
    /**
     * Ensure that the metrics are initialized before proceeding with the 
     * synchronization process.
     */
    private void initializeMetrics() {
        metricsBuilder = new Metrics.MetricsBuilder();
        metricsBuilder.initialize();
        metricsBuilder.executionTime(
                new java.sql.Date((new java.util.Date()).getTime()));
        metricsBuilder.hostName(FileUtils.getHostName());
        metricsBuilder.jvmName(
                EJBClientUtilities.getInstance().getServerName());
    }
        
    
    /**
     * Add products to the NGA holdings.  
     * 
     * @param intermediate List of products downloaded from the UPG data 
     * source. 
     * @param intermediate The NGA holdings.
     * @return The number of new products added.
     */
    public int addProducts(
            List<String> products, 
            Map<String, IntermediateUPGData> intermediate) {
        
        int counter = 0;
        
        if ((products != null) && (products.size() > 0)) { 
            
            LOGGER.info("Adding [ "
                    + products.size()
                    + " ] products to the local holdings.");
            
            for (String uuid : products) {
                try {
                    getAeroDataUpdateService().add(intermediate.get(uuid));
                    counter++;
                }
                catch (UPGDataException upde) {
                    
                }
            }
        }
        else {
            LOGGER.info("NGA Holdings : No products to add.");
        }
        metricsBuilder.added(counter);
        return counter;
    }
    
    /**
     * Remove deprecated products from the NGA holdings. 
     * 
     * @param intermediate List of products downloaded from the UPG data 
     * source. 
     * @param intermediate The NGA holdings.
     * @return The number of old products to remove.
     */
    public int removeProducts(
            List<String> products, 
            Map<String, UPGData> data) throws UPGDataException {
        
        int counter = 0;
        
        if ((products != null) && (products.size() > 0)) {
            
            LOGGER.info("Removing [ "
                    + products.size()
                    + " ] products from the local holdings.");
            
            if ((data != null) && (data.size() > 0)) {
                for (String uuid : products) {
                    
                    LOGGER.info("UUID [ "
                            + uuid
                            + " ] no longer exists in the source "
                            + "holdings and will be removed.");
                    
                    getAeroDataUpdateService()
                        .remove(data.get(uuid));
                    counter++;
                }
            }
        }
        else {
            LOGGER.info("NGA Holdings : No products to remove.");
        }
        metricsBuilder.removed(counter);
        return counter;
    }
        
    /**
     * Update existing products in the local holdings. 
     * 
     * @param intermediate List of products downloaded from the UPG data 
     * source. 
     * @return The number of products that have been updated since the last run.
     */
    public int updateProducts(
            List<String> products, 
            Map<String, IntermediateUPGData> data) throws UPGDataException {
        
        int counter = 0;

        if ((products != null) && (products.size() > 0)) {
            
            LOGGER.info("Updating [ "
                    + products.size()
                    + " ] products in the local holdings.");
            
            if ((data != null) && (data.size() > 0)) {
                for (String uuid : products) {
                    
                    LOGGER.info("UUID [ "
                            + uuid
                            + " ] is out of date and will be updated.");
                    
                    getAeroDataUpdateService()
                        .update(data.get(uuid));
                    counter++;
                }
            }
        }
        else {
            LOGGER.info("NGA Holdings : No products to update.");
        }
        metricsBuilder.updated(counter);
        return counter;
    }
    

    /**
     * Setter method for the aero data type so synchronize.
     * @param type
     */
    private void setType(AeroDataType type) {
        this.type = type;
    }
    
    /**
     * This is what amounts to the "main" method of the Aero data 
     * synchronization process.  
     * 
     * @param type The data type that we are synchronizing.
     * @throws UPGDataException Thrown for a variety of reasons.
     */
    public void synchronize(AeroDataType type) throws UPGDataException {
        
        long                             startTime = System.currentTimeMillis();
        RawUPGData                       rawData   = null;
        Metrics                          metrics   = null;
        Map<String, IntermediateUPGData> intermediate = null;
        Map<String, UPGData>             localHoldings  = null;
        
        // Save the type for use throughout the synchronization process.
        setType(type);
        LOGGER.info("Beginning data synchronization process for type [ "
                + getType()
                + " ].");
        
        initializeMetrics();

        // Make sure we can look up all of the required object handles.
        if (getAeroDataUpdateService() == null) {
            LOGGER.error("Unable to obtain a reference to [ " 
                    + "AeroDataUpdateServiceI"
                    + " ].  Synchronization operation cannot "
                    + "proceed.");
            throw new UPGDataException(ErrorCodes.APPLICATION_EXCEPTION);           
        }
        if (getAeroMetricsService() == null) {
            LOGGER.error("Unable to obtain a reference to [ " 
                    + "AeroDataMetricsServiceI"
                    + " ].  Synchronization operation cannot "
                    + "proceed.");
            throw new UPGDataException(ErrorCodes.APPLICATION_EXCEPTION);            
        }
        if (getAeroDataService() == null) {
            LOGGER.error("Unable to obtain a reference to [ " 
                    + "AeroDataServiceI"
                    + " ].  Synchronization operation cannot "
                    + "proceed.");
            throw new UPGDataException(ErrorCodes.APPLICATION_EXCEPTION);
        }
        if (getAeroDataStoreService() == null) {
            LOGGER.error("Unable to obtain a reference to [ " 
                    + "AeroDataStoreServiceI"
                    + " ].  Synchronization operation cannot "
                    + "proceed.");
            throw new UPGDataException(ErrorCodes.APPLICATION_EXCEPTION);
        }
            
        // Get the raw product data from the provider.
        rawData = getAeroDataService().getRawData();
        if ((rawData != null) && 
                (rawData.getData().size() > 0)) { 
                    
            // Convert the raw product data to an intermediate format for 
            // processing.
            intermediate = new ProductBuilder(getType()).buildMap(rawData); 
            if ((intermediate != null) && 
                    (intermediate.entrySet().size() > 0)) { 
                
                // Get the current local holdings.
                localHoldings = getAeroDataStoreService().getData();
                if ((localHoldings != null) && 
                                (localHoldings.size() > 0)) {
                    
                    // Update metrics.
                    metricsBuilder.sourceHoldings(intermediate.entrySet().size());
                    metricsBuilder.localHoldings(localHoldings.size());
                    
                    addProducts(
                            getProductsToAdd(
                                    intermediate, 
                                    localHoldings),
                            intermediate);
                            
                    updateProducts(
                            getProductsToUpdate(
                                    intermediate, 
                                    localHoldings),
                            intermediate);
                    
                    removeProducts(
                            getProductsToRemove(
                                    intermediate, 
                                    localHoldings),
                            localHoldings);
                }
                else {
                    
                    LOGGER.error("Unable to retrieve the local "
                            + "holdings for type [ "
                            + type.getText()
                            + " ] from the target data store.");
                    throw new UPGDataException(
                            ErrorCodes.DATA_SOURCE_EXCEPTION);
                }
            }
            else {
                LOGGER.error("Unable to convert raw data to its "
                        + "intermediate format for type [ "
                        + type.getText()
                        + " ].");
                throw new UPGDataException(
                        ErrorCodes.APPLICATION_EXCEPTION);
            }        
        }
        else {
            LOGGER.error("Unable to obtain the required information on "
                    + "source holdings for data type [ "
                    + type.getText()
                    + " ].  Synchronization operation cannot proceed.");
            throw new UPGDataException(ErrorCodes.NO_DATA_RETRIEVED);
        }

        try {
            
            // Update and store the metrics data.
            metricsBuilder.failedDownloads(
                    getAeroDataStoreService().getNumFailedDownloads());
            metricsBuilder.elapsedTime(System.currentTimeMillis() - startTime);
            metrics = metricsBuilder.build();
            LOGGER.info("Synchronization for data type [ "
                    + type.getText()
                    + " ] complete.  Metrics => [ "
                    + metrics.toString()
                    + " ].");
            getAeroMetricsService().insert(metrics);
            
        }
        catch (IllegalStateException ise) {
            LOGGER.error("Unexpected IllegalStateException raised while "
                    + "constructing the Metrics object.  Error message [ " 
                    + ise.getMessage()
                    + " ].");
        }
    } 
}

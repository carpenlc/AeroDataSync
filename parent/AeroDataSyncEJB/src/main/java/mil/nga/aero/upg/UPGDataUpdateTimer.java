package mil.nga.aero.upg;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timer;

import mil.nga.aero.DataSyncService;
import mil.nga.aero.EJBClientUtilities;
import mil.nga.aero.upg.exceptions.UPGDataException;
import mil.nga.types.AeroDataType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Singleton
public class UPGDataUpdateTimer {

    /**
     * Set up the logging system for use throughout the class
     */        
    private static final Logger LOGGER = LoggerFactory.getLogger(
            UPGDataUpdateTimer.class);
    
    /** 
     * Format associated with dates incoming from the target UPG data source.
     */
    private static final String DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
            
    /**
     * DateFormat object used to convert the String based last-update date 
     * retrieved from the target data store.
     */
    private static final SimpleDateFormat formatter = 
                new SimpleDateFormat(DATE_FORMAT_STRING);
    
    /**
     * Container-injected reference to the UPGDataService session bean.
     */
    @EJB
    DataSyncService dataSyncService;
    
    /**
     * Default constructor. 
     */
    public UPGDataUpdateTimer() { }
    
    /**
     * Private method used to obtain a reference to the target EJB.  
     * 
     * @return Reference to the UPGDataService EJB.
     */
    private DataSyncService getDataSyncService() {
        if (dataSyncService == null) {
            
            LOGGER.warn("Application container failed to inject the "
                    + "reference to DataSyncService.  Attempting to "
                    + "look it up via JNDI.");
            dataSyncService = EJBClientUtilities
                    .getInstance()
                    .getDataSyncService();
        
        }
        return dataSyncService;
    }
    
    public void retry() {
        
    }
    
    /**
     * Entry point called by the application container to invoke the 
     * UPG data synchronization process.
     * 
     * @param t Container injected Timer object.
     */
    @Schedule(second="0", minute="0", hour="*/2", dayOfWeek="*",
    dayOfMonth="*", month="*", year="*", info="UPGUpdateTimer")
    private void scheduledTimeout(final Timer t) {
        
    	
    	LOGGER.info("Per direction from the Aero office, the UPG "
    			+ "synchronization process has been disabled due "
    			+ "due to changes at the data provider (Leidos).");
//        LOGGER.info("UPG data synchronization service launched at [ "
//                + formatter.format(new Date(System.currentTimeMillis()))
//                + " ].");
//        
//        try {
//            if (getDataSyncService() != null) {
//                getDataSyncService().synchronize(AeroDataType.UPG);
//            }
//            else {
//                LOGGER.error("Application error encountered!  Container " 
//                        + "failed to inject the required EJB references.");
//            }
//        }
//        catch (UPGDataException ude) {
//            // Send an e-mail?
//            ude.printStackTrace();
//        }
        
    }
    
}
package mil.nga.aero;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.aero.upg.exceptions.UPGDataException;
import mil.nga.types.AeroDataType;
import mil.nga.util.FileUtils;

@Path("")
public class AeroDataSync {

    /**
     * Set up the Log4j system for use throughout the class
     */
    static final Logger LOGGER = LoggerFactory.getLogger(AeroDataSync.class);
    
    /**
     * The name of the application
     */
    public static final String APPLICATION_NAME = "UPGDownload";
    
    @EJB
    DataSyncService syncService;
    
    /**
     * Private method used to obtain a reference to the target EJB.  
     * 
     * @return Reference to the JDBCUPGDataServices EJB.
     */
    private DataSyncService getSyncService() {
        if (syncService == null) {
            
            LOGGER.warn("Application container failed to inject the "
                    + "reference to UPGDataService.  Attempting to "
                    + "look it up via JNDI.");
            syncService = EJBClientUtilities
                    .getInstance()
                    .getDataSyncService();
        }
        return syncService;
    }
    
    /**
     * Simple method used to determine whether or not the bundler 
     * application is responding to requests.
     */
    @GET
    @Path("/isAlive")
    public Response isAlive(@Context HttpHeaders headers) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("Application [ ");
        sb.append(APPLICATION_NAME);
        sb.append(" ] on host [ ");
        sb.append(FileUtils.getHostName());
        sb.append(" ] running in JVM [ ");
        sb.append(EJBClientUtilities.getInstance().getServerName());
        sb.append(" ].");
        
        return Response.status(Status.OK).entity(sb.toString()).build();
            
    }
    
    @GET
    @Path("/startJEPP")
    public String startSyncJEPP() {
        
        try {
            if (getSyncService() != null) {
                
                getSyncService().synchronize(AeroDataType.JEPP);
                
            }
            else {
                return "Unable to look up sync service!";
            }
        }
        catch (UPGDataException ude) {
            return ude.getMessageText();
        }
        return "Done!";
        
    }
    
//    @GET
//    @Path("/startUPG")
//    public String startSyncUPG() {
//        
//        try {
//            if (getSyncService() != null) {
//                
//                getSyncService().synchronize(AeroDataType.UPG);
//                
//            }
//            else {
//                return "Unable to look up sync service!";
//            }
//        }
//        catch (UPGDataException ude) {
//            return ude.getMessageText();
//        }
//        return "Done!";
//        
//    }
}

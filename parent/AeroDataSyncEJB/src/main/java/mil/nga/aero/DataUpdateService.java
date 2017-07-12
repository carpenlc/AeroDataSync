package mil.nga.aero;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.ejb.EJB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.PropertyLoader;
import mil.nga.aero.interfaces.AeroDataConstants;
import mil.nga.aero.interfaces.AeroDataStoreI;
import mil.nga.aero.upg.exceptions.ErrorCodes;
import mil.nga.aero.upg.exceptions.UPGDataException;
import mil.nga.aero.upg.model.IntermediateUPGData;
import mil.nga.aero.upg.model.ProductBuilder;
import mil.nga.aero.upg.model.UPGData;
import mil.nga.types.AeroDataType;
import mil.nga.types.HashType;
import mil.nga.util.FileUtils;

/**
 * Session Bean implementation class DataUpdateService
 */
public abstract class DataUpdateService 
        extends PropertyLoader 
        implements AeroDataConstants, Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 630021577415303498L;
    
    /**
     * Set up the Log4j system for use throughout the class
     */        
    private static final Logger LOGGER = LoggerFactory.getLogger(
            DataUpdateService.class);
    
    /**
     * Container-injected reference to the DataService session bean.
     */
    @EJB
    DataService dataService;
    
    /**
     * Container-injected reference to the HashGeneratorService session bean.
     */
    @EJB
    HashGeneratorService hashGeneratorService;
    
    /**
     * Default constructor. 
     */
    public DataUpdateService() { 
        super(PROPERTIES_FILE);
    }
    
    /**
     * Simple method to determine whether or not the input file exists.
     * 
     * @param path The full path to the target file.
     * @return True if the file exists, false otherwise.
     */
    private boolean fileExists(String path) {
        
        boolean exists = false;
        
        if ((path != null) && (!path.isEmpty())) {
            File f = new File(path);
            exists = f.exists();
        }
        
        return exists;
    }
    
    /**
     * Private method used to obtain a reference to the target EJB. 
     * This was added in case the application is deployed to JBoss  
     * EAP 6.3 or earlier which periodically had problems with bean 
     * injection.
     * 
     * @return Reference to the DataService EJB.
     */
    protected DataService getDataService() {
        
        if (dataService == null) {
            
            LOGGER.warn("Application container failed to inject the "
                    + "reference to the DataService bean.  Attempting to "
                    + "look it up via JNDI.");
            dataService = EJBClientUtilities
                    .getInstance()
                    .getDataService();
            
        }
        return dataService;
    }
    
    /**
     * Private method used to obtain a reference to the target EJB. 
     * This was added in case the application is deployed to JBoss  
     * EAP 6.3 or earlier which periodically had problems with bean 
     * injection.
     * 
     * @return Reference to the UPGDataService EJB.
     */
    private HashGeneratorService getHashGeneratorService() {
        
        if (hashGeneratorService == null) {
            
            LOGGER.warn("Application container failed to inject the "
                    + "reference to UPGDataService.  Attempting to "
                    + "look it up via JNDI.");
            hashGeneratorService = EJBClientUtilities
                    .getInstance()
                    .getHashGeneratorService();
        
        }
        return hashGeneratorService;
    }
    
    /**
     * This method calculates the final destination directory where the 
     * updated UPG data record will reside. The temp file will be moved to 
     * this location.
     * 
     * @param data The "raw" UPG data record.
     * @return The final on-disk location for the UPG data.
     * @throws UPGDataException Thrown if the required system properties are
     * not loaded.
     */
    private String getFinalDestination( 
            String icao,
            String type) throws UPGDataException, IOException  {
        
        StringBuilder sb = new StringBuilder();

        sb.append(getBaseDirectory());
        FileUtils.mkdir(sb.toString());
        if (!sb.toString().endsWith(File.separator)) {
            sb.append(File.separator);
        }
        sb.append(icao.trim());
        FileUtils.mkdir(sb.toString());
        sb.append(File.separator);
        sb.append(type.trim());
        FileUtils.mkdir(sb.toString());
        sb.append(File.separator);

        return sb.toString();
    }
    
    /**
     * This method calculates the final destination directory where the 
     * updated UPG data record will reside. The temp file will be moved to 
     * this location.
     * 
     * @param data The "raw" UPG data record.
     * @return The final on-disk location for the UPG data.
     * @throws UPGDataException Thrown if the required system properties are
     * not loaded.
     */
    private String getFinalDestinationFilename( 
            String icao,
            String type,
            String filename) throws UPGDataException, IOException  {
        
        StringBuilder sb = new StringBuilder();

        sb.append(getBaseDirectory());
        FileUtils.mkdir(sb.toString());
        if (!sb.toString().endsWith(File.separator)) {
            sb.append(File.separator);
        }
        sb.append(icao.trim());
        FileUtils.mkdir(sb.toString());
        sb.append(File.separator);
        sb.append(type.trim());
        FileUtils.mkdir(sb.toString());
        sb.append(File.separator);
        sb.append(filename.trim());

        return sb.toString();
    }
    
    /**
     * This method requires a "raw" UPGData object (i.e. containing the data 
     * that was provided by the UPG data source (i.e. Leidos).  This method 
     * then retrieves the target file from the source holdings, checks the 
     * hash values, then moves it to it's final location.  
     * 
     * @param data A "raw" UPGDate object as retrieved from the UPG data 
     * source.
     * @return boolean indicating success or failure of the download.
     */
    public boolean getProduct(
            String uuid,
            String icao,
            String type, 
            String filename,
            String hash,
            String sourceFile) throws UPGDataException {
        
        boolean success = false;
            
        try {
            
            String tmpDestination = getTempDestination (
                    icao, type, filename);
            String finalDestination = getFinalDestinationFilename (
                    icao, type, filename);
            
            if ((getDataService() != null) 
                    && (getHashGeneratorService() != null)) {
                
                // Retrieve the target UPG file from Leidos 
                success = getDataService().getProductFile(
                        sourceFile, 
                        tmpDestination);
                
                if (success) {
                    
                    // Check to ensure the hashes match.
                    if (getHashGeneratorService().checkHash(
                                tmpDestination,
                                hash,
                                HashType.MD5)) {
                        
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Successfully downloaded and "
                                    + "validated product [ "
                                    + uuid
                                    + " ] moving temp file [ "
                                    + tmpDestination
                                    + " ] to final destination [ "
                                    + finalDestination
                                    + " ].");
                        }
                        
                        // Move the file from it's temporary location to
                        // it's final destination.
                        FileUtils.move(tmpDestination, finalDestination);

                        // Get rid of the temporary destination directory
                        removeTempDestination(icao);
                        
                    }
                    else {
                        LOGGER.warn("Hash check for file downloaded in "
                                + "association with UUID [ "
                                + uuid
                                + " ] failed.  Setting the download "
                                + "failed flag so it can be re-tried "
                                + "later.");
                        success = false;
                       
                        // Add a searchable error message for the product in 
                        // question.
                        LOGGER.error(
                                "Error processing product UUID => [ "
                                + uuid
                                + " ], ICAO => [ "
                                + icao
                                + " ], TYPE => [ "
                                + type
                                + " ], FILENAME => [ "
                                + filename
                                + " ], SOURCE HASH => [ "
                                + hash
                                + " ], expected hash => [ "
                                + getHashGeneratorService().getHash(tmpDestination, HashType.MD5)
                                + " ].  "
                                + "REASON: Hash values do not match.");
                        
                        // Still need to get rid of the temporary directory.
                        removeTempDestination(icao);
                    }
                }
                else {
                    LOGGER.warn("Download of UPG product file for "
                            + "UUID [ "
                            + uuid
                            + " ] failed.  Setting the download "
                            + "failed flag so it can be re-tried "
                            + "later.");
                    
                    success = false;
                    
                    // Add the current product information to the error report.
                    // Add a searchable error message for the product in 
                    // question.
                    LOGGER.error(
                            "Error processing product UUID => [ "
                            + uuid
                            + " ], ICAO => [ "
                            + icao
                            + " ], TYPE => [ "
                            + type
                            + " ], FILENAME => [ "
                            + filename
                            + " ], SOURCE HASH => [ "
                            + hash
                            + " ].  "
                            + "REASON: Unable to download.");
                }
            }
            else {
                LOGGER.error("Unable to obtain references to the required "
                        + "EJBs.  UPG product will not be downloaded");
                throw new UPGDataException(ErrorCodes.APPLICATION_EXCEPTION);
            }
        }
        catch (IOException ioe) {
            LOGGER.error("Unexpected IOException encountered while "
                    + "interacting with the file system.  Error message [ "
                    + ioe.getMessage()
                    + " ].");
            throw new UPGDataException(ErrorCodes.FILESYSTEM_EXCEPTION);
        }
        return success;
    }
    
    /**
     * This method calculates the temporary download location.  Once the file
     * is downloaded and validated, it will be moved to the final destination 
     * directory.  This method is used by both the data add and update 
     * processes.
     * 
     * @param data The "raw" UPG data record.
     * @return The temporary download location for the data.
     */
    public String getTempDestination(
            String icao,
            String type,
            String filename) throws IOException {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(System.getProperty("java.io.tmpdir"));
        sb.append(File.separator);
        sb.append(getDataType().getText());
        FileUtils.mkdir(sb.toString());
        sb.append(File.separator);
        sb.append(icao.trim());
        FileUtils.mkdir(sb.toString());
        sb.append(File.separator);
        sb.append(type.trim());
        FileUtils.mkdir(sb.toString());
        sb.append(File.separator);
        sb.append(filename.trim());
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Temporary download location [ "
                    + sb.toString()
                    + " ].");
        }
        
        return sb.toString();
    }
    
    /**
     * Method containing the logic required to remove a product from the 
     * local file system.
     * 
     * @param data The product to remove.
     */
    public void remove(UPGData data) throws UPGDataException {
        if (data != null) {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Removing the following product from the "
                        + "local repository [ "
                        + data.toString()
                        + " ].");
            }
            
            removeFromFilesystem(data); 

            // Delete the associated database record.
            AeroDataStoreI dataStore = 
                    AeroDataStoreFactory.getInstance().construct(getDataType());
            dataStore.deleteData(data.getUUID());
            
        }
        else {
            LOGGER.warn("The input UPG data object was null.  Nothing to "
                    + " process.");
        }
    }
    
    /**
     * Method to determine whether or not the local holdings need to be
     * updated with the source holdings.
     * 
     * @param sourceHolding The source UPG data.
     * @param localHolding The UPG data in the local holdings.
     * @return True if the source data needs to be updated, false otherwise.
     */
    public boolean isUpdateRequired(
            IntermediateUPGData intermediate, 
            UPGData             localHoldings) {
        
        boolean update = false;
        
        try {
            
            // If the download failed flag is set to "failed" (i.e. 0 
            // retry the download.
            if (localHoldings.getDownloadSuccess() == 0) {
                
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Download failed flag is set for data "
                            + "type [ "
                            + getDataType().getText()
                            + " ] and UUID [ "
                            + localHoldings.getUUID()
                            + " ] Product download will be re-tried.");
                }
                update = true;
                
            }
            else if (!(fileExists(
                    getFinalDestinationFilename(
                            localHoldings.getICAO(), 
                            localHoldings.getType(),
                            localHoldings.getFilename())))) {
                
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Expected on-disk file associated with "
                            + "data type [ "
                            + getDataType().getText()
                            + " ] and UUID [ "
                            + localHoldings.getUUID()
                            + " ] does not exist.  Product will be "
                            + "updated.");
                }
                
                update = true;
            }
            
            // Temporary check to make sure that all of the source link data
            // is set in the local repository.  This was done because the 
            // legacy Coldfusion code was not setting the source links 
            // correctly.
            else if ((localHoldings.getSourceLink() == null) || 
                    (localHoldings.getSourceLink().isEmpty())) {
                
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Source link for data type [ "
                            + getDataType().getText()
                            + " ] data not set for "
                            + "UUID [ "
                            + localHoldings.getUUID()
                            + " ] Product will be updated.");
                }
                
                update = true;
            }
            
            // If the last modified date of the source holdings is newer
            // than the local holdings the data should be updated.
            else if (intermediate.getDateLastModified()
                    .after(localHoldings.getDateLastModified())) {
                
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Source holdings for data type [ "
                            + getDataType().getText()
                            + " ] modified date newer "
                            + "than local holdings.  "
                            + "UUID [ "
                            + localHoldings.getUUID()
                            + " ] Product will be updated.");
                }
                
                update = true;
            }
        }
        catch (IOException ioe) {
            LOGGER.warn("Unexpected IOException raised while attempting to "
                    + "calculate the destination filename.  Exception message "
                    + "[ "
                    + ioe.getMessage()
                    + " ].");
        }
        catch (UPGDataException ude) { 
            // Eat the exception here as it is not yet catastrophic.  If it's
            // an issue it will be raised again later.
            LOGGER.warn("Unexpected UPGDataException raised while attempting "
                    + "to calculate the final output filename.");
        }
        return update;
    }
    
    /**
     * This method calculates the temporary download location.  Once the file
     * is downloaded and validated, it will be moved to the final destination 
     * directory.
     * 
     * @param type The aero data type
     * @param icao The ICAO associated with the target file.
     * @return The temporary download location for the data.
     */
    public void removeTempDestination(String icao) throws IOException {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(System.getProperty("java.io.tmpdir"));
        sb.append(File.separator);
        sb.append(getDataType().getText());
        sb.append(File.separator);
        sb.append(icao.trim());
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removing temporary download location [ "
                    + sb.toString()
                    + " ].");
        }

        FileUtils.delete(sb.toString());
    }
    
    /**
     * Remove a given product from the file system.
     * 
     * @param data Object containing information on the product to remove
     * from the filesystem.
     * @throws UPGDataException Thrown if the method cannot obtain the required
     * system properties.
     */
    protected void removeFromFilesystem(UPGData data) throws UPGDataException {
        
        StringBuilder sb = new StringBuilder();
        
        try {
            
            sb.append(getBaseDirectory());
            sb.append(File.separator);
            sb.append(data.getICAO().trim());
            String icaoDir = sb.toString();
            sb.append(File.separator);
            sb.append(data.getType().trim());
            String typeDir = sb.toString();
            sb.append(File.separator);
            sb.append(data.getFilename().trim());
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Removing file [ "
                        + sb.toString() 
                        + " ].");
            }
            
            FileUtils.delete(sb.toString());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Removing directory [ "
                        + typeDir 
                        + " ].");
            }
            
            FileUtils.rmdirIfEmpty(typeDir);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Removing directory [ "
                        + icaoDir 
                        + " ].");
            }
            FileUtils.rmdirIfEmpty(icaoDir);
        }
        catch (IOException ioe) {
            LOGGER.warn("IOException encountered while attempting to delete "
                    + "files associated with UUID [ "
                    + data.getUUID() 
                    + " ].  This could result in orphaned files on disk.");
        }
    }
    
    /**
     * Method containing the logic required to add a new product to the 
     * file system.  
     * 
     * @param product The product to add to the file system.
     * @throws UPGDataException Thrown if exceptions are encountered while 
     * adding the target product to the file system.
     */
    public void add(IntermediateUPGData product) throws UPGDataException {
        
        if (product != null)  { 
            
            LOGGER.info("Adding [ "
                    + product.getUUID()
                    + " ] UUID of type [ "
                    + getDataType().getText()
                    + " ] to local holdings.");
            
            UPGData finalData = 
                    (new ProductBuilder(getDataType())).build(product);
            
            // Retrieve the product from the source.
            if (getProduct(
                    finalData.getUUID(), 
                    finalData.getICAO(),
                    finalData.getType(),
                    finalData.getFilename(),
                    product.getHash(),
                    product.getLink())) { 
                
                finalData.setDownloadSuccess(1);
            }
            else {
                
                // Setting the download flag to zero will result in a retry on 
                // the next product update iteration.
                LOGGER.warn("Product download failed.  Setting download flag "
                        + "to [ 0 ] for UUID [ "
                        + finalData.getUUID()
                        + " ].");
                finalData.setDownloadSuccess(0);
            }

            // Persist the new information
            AeroDataStoreI dataStore = 
                    AeroDataStoreFactory.getInstance().construct(getDataType());
            dataStore.insertData(finalData);    
            
        }
        else {
            
            LOGGER.warn("Input product object for type [ "
                    + getDataType().getText()
                    + " ] is null.  No action taken.");
            
        }
    }
    
    /**
     * Subclasses must provide a method that supplies the location of the local
     * on-disk storage location for the data that will be retrieved from the 
     * source holdings.  
     * 
     * @return The location where the data retrieved from the source holdings 
     * will reside.
     */
    public abstract String getBaseDirectory();
    
    /**
     * Subclasses must provide a method that identifies the data type that is 
     * to be synchronized. 
     * 
     * @return The aeronautical data type that will be synchronized.
     */
    public abstract AeroDataType getDataType();
    
    /**
     * Update the product that already exists on the local system.  
     * 
     * @param product The product to add to the file system.
     * @throws UPGDataException Thrown if exceptions are encountered while 
     * adding the target product to the file system.
     */
    public void update(IntermediateUPGData product) 
            throws UPGDataException {
        
        if (product != null) {
        
            UPGData finalData = 
                    (new ProductBuilder(getDataType())).build(product);
            
            // Retrieve the product from the source.
            if (getProduct(
                    finalData.getUUID(), 
                    finalData.getICAO(),
                    finalData.getType(),
                    finalData.getFilename(),
                    product.getHash(),
                    product.getLink())) { 
                finalData.setDownloadSuccess(1);
            }
            else {
                
                // Setting the download flag to zero will result in a retry on 
                // the next product update iteration.
                LOGGER.warn("Product download failed.  Setting download flag "
                        + "to [ 0 ] for UUID [ "
                        + finalData.getUUID()
                        + " ].");
                finalData.setDownloadSuccess(0);
            }

            // Persist the new information
            AeroDataStoreI dataStore = 
                    AeroDataStoreFactory.getInstance().construct(getDataType());
            dataStore.updateData(finalData);
        }
        else {
            LOGGER.warn("Input product object for type [ "
                    + getDataType().getText()
                    + " ] is null.  No action taken.");
        }
    }
}

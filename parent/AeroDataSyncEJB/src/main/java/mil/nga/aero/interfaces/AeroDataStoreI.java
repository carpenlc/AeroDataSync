package mil.nga.aero.interfaces;

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import mil.nga.aero.upg.model.UPGData;

/**
 * Interface documenting the methods required to keep the back-end data
 * store updated with the latest available data.  This interface is implemented 
 * for each Aero data type that we are going to keep in sync with the 
 * external stores.
 * 
 * @author L. Craig Carpenter
 *
 */
@Remote
public interface AeroDataStoreI {

    /**
     * Delete all records that match the input UUID.
     * 
     * @param uuid The UUID to be deleted.
     */
    public void deleteData(String uuid);
    
    /**
     * Select a UPGData record from the target data store based on the input 
     * UUID.
     * 
     * @param uuid UUID to retrieve. 
     * @return The UPGData record.  Null if not found.
     */
    public UPGData getData(String uuid);
    
    /**
     * Get a list of all UPGData records from the data store.
     * 
     * @return A Map object containing all data records in the 
     * back-end data store.  The key is the UUID and the value is the 
     * UPGData POJO.
     */
    public Map<String, UPGData> getData();
    
    /**
     * Retrieve a complete list of unique TYPE fields from the data store.
     * @return A list of TYPEs
     */
    public List<String> getTypeList();
    
    /**
     * Retrieve a complete list of unique ICAO fields from the data store.
     * @return A list of ICAOs
     */
    public List<String> getICAOList();
    
    /**
     * Select the number of failed downloads from the data store.
     * 
     * @return The number of failed downloads.
     */
    public int getNumFailedDownloads();
    
    /**
     * Select the number of  UPGData records from the data store.
     * 
     * @return The number of rows in the data store.
     */
    public int getNumProducts();
    
    /**
     * Retrieve a complete list of UUIDs from the data store.
     * @return A list of UUIDs
     */
    public List<String> getUUIDList();
    
    /**
     * Persist (insert) the information associated with the input 
     * UPGData object.
     * 
     * @param data UPGData object containing updated state 
     * information.
     */
    public void insertData(UPGData data);

    
    /**
     * Persist (update) the information associated with the input 
     * UPGData object.
     * 
     * @param data UPGData object containing updated state 
     * information.
     */
    public void updateData(UPGData data);
    
}

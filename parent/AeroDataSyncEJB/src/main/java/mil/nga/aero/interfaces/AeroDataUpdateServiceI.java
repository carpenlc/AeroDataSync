package mil.nga.aero.interfaces;

import javax.ejb.Remote;

import mil.nga.aero.upg.exceptions.UPGDataException;
import mil.nga.aero.upg.model.IntermediateUPGData;
import mil.nga.aero.upg.model.UPGData;

/**
 * Interface defining the methods that will need to be implemented by session
 * beans that encapsulate the logic required to add, update, and remove 
 * products from the file system. 
 * 
 * @author L. Craig Carpenter
 */
@Remote
public interface AeroDataUpdateServiceI {

    /**
     * Add the identified product to the local holdings.
     * 
     * @param product Object representing the product to add to local 
     * holdings.
     */
    public void add(IntermediateUPGData product) throws UPGDataException;
    
    /**
     * Remove the identified product from the local holdings.
     * 
     * @param product Object representing the product to remove from local 
     * holdings.
     */
    public void remove(UPGData product) throws UPGDataException;
    
    /**
     * Update the identified product in the local holdings.
     * 
     * @param product Object representing the product to update in local 
     * holdings.
     */
    public void update(IntermediateUPGData product) throws UPGDataException;
    
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
            UPGData             localHoldings);
    
}

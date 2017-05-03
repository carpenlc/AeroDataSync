package mil.nga.aero.interfaces;

import mil.nga.aero.upg.exceptions.UPGDataException;
import mil.nga.aero.upg.model.RawUPGData;

/**
 * Interface implemented by classes designed to obtain information on 
 * current source holdings from the source.  The classes will download 
 * raw UPG data in JSON format and unmarshal it into a usable format.  
 * 
 * @author L. Craig Carpenter
 */
public interface AeroDataServiceI {

    /**
     * Retrieve the raw UPG/JEPP data from the source.
     * 
     * @return The un-marshalled JSON data.
     * @throws UPGDataException Thrown if there are problems retrieving the
     * raw data from the source.
     */
    public RawUPGData getRawData() throws UPGDataException;
}

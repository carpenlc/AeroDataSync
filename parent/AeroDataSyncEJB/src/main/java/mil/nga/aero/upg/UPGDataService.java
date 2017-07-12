package mil.nga.aero.upg;

import java.io.Serializable;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.aero.DataService;
import mil.nga.aero.interfaces.AeroDataConstants;
import mil.nga.aero.interfaces.AeroDataServiceI;
import mil.nga.aero.upg.exceptions.ErrorCodes;
import mil.nga.aero.upg.exceptions.UPGDataException;
import mil.nga.aero.upg.model.RawUPGData;

/**
 * Session Bean implementation class UPGDataService
 */
@Stateless
@LocalBean
public class UPGDataService 
        extends DataService 
        implements AeroDataConstants, AeroDataServiceI, Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -1107720629682573808L;
    
    /**
     * Set up the Log4j system for use throughout the class
     */        
    private static final Logger LOGGER = LoggerFactory.getLogger(
            UPGDataService.class);
    
    /**
     * Eclipse-generated default constructor. 
     */
    public UPGDataService() { }

    /**
     * Retrieve the raw UPG data from the source.
     * 
     * @return The un-marshalled JSON data.
     * @throws UPGDataException Thrown if there are problems retrieving the
     * raw data from the source.
     */
    public RawUPGData getRawData() throws UPGDataException {
        
        String targetURL = super.getTargetURL(
                AeroDataConstants.UPG_TARGET_URL);
        
        if ((targetURL == null) || (targetURL.isEmpty())) {
            LOGGER.error("Unable to retreive the target URL associated with "
                    + "UPG data.  This is an issue with the properties "
                    + "file.");
            throw new UPGDataException(ErrorCodes.PROPERTIES_NOT_LOADED);
        }
        
        return deserialize(getRawData(targetURL));
    }
    
}

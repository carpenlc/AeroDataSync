package mil.nga.aero.jepp;

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
 * Session Bean implementation class JEPPDataService
 */
@Stateless
@LocalBean
public class JEPPDataService 
		extends DataService 
		implements AeroDataConstants, AeroDataServiceI, Serializable {

    /**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = -167055492849742028L;

	/**
	 * Set up the Log4j system for use throughout the class
	 */		
	private static final Logger LOGGER = LoggerFactory.getLogger(
			JEPPDataService.class);
	
	/**
     * Eclipse-generated default constructor. 
     */
    public JEPPDataService() { }

    /**
     * Retrieve the raw JEPP data from the source.
     * 
     * @return The un-marshalled JSON data.
     * @throws UPGDataException Thrown if there are problems retrieving the
     * raw data from the source.
     */
    public RawUPGData getRawData() throws UPGDataException {
    	
    	String targetURL = super.getTargetURL(
    			AeroDataConstants.JEPP_TARGET_URL);
    	
    	if ((targetURL == null) || (targetURL.isEmpty())) {
    		LOGGER.error("Unable to retreive the target URL associated with "
    				+ "JEPP data.  This is an issue with the properties "
    				+ "file.");
    		throw new UPGDataException(ErrorCodes.PROPERTIES_NOT_LOADED);
    	}
    	
    	return unmarshal(getRawData(targetURL));
    }
}

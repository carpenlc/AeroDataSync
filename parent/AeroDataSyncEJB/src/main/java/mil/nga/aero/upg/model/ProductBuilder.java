package mil.nga.aero.upg.model;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.nga.PropertyLoader;
import mil.nga.aero.interfaces.AeroDataConstants;
import mil.nga.aero.upg.exceptions.ErrorCodes;
import mil.nga.aero.upg.exceptions.UPGDataException;
import mil.nga.exceptions.PropertiesNotLoadedException;
import mil.nga.types.AeroDataType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains some methods used to construct data structures 
 * containing the various UPGData classes.  
 * 
 * @author L. Craig Carpenter
 */
public class ProductBuilder 
        extends PropertyLoader 
        implements AeroDataConstants {

    /**
     * Set up the Log4j system for use throughout the class
     */        
    private static final Logger LOGGER = LoggerFactory.getLogger(
            ProductBuilder.class);
    
    /**
     * The base URL that will be used when constructing the local URL data.
     */
    private final String baseURL;
    
    /**
     * The base URL that will be used when constructing the local URL data.
     */
    private final AeroDataType dataType;
    
    /**
     * Default constructor requiring clients to supply the type of data 
     * that will be constructed by the instanced object.
     * 
     * @param type The AeroDataType
     */
    public ProductBuilder(AeroDataType type) throws UPGDataException { 
        try {
            if (type != null) {
                dataType = type;
                switch (type) {
                    case UPG:
                        baseURL = getProperty(UPG_BASE_URL);
                        break;
                    case JEPP:
                        baseURL = getProperty(JEPP_BASE_URL);
                        break;
                    default:
                        LOGGER.error("Unsupported data type requested.  Client "
                                + "requested data service associated with data "
                                + "type [ "
                                + type.getText()
                                + " ] which is not implemented.  Exception to be "
                                + "thrown.");
                        throw new UPGDataException(
                                ErrorCodes.UNSUPPORTED_PRODUCT_EXCEPTION);
                }        
            }
            else {
                LOGGER.error("Invalid data type requested.  Client "
                        + "supplied a NULL data type.  Exception to be thrown.");
                throw new UPGDataException(
                        ErrorCodes.INVALID_PRODUCT_EXCEPTION);
            }
        }
        catch (PropertiesNotLoadedException pnle) {
            LOGGER.error("Unable to load property [ "
                    + UPG_BASE_URL
                    + " ].  Unexpected PropertiesNotLoadedException raised.  "
                    + "Error message [ "
                    + pnle.getMessage()
                    + " ].");
            throw new UPGDataException(ErrorCodes.PROPERTIES_NOT_LOADED);
        }
    }
    
    /**
     * Getter method for the base URL for the product type..
     * @return The baseURL.
     */
    public String getBaseURL() {
        return baseURL;
    }
    
    /**
     * Getter method for the AeroDataType this class will be associated with.
     * @return The data type.
     */
    public AeroDataType getDataType() {
        return dataType;
    }
    
    /**
     * This method contains the logic required to extract the short type name
     * from what we receive from Leidos.  This type name is used for the 
     * construction of the output destination directory and for storage in 
     * the back end data store.
     * 
     * @return The NGA name of the data type.
     */
    private String getType(String value) {
        
        String shortType = "IAP";
        
        if ((value != null) && (!value.isEmpty())) {
            if (value.toLowerCase().contains("sid")) {
                shortType = "DEP";
            }
            else if (value.toLowerCase().contains("star")) {
                shortType = "ARR";
            }
        }
        
        return shortType;
    }
    
    /**
     * Extract the name of the target UPG data file from the link that was 
     * received from the target UPG data source.
     * 
     * @param link The source URL.
     * @return The extracted filename.
     */
    private String getFilename(String link) {
        String filename = "";
        
        if ((link != null) && (!link.isEmpty())) {
            filename = link.substring(
                    link.lastIndexOf('/')+1, 
                    link.length());
        }
        
        return filename;
    }
    
    /**
     * Construct the local URL that will be used by clients to download the
     * UPG products.  This method also requires the base URL which is read 
     * from the system properties file.
     * 
     * @param icao The ICAO number
     * @param type The short type
     * @param filename The actual name of the file.
     * @return The local URL
     * @throws IllegalStateException Thrown if there are issues with the 
     * input data.
     * @throws UPGDataException Thrown if the required properties are not set.
     */
    public String getLocalURL( 
            String icao, 
            String type, 
            String filename) throws IllegalStateException, UPGDataException {
        
        StringBuilder sb = new StringBuilder();

    
        if ((baseURL != null) && (!baseURL.isEmpty())) {
            if ((icao != null) && (!icao.isEmpty())) {
                if ((type != null) && (!type.isEmpty())) {
                    if ((filename != null) && (!filename.isEmpty())) {
                        sb.append(baseURL);
                        sb.append(icao);
                        sb.append(File.separator);
                        sb.append(type);
                        sb.append(File.separator);
                        sb.append(filename);
                    }
                    else {
                        throw new IllegalStateException("The input filename "
                                + "is null or empty.");    
                    }
                }
                else {
                    throw new IllegalStateException("The input Type is "
                            + "null or empty.");        
                }
            }
            else {
                throw new IllegalStateException("The input ICAO is null "
                        + "or empty.");
            }
        }
        else {
            throw new IllegalStateException("The input base URL is null "
                    + "or empty.");
        }

        return sb.toString();
    }
    
    /**
     * This method builds an UPGData product from an IntermediateUPGData 
     * object.  The resulting UPGData product object will be ready to persist
     * in the target data store.
     * 
     * @param intermediate An intermediate UPGData product.
     * @param baseURL The base URL 
     * @return A final NGA formatted product.
     */
    public UPGData build (IntermediateUPGData intermediate) 
            throws UPGDataException {
        
        UPGData object = null;
        
        if (intermediate != null) {
            
            String filename = getFilename(intermediate.getLink());
            String type = getType(intermediate.getType());
            String localLink = getLocalURL(intermediate.getICAO(), type, filename);
            object = new UPGData.UPGDataBuilder()
                        .uuid(intermediate.getUUID())
                        .icao(intermediate.getICAO())
                        .dateLastModified(intermediate.getDateLastModified())
                        .link(localLink)
                        .sourceLink(intermediate.getLink())
                        .filename(filename)
                        .hash(intermediate.getHash())
                        .psuedoName(intermediate.getPsuedoName())
                        .type(type)
                        .build();    
        }
        else {
            LOGGER.error("The input intermediate UPGData object is null.");
            throw new UPGDataException(ErrorCodes.APPLICATION_EXCEPTION);
        }
        return object;
    }
    
    /**
     * Construct a Map object (i.e. key/value pair) containing the  
     * <code>IntermediateUPGData</code> downloaded from the target UPG data 
     * source.  The Map facilitates searching and set operations.
     * 
     * @param rawData The raw UPG data retrieved from the source.
     * @return A Map object containing the IntermediateUPGData
     */
    public Map<String, IntermediateUPGData> buildMap(RawUPGData rawData) {
        
        Map<String, IntermediateUPGData> upgData = 
                new HashMap<String, IntermediateUPGData>();
        
        if ((rawData != null) && (rawData.getData() != null)) {
            for (ArrayList<String> list : rawData.getData()) {
                if (list.size() == 7) {
                    try {
                        upgData.put(
                                list.get(0),
                                new IntermediateUPGData.IntermediateUPGDataBuilder()
                                    .attributes(list)
                                    .build());
                    }
                    catch (ParseException pe) {
                        LOGGER.warn("An unexpected ParseException was raised "
                                + "while attempting to parse the date field "
                                + "associated with UUID [ "
                                + list.get(0)
                                + " ].  Bad date data [ "
                                + list.get(2)
                                + " ].");
                    }
                    catch (IllegalStateException ile) {
                        LOGGER.warn("Error encountered processing incoming "
                                + "JSON data.  IllegalStateException "
                                + "encountered.  Error message [ "
                                + ile.getMessage()
                                + " ].");
                    }
                }
                else {
                    LOGGER.warn("Encountered a JSON array with the incorrect "
                            + "number of elements.");
                }
            }
        }
        return upgData;
    }
        
    /**
     * Construct a List object (i.e. key/value pair) containing the  
     * <code>IntermediateUPGData</code> downloaded from the target UPG data 
     * source.  The Map facilitates searching and set operations.
     * 
     * @param rawData The raw UPG data retrieved from the source.
     * @return A List object containing the IntermediateUPGData data.
     */
    public List<IntermediateUPGData> build(RawUPGData rawData) {
        
        List<IntermediateUPGData> upgData = new ArrayList<IntermediateUPGData>();
        
        if ((rawData != null) && (rawData.getData() != null)) {
            for (ArrayList<String> list : rawData.getData()) {
                if (list.size() == 7) {
                    try {
                        
                        upgData.add(
                                new IntermediateUPGData.IntermediateUPGDataBuilder()
                                    .attributes(list)
                                    .build());
                        
                    }
                    catch (ParseException pe) {
                        
                        LOGGER.warn("An unexpected ParseException was raised "
                                + "while attempting to parse the date field "
                                + "associated with UUID [ "
                                + list.get(0)
                                + " ].  Bad date data [ "
                                + list.get(2)
                                + " ].");
                        
                    }
                    catch (IllegalStateException ile) {
                        
                        LOGGER.warn("Error encountered processing incoming "
                                + "JSON data.  IllegalStateException "
                                + "encountered.  Error message [ "
                                + ile.getMessage()
                                + " ].");
                        
                    }
                }
                else {
                    LOGGER.warn("Encountered a JSON array with the incorrect "
                            + "number of elements.");
                }
            }
        }
        return upgData;
    }
}

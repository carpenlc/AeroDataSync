package mil.nga.aero;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import mil.nga.PropertyLoader;
import mil.nga.aero.interfaces.AeroDataConstants;
import mil.nga.aero.upg.exceptions.ErrorCodes;
import mil.nga.aero.upg.exceptions.UPGDataException;
import mil.nga.aero.upg.model.RawUPGData;
import mil.nga.exceptions.PropertiesNotLoadedException;

/**
 * Session Bean implementation class UPGDataService
 * 
 * This class provides the methods that interface with Leidos via HTTP for 
 * retrieving data from the target UPG data source.  This includes 
 * retrieving and unmarshalling the latest UPG data lists and downloading 
 * individual product files. 
 * 
 * Note: The connection to the data source is over HTTPS so the 
 * application container must be configured to trust the target server 
 * certificate.
 */
@Stateless
@LocalBean
public class DataService 
        extends PropertyLoader
        implements AeroDataConstants, Serializable {
    
    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -7736797070521926821L;

    /**
     * Set up the Log4j system for use throughout the class
     */        
    private static final Logger LOGGER = LoggerFactory.getLogger(
            DataService.class);

    /**
     * Eclipse-generated constructor.
     */
    public DataService() {
        super(PROPERTIES_FILE); 
    }

    /**
     * Getter method for the target URL from which we will retrieve the 
     * UPG data in JSON format. 
     * 
     * @return The target URL.
     * @throws UPGDataException Thrown if there are issues obtaining the
     * system properties data.
     */
    protected String getTargetURL(String propName) throws UPGDataException {
        String url = null;
        try {
            url =  getProperty(propName);
            if ((url == null) || (url.isEmpty())) {
                throw new UPGDataException(ErrorCodes.PROPERTIES_NOT_DEFINED);
            }
        }
        catch (PropertiesNotLoadedException pnle) {
            throw new UPGDataException(ErrorCodes.PROPERTIES_NOT_LOADED);
        }
        return url;
    }
   
    /**
     * Some of the filenames that we receive from Leidos contain spaces.  
     * Leidos only handles the spaces if you encode them with <code>%20</code>
     * as opposed to the more correct <code>+</code> character.  This method
     * was introduced to ensure that the URLs are properly encoded.
     * 
     * @return encoded URL.
     */
    private String getEncodedURL(String urlStr) 
            throws MalformedURLException, URISyntaxException {
        
        URL url = new URL(urlStr);
        URI uri = new URI(
                url.getProtocol(), 
                url.getUserInfo(), 
                url.getHost(), 
                url.getPort(), 
                url.getPath(), 
                url.getQuery(), 
                url.getRef());
        return uri.toASCIIString();
    }
    
    /**
     * Download the file represented by the source URL to the file system 
     * location specified by the destination parameter. 
     * 
     * @param source The source URL identifying the location of the file to
     * download.
     * @param destination The target on-disk location into which the source 
     * file will be downloaded.
     * @return True if the method completed without error, false otherwise.
     * @throws UPGDataException Thrown if there are errors associated with any 
     * of the input parameters, or if there are errors during the download 
     * process.
     */
    public boolean getProductFile(
            String source, 
            String destination) throws UPGDataException {
        
        CloseableHttpClient   client     = null;
        BufferedInputStream   input      = null;
        BufferedOutputStream  output     = null;
        HttpGet               request    = null;
        String                encodedURL = null;
        CloseableHttpResponse response   = null;
        long                  start      = System.currentTimeMillis();
        boolean               success    = false;
        
        if ((source != null) && (!source.isEmpty())) {
            if ((destination != null) && (!destination.isEmpty())) {
                
                try {
                    
                    client = HttpClients.createDefault();
                    encodedURL = getEncodedURL(source);
                    request = new HttpGet(encodedURL);
                    request.addHeader("User-Agent", DEFAULT_USER_AGENT);
                    request.addHeader("Accept", "application/pdf");
                    request.addHeader("Content-Type", 
                            "application/x-www-form-urlencoded");
                    
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Executing HTTP GET request for URL [ "
                                + encodedURL
                                + " ].");
                    }
                    
                    response = client.execute(request);
                    int httpCode = response.getStatusLine().getStatusCode();
                    
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("GET request for URL [ "
                                + encodedURL
                                + " ] returned HTTP status code [ "
                                + destination
                                + " ].");
                    }
                    
                    if (httpCode == HttpStatus.SC_OK) {
                        
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Downloading file [ "
                                    + encodedURL
                                    + " ].  Total bytes available [ "
                                    + response.getEntity().getContentLength()
                                    + " ].");
                        }
                        
                        input = new BufferedInputStream(
                                response.getEntity().getContent());
                        output = new BufferedOutputStream(
                                new FileOutputStream(
                                        new File(destination)));
                        
                        int inByte;
                        while((inByte = input.read()) != -1) {
                            output.write(inByte);
                        }
                        
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("File [ "
                                    + destination
                                    + " ] downloaded in [ "
                                    + (System.currentTimeMillis() - start) 
                                    + " ] ms.");
                        }
                        output.flush();
                        success = true;
                    }
                    else {
                        LOGGER.error("Execute of GET for URL [ "
                                + encodedURL
                                + " ] returned a status code of [ "
                                + httpCode
                                + " ].");
                        throw new UPGDataException(
                                ErrorCodes.INVALID_HTTP_STATUS_CODE);
                    }
                }
                catch (MalformedURLException mue) {
                    LOGGER.error("Unable to execute the HTTP GET request for URL [ "
                            + source
                            + " ]. Unexpected MalformedURLException encountered [ "
                            + mue.getMessage()
                            + " ].");
                    throw new UPGDataException(
                            ErrorCodes.MALFORMED_URL);
                }
                catch (URISyntaxException use) {
                    LOGGER.error("Unable to execute the HTTP GET request for URL [ "
                            + source
                            + " ]. Unexpected URISyntaxException encountered [ "
                            + use.getMessage()
                            + " ].");
                    throw new UPGDataException(
                            ErrorCodes.MALFORMED_URL);
                }
                catch (ClientProtocolException cpe) {
                    LOGGER.error("Unable to execute the HTTP GET request for URL [ "
                            + source
                            + " ]. Unexpected ClientProtocolException encountered [ "
                            + cpe.getMessage()
                            + " ].");
                    throw new UPGDataException(
                            ErrorCodes.CLIENT_PROTOCOL_EXCEPTION);
                }
                catch (IOException ioe) {
                    LOGGER.error("Unable to execute the HTTP GET request for URL [ "
                            + source
                            + " ]. Unexpected IOException encountered [ "
                            + ioe.getMessage()
                            + " ].");
                    throw new UPGDataException(
                            ErrorCodes.IO_EXCEPTION);
                }
                finally {
                    if (input != null) {
                        try { input.close(); } catch (Exception e) {}
                    }
                    if (output != null) {
                        try { output.close(); } catch (Exception e) {}
                    }
                    if (response != null) {
                        try { response.close(); } catch (Exception e) {}
                    }
                    if (client != null) {
                        try { client.close(); } catch (Exception e) {}
                    }
                }
            }
            else {
                LOGGER.debug("The destination location is null or empty.  "
                        + "No attempt will be made to download source file [ "
                        + source
                        + " ].");
            }
        }
        else {
            LOGGER.error("The input URL identifying the source file to "
                    + "download is null or empty.  No attempt will be "
                    + "made to initiate a download.");
        }
        return success;
    }
    
    /**
     * Method used to retrieve a String containing JSON data associated with
     * various aeronautical holdings at an external location.  
     * 
     * @param targetURL the target URL that will be providing the requested 
     * JSON data.
     * @return The raw String data retrieved from the target URL.
     * @throws UPGDataException Thrown if problems are encountered while 
     * downloading and processing the UPG source data.
     */
    public String getRawData(String targetURL) throws UPGDataException {
        
        CloseableHttpClient   client   = HttpClients.createDefault();
        BufferedReader        reader   = null;
        HttpGet               request  = new HttpGet(targetURL);
        CloseableHttpResponse response = null;
        StringBuilder         sb       = new StringBuilder();
        long                  start    = System.currentTimeMillis();
        
        try {
            
            request.addHeader("User-Agent", DEFAULT_USER_AGENT);
            request.addHeader("Accept", "application/json");
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Executing HTTP GET request for URL [ "
                        + targetURL
                        + " ].");
            }
            
            response = client.execute(request);
            int httpCode = response.getStatusLine().getStatusCode();
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("GET request for URL [ "
                        + targetURL
                        + " ] returned HTTP status code [ "
                        + httpCode
                        + " ].");
            }
            if (httpCode == HttpStatus.SC_OK) {
                
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("HTTP GET bytes available [ "
                            + response.getEntity().getContentLength()
                            + " ].");
                }
                
                reader = new BufferedReader(
                            new InputStreamReader(
                                response.getEntity().getContent()));
                
                String input = null;
                while((input = reader.readLine()) != null) {
                    sb.append(input);
                }
                
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Execution of HTTP GET request for URL [ "
                            + targetURL
                            + " ] completed in [ "
                            + (System.currentTimeMillis() - start) 
                            + " ] ms.");
                }
                
            }
            else {
                LOGGER.error("Execute of GET for URL [ "
                        + targetURL
                        + " ] returned a status code of [ "
                        + httpCode
                        + " ].");
                throw new UPGDataException(
                        ErrorCodes.INVALID_HTTP_STATUS_CODE);
            }
        }
        catch (ClientProtocolException cpe) {
            LOGGER.error("Unable to execute the HTTP GET request for URL [ "
                    + targetURL
                    + " ]. Unexpected ClientProtocolException encountered [ "
                    + cpe.getMessage()
                    + " ].");
            throw new UPGDataException(
                    ErrorCodes.CLIENT_PROTOCOL_EXCEPTION);
        }
        catch (IOException ioe) {
            LOGGER.error("Unable to execute the HTTP GET request for URL [ "
                    + targetURL
                    + " ]. Unexpected IOException encountered [ "
                    + ioe.getMessage()
                    + " ].");
            
            // Debugging message to see what the default timeout is.
            if (ioe.getMessage().contains("imeout")) {
            	LOGGER.error("HTTP GET request timed out in [ "
            			+ (System.currentTimeMillis() - start)
            			+ " ] ms.");
            }
            
            throw new UPGDataException(
                    ErrorCodes.IO_EXCEPTION);
        }
        finally {
            if (reader != null) {
                try { reader.close(); } catch (Exception e) {}
            }
            if (response != null) {
                try { response.close(); } catch (Exception e) {}
            }
            if (client != null) {
                try { client.close(); } catch (Exception e) {}
            }
        }
        return sb.toString();
    }
    
    /**
     * This method converts the JSON data that was retrieved from the 
     * target URL into an object of type UPGData.
     * 
     * @param data A JSON String
     * @return The UPGData unmarshalled into a Java POJO.
     * @throws UPGException Thrown if any errors were encountered during the 
     * unmarshalling operation.
     */
    public RawUPGData deserialize (String data) throws UPGDataException {
        
        RawUPGData obj   = null;
        long       start = System.currentTimeMillis();
        
        try {
            if ((data == null) || (data.isEmpty())) {
                
                LOGGER.error("No data was retrieved from target URL.");
                throw new UPGDataException(
                        ErrorCodes.NO_DATA_RETRIEVED);
                
            }
            else {
                
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(
                        MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, 
                        true);
                obj = mapper.readValue(data, RawUPGData.class);
                
            }
        }
        catch (JsonMappingException jme) {
            
            LOGGER.error("Unable to unmarshal the input JSON String.  "
                    + "Unexpected JsonMappingException encountered [ "
                    + jme.getMessage()
                    + " ].");
            if (LOGGER.isDebugEnabled()) { 
                LOGGER.debug("Problematic JSON data is as follows: "
                        + data);
            }
            throw new UPGDataException(
                    ErrorCodes.JSON_PARSER_EXCEPTION);
            
        }
        catch (JsonParseException jpe) {
            
            LOGGER.error("Unable to unmarshal the input JSON String.  "
                    + "Unexpected JsonParseException encountered [ "
                    + jpe.getMessage()
                    + " ].");
            if (LOGGER.isDebugEnabled()) { 
                LOGGER.debug("Problematic JSON data is as follows: "
                        + data);
            }
            throw new UPGDataException(
                    ErrorCodes.JSON_PARSER_EXCEPTION);
            
        }
        catch (IOException ioe) {
            
            LOGGER.error("Unable to unmarshal the input JSON String.  "
                    + "Unexpected IOException encountered [ "
                    + ioe.getMessage()
                    + " ].");
            if (LOGGER.isDebugEnabled()) { 
                LOGGER.debug("Problematic JSON data is as follows: "
                        + data);
            }
            throw new UPGDataException(
                    ErrorCodes.JSON_IO_EXCEPTION);
            
        }
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Unmarshal of [ "
                    + obj.getData().size()
                    + " ] UPG data records completed in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
        
        return obj;
    }
}

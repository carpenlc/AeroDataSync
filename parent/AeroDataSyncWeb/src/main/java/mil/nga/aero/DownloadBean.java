package mil.nga.aero;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import mil.nga.aero.upg.model.UPGData;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Managed bean class introduced to handle the file download. 
 * This bean is bound to commandButton added to each row in the filtered list 
 * of available ISOs.  When a download is requested this class 
 * 
 * Additional logic was added to enable tracking of metrics associated with
 * file downloads.  Data associated with the requested download and passed to 
 * the MetricsService EJB in order to track what products are being downloaded 
 * by who and when.
 *  
 * @author L. Craig Carpenter
 */
@ManagedBean
public class DownloadBean 
        
        implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -5193223272254043322L;

    /**
     * Static logger for use throughout the class.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(DownloadBean.class);
    
    /**
     * Length of the hexadecimal token used as the download request ID
     */
    private static int TOKEN_LENGTH = 8;
    
    /**
     * Handle to the streamed content object that will be returned when 
     * the download button is pressed.  This is essentially a stream 
     * attached to a target file.
     */
    private StreamedContent fileToDownload;
    
    /**
     * Construct the local filename from the input URL.
     * 
     * @param path The URL for the file.
     * @return The local path to the file.
     */
    private String getLocalPath(String path) {
        Path p = Paths.get(path);
        return p.getFileName().toString();
    }
    
    /**
     * Obtain the actual size associated with the on-disk file. 
     *  
     * @param path String defining the full path to the target file.
     * @return The size of the target file in bytes.  Zero is returned
     * if there are any problems accessing the target file.
     */
    private long getSize(String path) {
        long size = 0;
        try { 
            size = Files.size(Paths.get(path));
        } 
        catch (IOException ioe) {
            LOGGER.warn("Unexpected IOException raised while attempting "
                    + "to determine the size of file [ "
                    + path
                    + " ].  Exception message [ "
                    + ioe.getMessage()
                    + " ].");
        }
        return size;
        
    }
    
    /**
     * This is the interface utilized by PrimeFaces to obtain a Stream 
     * attached to an on-disk file.  The stream
     * will be used to actually download the file contents.
     * 
     * @param product The Object containing the information associated 
     * with the product to be downloaded.
     * @return A stream attached to the requested file.
     */
    public StreamedContent getFile(
            UPGData product) {
    	
    	String localPath = null;
    	
    	if (product != null) {
	        if (LOGGER.isDebugEnabled()) {
	            LOGGER.debug("User requested download of file [ "
	                    + product.getLink()
	                    + " ].");
	        }
	        if ((product.getLink() != null) && 
	        		(!product.getLink().isEmpty())) {
	        	
	        	try {
		        	localPath = getLocalPath(product.getLink());
		        	Path p = Paths.get(localPath);
		            if ((p != null) && (Files.exists(p))) {
	                    fileToDownload = new DefaultStreamedContent(
	                            new FileInputStream(
	                            		localPath),
	                                    "application/octet-stream", 
	                                    product.getFilename());
		            }
		            else {
		            	LOGGER.error("Requested file [ "
		            			+ localPath
		            			+ " ] does not exist on server.");
	                    FacesContext.getCurrentInstance().addMessage(
	                            null, 
	                            new FacesMessage(
	                                    FacesMessage.SEVERITY_ERROR, 
	                                    "Error!", 
	                                    "Requested file does not exist on the server."));
		            }
	        	}
	        	catch (FileNotFoundException fnfe) {
                    
                    LOGGER.error("Unexpected FileNotFoundException "
                            + "encountered while attempting to construct "
                            + "a FileInputStream to file [ "
                            + localPath
                            + " ].  Error message details [ "
                            + fnfe.getMessage()
                            + " ].  (This should not happen.)");
                    FacesContext.getCurrentInstance().addMessage(
                            null, 
                            new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR, 
                                    "Error!", 
                                    "Requested file does not exist on the server."));
	        	}
	        }
    	}
        else {
            LOGGER.warn("Faces client returned an empty Product object as the "
                    + "target for download.");
            FacesContext.getCurrentInstance().addMessage(
                    null, 
                    new FacesMessage(
                            FacesMessage.SEVERITY_FATAL, 
                            "Fatal!", 
                            "Error validating file."));
        }        
        return fileToDownload;
    }
}




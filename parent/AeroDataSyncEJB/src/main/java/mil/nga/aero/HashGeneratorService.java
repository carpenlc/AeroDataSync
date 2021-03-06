package mil.nga.aero;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import mil.nga.types.HashType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This EJB is invoked after the archive files are created.  It will 
 * construct a hash of the supplied input file.  This service is also used 
 * by the validation algorithm generating a hash that can be used to 
 * uniquely identify a file and whether or not it has changed since a 
 * previous execution.
 * 
 * Note: We switched to using the commons codec classes because we found 
 * issues when converting the output hashes to Base64 using the JDK classes 
 * (specifically, leading 0s were being dropped).
 * 
 * @author L. Craig Carpenter
 */
public class HashGeneratorService {

    /**
     * Set up the Log4j system for use throughout the class
     */
    static final Logger LOGGER = LoggerFactory.getLogger(HashGeneratorService.class);
    
    /**
     * Default constructor. 
     */
    private HashGeneratorService() { }
    
    /**
     * Check the input hash against a previously generated hash.
     * 
     * @param file The file to check. 
     * @param hash The known hash
     * @param hashType The type of hash.
     * @return True if the hashes match, false otherwise.
     */
    public boolean checkHash(String file, String hash, HashType hashType) {
        
        boolean valid   = false;
        String  newHash = getHash(file, HashType.MD5);
        
        if ((newHash != null) && (!newHash.isEmpty())) {
            if (newHash.equalsIgnoreCase(hash)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Successfully validated hash for file [ "
                            + file
                            + " ].");
                }
                valid = true;
            }
            else {
                LOGGER.warn("Failed hash validation.  Calculated hash of [ "
                    + newHash 
                    + " ] for file [ "
                    + file
                    + " ] did not match expected hash of [ "
                    + hash
                    + " ] ");
            }
        }
        else {
            LOGGER.error("Unable to validate the hash for file [ "
                    + file 
                    + " ].");
        }
        
        return valid;
    }
    
    /**
     * Construct the hexadecimal-based hash of the input file.  If the input
     * file doesn't exist, or errors are encountered during hash generation the 
     * returned hash is null.
     * 
     * @param inputFile String containing the full path to a file on which
     * the requested hash should be applied.
     * @param hashType The type of hash to create 
     * @see <code>mil.nga.bundler.types.HashType</code>
     * @return The hash value as a hex string.
     */
    public String getHash(String inputFile, HashType hashType) {
        
        String hash = null;

        if ((inputFile != null) && (!inputFile.isEmpty())) { 
            File file = new File(inputFile);
            if (file.exists()) {
                
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Generating [ "
                            + hashType.getText()
                            + " ] hash for file [ "
                            + file.getAbsolutePath()
                            + " ].");
                }
                
                long startTime = System.currentTimeMillis();
                switch (hashType) {
                    case MD5 : 
                        hash = getMD5Hash(file);
                        break;
                    case SHA1:
                        hash = getSHA1Hash(file);
                        break;
                    case SHA256:
                        hash = getSHA256Hash(file);
                        break;
                    case SHA384:
                        hash = getSHA384Hash(file);                        
                        break;
                    case SHA512:
                        hash = getSHA512Hash(file);                        
                        break;
                }
                
                long elapsedTime = System.currentTimeMillis() - startTime;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(
                           "Hash type [ "
                            + hashType.getText()
                            + " ] for file [ "
                            + file.getAbsolutePath()
                            + " ] created in [ "
                            + Long.toString(elapsedTime)
                            + " ] ms.");
                }
            }
            else {
                LOGGER.error("Input file does not exists.  Input file "
                        + "specified [ "
                        + inputFile
                        + " ].  Null hash will be returned.");
            }
        }
        else {
            LOGGER.error("The require input file parameter is null or empty. "
                    + " The output hash file will not be generated.");
        }
        return hash;
    }
    
    /**
     * Generate a SHA-1 hash associated with the input 
     * 
     * @param inputFile The file on which to generate a hash.
     * @return The generated hash.
     * @deprecated
     * Use <code>getHash(String, HashType)</code>
     */
    public String generate(String inputFile) {
        return getHash(inputFile, HashType.SHA1);
    }
    
    /**
     * Generate a hash associated with the input file and store it in the output
     * file.
     * 
     * @param inputFile The file on which to generate a hash.
     * @param outputFile The output file in which to store the generated hash.
     */
    public void generate(String inputFile, String outputFile) {
        
        String method = "generate() - ";
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating hash for file [ "
                    + inputFile 
                    + " ].");
        }
        
        if ((inputFile != null) && (!inputFile.isEmpty())) {
            if ((outputFile != null) && (!outputFile.isEmpty())) {
                File file = new File(inputFile);
                if (file.exists()) {

                    String hash = getHash(file.getAbsolutePath(), HashType.SHA1);
                    saveHash(hash, outputFile);

                }
                else {
                    LOGGER.error(method
                            + "Input file does not exists.  Input file "
                            + "specified [ "
                            + inputFile
                            + " ].");
                }
            }
            else {
                    LOGGER.error(method
                            + "The require input file parameter is null or empty. "
                            + " The output hash file will not be generated.");
            }
        }
        else {
            LOGGER.error(method
                    + "The require input file parameter is null or empty.  "
                    + "The hash will not be generated.");
        }
    }
    
    /**
     * Accessor method for the singleton instance of the 
     * <code>HashGeneratorService</code> class.
     * 
     * @return The singleton instance of the 
     * <code>HashGeneratorService</code> class.
     */
    public static HashGeneratorService getInstance() {
        return HashGeneratorServiceHolder.getSingleton();
    }   
    
    /**
     * Calculate the MD5 hash using the Apache Commons Codec classes.  
     * 
     * @param file The file we need the hash for.
     * @return The calculated MD5 hash.
     */
    public String getMD5Hash(File file) {

        FileInputStream is   = null;
        String          hash = null;

        try {
            is = new FileInputStream(file);
            hash = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
        }
        catch (IOException ioe) {
            LOGGER.error(
                 "Unexpected IOException encountered while generating "
                 + "the [ " 
                 + HashType.MD5.getText() 
                 + " ] hash for file [ "
                 + file.getAbsolutePath()
                 + " ].  Exception message [ "
                 + ioe.getMessage()
                 + " ].  Method will return a null hash.");
        }
        finally {
            if (is != null) {
                    try { is.close(); } catch (Exception e) {}
            }
        }
        return hash;
    }
    
    /**
     * Calculate the SHA-1 hash using the Apache Commons Codec classes.
     * Note: SHA-1 hash generation seems to take about twice as long as MD5 
     * hash generation.
     * 
     * @param file The file we need the hash for.
     * @return The calculated SHA1 hash.
     */
    public String getSHA1Hash(File file) {

        FileInputStream is   = null;
        String          hash = null;

        try {
            is = new FileInputStream(file);
            hash = org.apache.commons.codec.digest.DigestUtils.sha1Hex(is);
        }
        catch (IOException ioe) {
             LOGGER.error(
                     "Unexpected IOException encountered while generating "
                     + "the [ " 
                     + HashType.SHA1.getText() 
                     + " ] hash for file [ "
                     + file.getAbsolutePath()
                     + " ].  Exception message [ "
                     + ioe.getMessage()
                     + " ].  Method will return a null hash.");
        }
        finally {
            if (is != null) {
                    try { is.close(); } catch (Exception e) {}
            }
        }
        return hash;
    }

    /**
     * Calculate the SHA-256 hash using the Apache Commons Codec classes.
     * 
     * @param file The file we need the hash for.
     * @return The calculated SHA256 hash.
     */
    public String getSHA256Hash(File file) {

        FileInputStream is   = null;
        String          hash = null;

        try {
            is = new FileInputStream(file);
            hash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(is);
        }
        catch (IOException ioe) {
            LOGGER.error(
                "Unexpected IOException encountered while generating "
                + "the [ " 
                + HashType.SHA256.getText() 
                + " ] hash for file [ "
                + file.getAbsolutePath()
                + " ].  Exception message [ "
                + ioe.getMessage()
                + " ].  Method will return a null hash.");
        }
        finally {
            if (is != null) {
                    try { is.close(); } catch (Exception e) {}
            }
        }
        return hash;
    }
    
    /**
     * Calculate the SHA-384 hash using the Apache Commons Codec classes.
     * 
     * @param file The file we need the hash for.
     * @return The calculated SHA384 hash.
     */
    public String getSHA384Hash(File file) {

        FileInputStream is   = null;
        String          hash = null;

        try {
            is = new FileInputStream(file);
            hash = org.apache.commons.codec.digest.DigestUtils.sha384Hex(is);
        }
        catch (IOException ioe) {
            LOGGER.error(
                "Unexpected IOException encountered while generating "
                + "the [ " 
                + HashType.SHA384.getText() 
                + " ] hash for file [ "
                + file.getAbsolutePath()
                + " ].  Exception message [ "
                + ioe.getMessage()
                + " ].  Method will return a null hash.");
        }
        finally {
            if (is != null) {
                    try { is.close(); } catch (Exception e) {}
            }
        }
        return hash;
    }
    
    /**
     * Calculate the SHA-512 hash using the Apache Commons Codec classes.
     * 
     * @param file The file we need the hash for.
     * @return The calculated SHA512 hash.
     */
    public String getSHA512Hash(File file) {

        FileInputStream is   = null;
        String          hash = null;

        try {
            is = new FileInputStream(file);
            hash = org.apache.commons.codec.digest.DigestUtils.sha512Hex(is);
        }
        catch (IOException ioe) {
            LOGGER.error(
                "Unexpected IOException encountered while generating "
                + "the [ " 
                + HashType.SHA512.getText() 
                + " ] hash for file [ "
                + file.getAbsolutePath()
                + " ].  Exception message [ "
                + ioe.getMessage()
                + " ].  Method will return a null hash.");
        }
        finally {
            if (is != null) {
                    try { is.close(); } catch (Exception e) {}
            }
        }
        return hash;
    }
    
    /**
     * Save the calculated hash to the specified output file.
     * 
     * @param hash The calculated hash.
     * @param filename The filename in which to save the hash.
     */
    public void saveHash(
                    String hash,
                    String filename) {

        String         method = "saveHash() - ";
        BufferedWriter writer = null;

        try {
            
            writer = new BufferedWriter(new FileWriter(filename));
            writer.write(hash);
            writer.flush();

            // Set file permissions on the hash file to wide open.
            File file = new File(filename);
            if (file.exists()) {
                    file.setExecutable(true, false);
                    file.setReadable(true, false);
                    file.setWritable(true, false);
            }
            else {
                LOGGER.warn(method
                        + "Expected hash file does not exist.  Filename [ "
                        + filename
                        + " ].");
            }
        }
        catch (IOException ioe) {
            String msg = "Unexpected IOException exception "
                    + "encountered while attempting to save the hash string "
                    + "to an output file.  Exception message [  "
                    + ioe.getMessage()
                    + " ].";
            LOGGER.error(method + msg, ioe);
        }
        finally {
            if (writer != null) {
                    try { writer.close(); } catch (Exception e) { }
            }
        }
    }

    /**
     * Static inner class used to construct the Singleton object.  This class
     * exploits the fact that classes are not loaded until they are referenced
     * therefore enforcing thread safety without the performance hit imposed
     * by the <code>synchronized</code> keyword.
     * 
     * @author L. Craig Carpenter
     */
    public static class HashGeneratorServiceHolder {
        
        /**
         * Reference to the Singleton instance of the ClientUtility
         */
        private static HashGeneratorService _instance = new HashGeneratorService();
    
        /**
         * Accessor method for the singleton instance of the ClientUtility.
         * @return The Singleton instance of the client utility.
         */
        public static HashGeneratorService getSingleton() {
            return _instance;
        }
        
    }
}

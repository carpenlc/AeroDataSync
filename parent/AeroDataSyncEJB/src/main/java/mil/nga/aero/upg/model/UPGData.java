package mil.nga.aero.upg.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;


/**
 * Simple POJO containing the data associated with a single UPG data record.
 * This class sorts the raw JSON array retrieved from the target UPG data 
 * source into named fields.
 * 
 * @author L. Craig Carpenter
 */
public class UPGData implements Serializable {
    
    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -9148483257243638693L;
    
    /** 
     * Format associated with dates incoming from the target UPG data source.
     */
    private static final String DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
            
    /**
     * DateFormat object used to convert the String based last-update date 
     * retrieved from the target data store.
     */
    private static final SimpleDateFormat formatter = 
                new SimpleDateFormat(DATE_FORMAT_STRING);
    
    private final String     uuid;
    private final String     icao;
    private final Date       dateLastModified;
    private       String     link;
    private final String     filename;
    private       BigDecimal success; // 0 or 1 (1 represents success).
    private final String     psuedoName;
    private final String     type;
    private final String     sourceLink; // Source holdings link.
    private final String     hash;       // Included for JEPP data but not UPG.
    
    /**
     * Constructor used to set all of the required internal members.
     * 
     * @param builder Populated builder object.
     */
    private UPGData(UPGDataBuilder builder) {
        this.uuid             = builder.uuid;
        this.icao             = builder.icao;
        this.dateLastModified = builder.dateLastModified;
        this.link             = builder.link;
        this.filename         = builder.filename;
        this.hash             = builder.hash;
        this.success          = builder.success;
        this.psuedoName       = builder.psuedoName;
        this.sourceLink       = builder.sourceLink;
        this.type             = builder.type;
    }
    
    /**
     * Getter method for the DLM (date last modified) attribute.
     * @return The DLM attribute.
     */
    public Date getDateLastModified() {
        return dateLastModified;
    }
    
    /**
     * Getter method for the DLM (date last modified) attribute.
     * @return The DLM attribute.
     */
    public String getDateLastModifiedString() {
        return formatter.format(dateLastModified);
    }
    
    /**
     * Getter method for the UPG filename.
     * @return The UPG filename.
     */
    public String getFilename() {
        return filename;
    }
    
    /**
     * Getter method for the hash associated with the file.
     * @return The hash.
     */
    public String getHash() {
        return hash;
    }
    
    /**
     * Getter method for the ICAO attribute.
     * @return The ICAO attribute.
     */
    public String getICAO() {
        return icao;
    }
    
    /**
     * Getter method for the PSUEDONAME attribute.
     * @return The PSUEDONAME attribute.
     */
    public String getPsuedoName() {
        return psuedoName;
    }
    
    /**
     * Getter method for the success attribute.
     * @return The success attribute.
     */
    public BigDecimal getDownloadSuccess() {
        return success;
    }
    
    /**
     * Getter method for the URL of the source data.
     * @return The URL of the source data.
     */
    public String getSourceLink() {
        return sourceLink;
    }
    
    /**
     * Getter method for the TYPE attribute.
     * @return The TYPE attribute.
     */
    public String getType() {
        return type;
    }
    
    /**
     * Getter method for the URL attribute.  This is the local URL.
     * @return The URL attribute.
     */
    public String getLink() {
        return link;
    }
    
    /**
     * Getter method for the UUID attribute.
     * @return The UUID attribute.
     */
    public String getUUID() {
        return uuid;
    }
    
    /**
     * Setter method for the download success attribute.  For some reason they 
     * created the back-end database with enough space for a BigDecimal for storing
     * either a zero or one.
     * @param value The download success attribute.
     */
    public void setDownloadSuccess(BigDecimal value) throws IllegalStateException {
        if (value == null) {
            throw new IllegalStateException("Attempted to build UPGData "
                    + "object but the value for the success field was out of "
                    + "range [ "
                    + value
                    + " ].");
        }
        success = value;
    }
    
    /**
     * Setter method for the URL attribute.
     * @return The URL attribute.
     */
    public void setLink(String value) {
        link = value;
    }
    
    /**
     * Convert to human-readable format.
     */
    public String toString() {
        
        StringBuilder sb      = new StringBuilder();
        String        newLine = System.getProperty("line.separator");
        
        sb.append("UUID => [ ");
        sb.append(getUUID());
        sb.append(" ], ICAO => [ ");
        sb.append(getICAO());
        sb.append(" ], DATE_LAST_MODIFIED => [ ");
        sb.append(getDateLastModifiedString());
        sb.append(" ], LINK => [ ");
        sb.append(getLink());
        sb.append(" ], SOURCE_LINK => [ ");
        sb.append(getSourceLink());
        sb.append(" ], PSUEDONAME => [ ");
        sb.append(getPsuedoName());
        sb.append(" ], TYPE => [ ");
        sb.append(getType());
        sb.append(" ], FILENAME => [ ");
        sb.append(getFilename());
        sb.append(" ], SUCCESS_DL => [ ");
        sb.append(getDownloadSuccess());
        if (getHash() != null) {
            sb.append(" ], HASH => [ ");
            sb.append(getHash());
        }
        sb.append(" ].");
        sb.append(newLine);
        
        return sb.toString();
    }
    
    /**
     * Class implementing the Builder creation pattern for new UPGData objects.
     * 
     * @author L. Craig Carpenter
     */
    public static class UPGDataBuilder {
        
        private String     uuid;
        private String     icao;
        private Date       dateLastModified;
        private String     link;
        private String     filename;
        private String     hash;   // Included for JEPP data but not UPG.
        private String     sourceLink; // URL of the source data.
        private BigDecimal success; // 0 or 1 (1 represents success).
        private String     psuedoName;
        private String     type;
        
        /**
         * Method used to actually construct the UPGData object.
         * @return A constructed and validated UPGData object.
         */
        public UPGData build() throws IllegalStateException {
            UPGData object = new UPGData(this);
            validateUPGDataObject(object);
            return object;
        }
        
        /**
         * Setter method for the DATE_LAST_MODIFIED attribute.
         * @param value The DATE_LAST_MODIFIED attribute.
         */
        public UPGDataBuilder dateLastModified(Date value) {
            dateLastModified = value;
            return this;
        }
        
        /**
         * Setter method for the SUCCESS_DL attribute.
         * @param value The SUCCESS_DL attribute.
         */
        public UPGDataBuilder success(BigDecimal value) {
            success = value;
            return this;
        }
        
        /**
         * Setter method for the Hash attribute.  This attribute is provided by the 
         * target, but it is not persisted in the back-end database.
         * @param value The HASH attribute.
         */
        public UPGDataBuilder filename(String value) {
            filename = value;
            return this;
        }
        
        /**
         * Setter method for the HASH attribute.
         * @param value The HASH attribute.
         */
        public UPGDataBuilder hash(String value) {
            hash = value;
            return this;
        }
        
        /**
         * Setter method for the ICAO attribute.
         * @param value The ICAO attribute.
         */
        public UPGDataBuilder icao(String value) {
            icao = value;
            return this;
        }
        
        /**
         * Setter method for the link attribute.
         * @param value The link attribute.
         */
        public UPGDataBuilder link(String value) {
            link = value;
            return this;
        }
        
        /**
         * Setter method for the URL of the source data.
         * @param value The URL of the source data.
         */
        public UPGDataBuilder sourceLink(String value) {
            sourceLink = value;
            return this;
        }
        
        /**
         * Setter method for the PSUEDONAME attribute.
         * @param value The PSUEDONAME attribute.
         */
        public UPGDataBuilder psuedoName(String value) {
            psuedoName = value;
            return this;
        }
        
        /**
         * Setter method for the TYPE attribute.
         * @param value The TYPE attribute.
         */
        public UPGDataBuilder type(String value) {
            type = value;
            return this;
        }
        
        /**
         * Getter method for the UUID attribute.
         * @param value The UUID attribute.
         */
        public UPGDataBuilder uuid(String value) {
            uuid = value;
            return this;
        } 
        
        /**
         * Validate that all required fields are populated.
         * @param object The IntermediateUPGData object to validate.
         * @throws IllegalStateException Thrown if any of the required fields 
         * are not populated.
         */
        private void validateUPGDataObject(UPGData object) 
                throws IllegalStateException {
            
            if ((filename == null) || (filename.isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "UPGData object but the value for HASH "
                        + "was null.");
            }
            filename = filename.trim();
            
            if ((icao == null) || (icao.isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "UPGData object but the value for ICAO "
                        + "was null.");
            }
            icao = icao.trim();
            
            if ((psuedoName == null) || (psuedoName.isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "UPGData object but the value for "
                        + "PSUEDONAME was null.");
            }
            psuedoName = psuedoName.trim();
            
            if ((type == null) || (type.isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "UPGData object but the value for TYPE "
                        + "was null.");
            }
            type = type.trim();
            
            if ((uuid == null) || (uuid.isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "UPGData object but the value for UUID "
                        + "was null.");
            }
            uuid = uuid.trim();
        }
    }
}

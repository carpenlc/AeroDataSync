package mil.nga.aero.upg.model;

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Simple POJO holding metrics associated with the execution of a single
 * UPG data synchronization run. This class contains JPA annotations to 
 * ensure that the correct table is created in the data store.
 * 
 * @author L. Craig Carpenter
 */
public class Metrics implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 7910132497216831765L;
    
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
    
    private final Date   executionTime;
    private final long   sourceHoldings;
    private final long   added;
    private final long   updated;
    private final long   removed;
    private final long   failedDownloads;
    private final long   elapsedTime;
    private final long   localHoldings;
    private final String hostName;
    private final String jvmName;
    
    /**
     * Constructor used to set all of the required internal members.
     * 
     * @param builder Populated builder object.
     */
    private Metrics (MetricsBuilder builder) {
        this.executionTime   = builder.executionTime;
        this.sourceHoldings  = builder.sourceHoldings;
        this.added           = builder.added;
        this.updated         = builder.updated;
        this.removed         = builder.removed;
        this.failedDownloads = builder.failedDownloads;
        this.elapsedTime     = builder.elapsedTime;
        this.localHoldings   = builder.localHoldings;
        this.hostName        = builder.hostName;
        this.jvmName         = builder.jvmName;
    }
    
    /**
     * Getter method for the time and date of execution.
     * @return The time and date of the time of execution.
     */
    public long getElapsedTime() {
        return elapsedTime;
    }
    
    /**
     * Getter method for the time and date of execution.
     * @return The time and date of the time of execution.
     */
    public Date getExecutionTime() {
        return executionTime;
    }
    
    /**
     * Getter method for the String version of time and date of execution.
     * @return String representation of the time and date of the time of 
     * execution.
     */
    public String getExecutionTimeString() {
        return formatter.format(executionTime);
    }
    
    /**
     * Getter method for the number of products that failed to download.
     * @return The number of products that failed to download.
     */
    public long getNumFailedDownloads() {
        return failedDownloads;
    }
    
    /**
     * Return the number of products held locally.
     * @return The number of products held locally.
     */
    public long getLocalHoldings() {
        return localHoldings;
    }
    
    /**
     * Getter method for the number of products added.
     * @return The number of products added.
     */
    public long getNumProductsAdded() {
        return added;
    }
    
    /**
     * Getter method for the number of products updated.
     * @return The number of products updated.
     */
    public long getNumProductsUpdated() {
        return updated;
    }
    
    /**
     * Getter method for the number of products removed.
     * @return The number of products removed.
     */
    public long getNumProductsRemoved() {
        return removed;
    }
    
    /**
     * Return the name of the host that processed the sync.
     * @return The host name.
     */
    public String getHostName() {
        return hostName;
    }
    
    /**
     * Return the name of the actual application server that processed the 
     * sync.
     * @return The JVM server name.
     */
    public String getJvmName() {
        return jvmName;
    }
    
    /**
     * Return the number of products at the source.
     * @return The number of products held by the source.
     */
    public long getSourceHoldings() {
        return sourceHoldings;
    }
    
    public String toString() {
        StringBuilder sb      = new StringBuilder();
        String        newLine = System.getProperty("line.separator");
        
        sb.append(newLine);
        sb.append("------------------------------------------");
        sb.append(newLine);
        sb.append("Execution Time   : ");
        sb.append(formatter.format(getExecutionTime()));
        sb.append(newLine);
        sb.append("Elapsed Time     : ");
        sb.append(getElapsedTime());
        sb.append(" ms");
        sb.append(newLine);
        sb.append("Products Added   : ");
        sb.append(getNumProductsAdded());
        sb.append(newLine);
        sb.append("Products Updated : ");
        sb.append(getNumProductsUpdated());
        sb.append(newLine);
        sb.append("Products Removed : ");
        sb.append(getNumProductsRemoved());
        sb.append(newLine);
        sb.append("Failed Downloads : ");
        sb.append(getNumFailedDownloads());
        sb.append(newLine);
        sb.append("Source Holdings  : ");
        sb.append(getSourceHoldings());
        sb.append(newLine);
        sb.append("Local Holdings   : ");
        sb.append(getLocalHoldings());
        sb.append(newLine);
        sb.append("Host Name        : ");
        sb.append(getHostName());
        sb.append(newLine);
        sb.append("JVM Name         : ");
        sb.append(getJvmName());
        sb.append(newLine);
        sb.append("------------------------------------------");
        sb.append(newLine);
        
        return sb.toString();
    }
    
    /**
     * Class implementing the Builder creation pattern for new Metrics objects.
     * 
     * @author L. Craig Carpenter
     */
    public static class MetricsBuilder {
        
        private Date   executionTime   = null;
        private long   sourceHoldings  = 0;
        private long   added           = 0; 
        private long   updated         = 0;
        private long   removed         = 0;
        private long   failedDownloads = 0;
        private long   elapsedTime     = 0;
        private long   localHoldings   = 0;
        private String hostName        = "";
        private String jvmName         = "";
        
        /**
         * Method used to actually construct the Metrics object.
         * @return A constructed and validated Metrics object.
         */
        public Metrics build() throws IllegalStateException {
            Metrics object = new Metrics(this);
            validateMetricsObject(object);
            return object;
        }
        
        /**
         * Reset the internal fields.
         */
        public void initialize() {
            executionTime   = null;
            sourceHoldings  = 0;
            added           = 0; 
            updated         = 0;
            removed         = 0;
            failedDownloads = 0;
            elapsedTime     = 0;
            localHoldings   = 0;
            hostName        = "";
            jvmName         = "";
        }
        
        /**
         * Setter method for the EXECUTION_TIME attribute.
         * @param value The EXECUTION_TIME attribute.
         */
        public MetricsBuilder executionTime (Date value) {
            executionTime = value;
            return this;
        }
        /**
         * Setter method for the NUM_PRODUCTS_ADDED attribute.
         * @param value The NUM_PRODUCTS_ADDED attribute.
         */
        public MetricsBuilder added (long value) {
            added = value;
            return this;
        }
        
        /**
         * Setter method for the FAILED_DOWNLOADS attribute.
         * @param value The FAILED_DOWNLOADS attribute.
         */
        public MetricsBuilder failedDownloads (long value) {
            failedDownloads = value;
            return this;
        }
        
        /**
         * Setter method for the NUM_PRODUCTS_REMOVED attribute.
         * @param value The NUM_PRODUCTS_REMOVED attribute.
         */
        public MetricsBuilder removed (long value) {
            removed = value;
            return this;
        }
        
        /**
         * Setter method for the NUM_PRODUCTS_UPDATED attribute.
         * @param value The NUM_PRODUCTS_UPDATED attribute.
         */
        public MetricsBuilder updated (long value) {
            updated = value;
            return this;
        }
        
        /**
         * Setter method for the ELAPSED_TIME attribute.
         * @param value The ELAPSED_TIME attribute.
         */
        public MetricsBuilder elapsedTime (long value) {
            elapsedTime = value;
            return this;
        }
        
        /**
         * Setter method for the HOST_NAME attribute.
         * @param value The HOST_NAME attribute.
         */
        public MetricsBuilder hostName (String value) {
            if (value != null) {
                hostName = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the SERVER_NAME attribute.
         * @param value The SERVER_NAME attribute.
         */
        public MetricsBuilder jvmName (String value) {
            if (value != null) {
                jvmName = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the LOCAL_HOLDINGS attribute.
         * @param value The LOCAL_HOLDINGS attribute.
         */
        public MetricsBuilder localHoldings (long value) {
            localHoldings = value;
            return this;
        }
        
        /**
         * Setter method for the SOURCE_HOLDINGS attribute.
         * @param value The SOURCE_HOLDINGS attribute.
         */
        public MetricsBuilder sourceHoldings (long value) {
            sourceHoldings = value;
            return this;
        }
        
        /**
         * Validate that all fields are populated.
         * 
         * @param object The Metrics object to validate.
         * @throws IllegalStateException Thrown if any of the required fields 
         * are not populated with acceptable values.
         */
        private void validateMetricsObject(Metrics object) 
                throws IllegalStateException {
            if (!(object.getElapsedTime() > 0)) {
                throw new IllegalStateException("Invalid value for "
                        + "ELAPSED_TIME [ "
                        + object.getElapsedTime()
                        + " ].  ELAPSED_TIME must be greater than 0.");
            }
            if (object.getExecutionTime() == null) {
                throw new IllegalStateException("Invalid value for "
                        + "EXECUTION_TIME.  EXECUTION_TIME must not be null.");
            }
            if (object.getNumProductsAdded() < 0) {
                throw new IllegalStateException("Invalid value for "
                        + "NUM_PRODUCTS_ADDED [ "
                        + object.getNumProductsAdded()
                        + " ].  NUM_PRODUCTS_ADDED must be greater than or "
                        + "equal to 0.");
            }
            if (object.getNumProductsRemoved() < 0) {
                throw new IllegalStateException("Invalid value for "
                        + "NUM_PRODUCTS_REMOVED [ "
                        + object.getNumProductsRemoved()
                        + " ].  NUM_PRODUCTS_REMOVED must be greater than or "
                        + "equal to 0.");
            }
            if (object.getNumProductsUpdated() < 0) {
                throw new IllegalStateException("Invalid value for "
                        + "NUM_PRODUCTS_UPDATED [ "
                        + updated
                        + " ].  NUM_PRODUCTS_UPDATED must be greater than or "
                        + "equal to 0.");
            }
            if (object.getNumFailedDownloads() < 0) {
                throw new IllegalStateException("Invalid value for "
                        + "FAILED_DOWNLOADS [ "
                        + object.getNumFailedDownloads()
                        + " ].  FAILED_DOWNLOADS must be greater than or "
                        + "equal to 0.");
            }
            if (object.getLocalHoldings() < 0) {
                throw new IllegalStateException("Invalid value for "
                        + "LOCAL_HOLDINGS [ "
                        + object.getLocalHoldings()
                        + " ].  LOCAL_HOLDINGS must be greater than or "
                        + "equal to 0.");
            }
            if (object.getSourceHoldings() < 0) {
                throw new IllegalStateException("Invalid value for "
                        + "SOURCE_HOLDINGS [ "
                        + object.getSourceHoldings()
                        + " ].  SOURCE_HOLDINGS must be greater than or "
                        + "equal to 0.");
            }
            if ((object.getHostName() == null) || (object.getHostName().isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "Metrics object but the value for "
                        + "HOST_NAME was null.");
            }
            if ((object.getJvmName() == null) || (object.getJvmName().isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "Metrics object but the value for "
                        + "SERVER_NAME was null.");
            }
        }
    }
}

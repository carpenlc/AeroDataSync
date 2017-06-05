package mil.nga.aero.jepp.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import mil.nga.aero.interfaces.AeroDataMetricsStoreI;
import mil.nga.aero.upg.model.Metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class JDBCMetricsServices
 * 
 * This class provides methods used to interact with the 
 * JEPP_SYNCHRONIZATION_METRICS table used to keep track of metrics 
 * associated with individual runs of the UPG synchronization process.
 */
@Stateless
@LocalBean
public class JDBCJEPPMetricsService 
        implements AeroDataMetricsStoreI, Serializable {

    /*
     CREATE TABLE JEPP_SYNCHRONIZATION_METRICS (
         EXECUTION_TIME       TIMESTAMP NOT NULL,
         SOURCE_HOLDINGS      NUMBER(38) NOT NULL,
         NUM_PRODUCTS_ADDED   NUMBER(38) NOT NULL,
         NUM_PRODUCTS_UPDATED NUMBER(38) NOT NULL,
         NUM_PRODUCTS_REMOVED NUMBER(38) NOT NULL,
        NUM_FAILED_DOWNLOADS NUMBER(38) NOT NULL,
        LOCAL_HOLDINGS       NUMBER(38) NOT NULL,
        ELAPSED_TIME         NUMBER(38) NOT NULL,
        HOST_NAME            VARCHAR2(100),
        SERVER_NAME          VARCHAR2(100)
     )
     */
    
    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -6519926677868335796L;

    /**
     * The name of the target table in which JEPP metrics will be stored.
     */
    private static final String METRICS_TABLE = "JEPP_SYNCH_METRICS";
    
    /**
     * Set up the logging system for use throughout the class
     */        
    private static final Logger LOGGER = LoggerFactory.getLogger(
            JDBCJEPPMetricsService.class);
    
    /**
     * Container-injected datasource object.
     */
    @Resource(mappedName="java:jboss/datasources/ACES")
    DataSource datasource;
    
    /**
     * Default constructor. 
     */
    public JDBCJEPPMetricsService() { }

    /**
     * Persist (insert) the information associated with the input 
     * <code>JEPP_SYNCHRONIZATION_METRICS</code> object.
     * 
     * @param metrics <code>JEPP_SYNCHRONIZATION_METRICS</code> object containing 
     * updated metrics information.
     */
    public void insert(Metrics metrics) {
        
        Connection        conn   = null;
        PreparedStatement stmt   = null;
        long              start  = System.currentTimeMillis();
        String            sql    = "insert into "
                + METRICS_TABLE
                + " (EXECUTION_TIME, SOURCE_HOLDINGS, NUM_PRODUCTS_ADDED, "
                + "NUM_PRODUCTS_UPDATED, NUM_PRODUCTS_REMOVED, "
                + "NUM_FAILED_DOWNLOADS, LOCAL_HOLDINGS, ELAPSED_TIME, "
                + "HOST_NAME, SERVER_NAME ) values "
                + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        if (datasource != null) {
            if (metrics != null) {
                    
                try { 
                    
                    conn = datasource.getConnection();
                    stmt = conn.prepareStatement(sql);
                    
                    stmt.setTimestamp(   1,  new Timestamp(metrics.getExecutionTime().getTime()));
                    stmt.setInt(    2,  metrics.getSourceHoldings());
                    stmt.setInt(    3,  metrics.getNumProductsAdded());
                    stmt.setInt(    4,  metrics.getNumProductsUpdated());
                    stmt.setInt(    5,  metrics.getNumProductsRemoved());
                    stmt.setInt(    6,  metrics.getNumFailedDownloads());
                    stmt.setInt(    7,  metrics.getLocalHoldings());
                    stmt.setLong(   8,  metrics.getElapsedTime());
                    stmt.setString( 9,  metrics.getHostName());
                    stmt.setString( 10, metrics.getJvmName());
  
                    stmt.executeUpdate();
                    
                }
                catch (SQLException se) {
                    LOGGER.error("An unexpected SQLException was raised while "
                            + "attempting to insert a new "
                            + "JEPP_SYNCHRONIZATION_METRICS object.  "
                            + "Error message [ "
                            + se.getMessage() 
                            + " ].");
                }
                finally {
                    try { 
                        if (stmt != null) { stmt.close(); } 
                    } catch (Exception e) {}
                    try { 
                        if (conn != null) { conn.close(); } 
                    } catch (Exception e) {}
                }
            }
        }
        else {
            LOGGER.warn("DataSource object not injected by the container.  "
                    + "An empty List will be returned to the caller.");
        }
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Insert of JEPP_SYNCHRONIZATION_METRICS record "
                    + " completed in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
    }
    
}

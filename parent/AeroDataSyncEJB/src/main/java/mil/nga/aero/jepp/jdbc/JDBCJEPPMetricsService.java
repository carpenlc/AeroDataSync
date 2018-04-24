package mil.nga.aero.jepp.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
 * JEPP_SYNC_METRICS table used to keep track of metrics 
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
    private static final String METRICS_TABLE = "JEPP_SYNC_METRICS";
    
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
                    
                    stmt.setTimestamp(1,  new Timestamp(metrics.getExecutionTime().getTime()));
                    stmt.setLong(     2,  metrics.getSourceHoldings());
                    stmt.setLong(     3,  metrics.getNumProductsAdded());
                    stmt.setLong(     4,  metrics.getNumProductsUpdated());
                    stmt.setLong(     5,  metrics.getNumProductsRemoved());
                    stmt.setLong(     6,  metrics.getNumFailedDownloads());
                    stmt.setLong(     7,  metrics.getLocalHoldings());
                    stmt.setLong(     8,  metrics.getElapsedTime());
                    stmt.setString(   9,  metrics.getHostName());
                    stmt.setString(   10, metrics.getJvmName());
  
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
            LOGGER.debug("Insert of "
                    + METRICS_TABLE
                    + " record completed in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
    }
    
    /**
     * Select a list containing all of the elements from the target table.
     * @return A list of all metrics records from the target table.
     */
    public List<Metrics> select() {
        
        Connection        conn       = null;
        List<Metrics>     metrics    = new ArrayList<Metrics>();
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;
        long              startTime  = System.currentTimeMillis();
        String            sql        = "select "
                + "EXECUTION_TIME, SOURCE_HOLDINGS, NUM_PRODUCTS_ADDED, "
                + "NUM_PRODUCTS_UPDATED, NUM_PRODUCTS_REMOVED, "
                + "NUM_FAILED_DOWNLOADS, LOCAL_HOLDINGS, ELAPSED_TIME, "
                + "HOST_NAME, SERVER_NAME from "
                + METRICS_TABLE;
        
        if (datasource != null) {
            try {
                
                conn = datasource.getConnection();
                stmt = conn.prepareStatement(sql);
                rs   = stmt.executeQuery();
                
                while (rs.next()) {
                    Metrics entry = new Metrics.MetricsBuilder()
                            .executionTime(rs.getTimestamp("EXECUTION_TIME"))
                            .sourceHoldings(rs.getInt("SOURCE_HOLDINGS"))
                            .added(rs.getLong("NUM_PRODUCTS_ADDED"))
                            .updated(rs.getLong("NUM_PRODUCTS_UPDATED"))
                            .removed(rs.getLong("NUM_PRODUCTS_REMOVED"))
                            .failedDownloads(rs.getLong("NUM_FAILED_DOWNLOADS"))
                            .localHoldings(rs.getLong("LOCAL_HOLDINGS"))
                            .elapsedTime(rs.getLong("ELAPSED_TIME"))
                            .hostName(rs.getString("HOST_NAME"))
                            .jvmName(rs.getString("SERVER_NAME"))
                            .build();
                     metrics.add(entry);
                }
            }
            catch (SQLException se) {
                LOGGER.error("An unexpected SQLException was raised while "
                        + "attempting to retrieve a list of Metrics from the "
                        + "target data source.  Error message [ "
                        + se.getMessage() 
                        + " ].");
            }
            finally {
                try { 
                    if (rs != null) { rs.close(); } 
                } catch (Exception e) {}
                try { 
                    if (stmt != null) { stmt.close(); } 
                } catch (Exception e) {}
                try { 
                    if (conn != null) { conn.close(); } 
                } catch (Exception e) {}
            }
        }
        else {
            LOGGER.warn("DataSource object not injected by the container.  "
                    + "An empty List will be returned to the caller.");
        }
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("[ "
                    + metrics.size() 
                    + " ] metrics records selected in [ "
                    + (System.currentTimeMillis() - startTime) 
                    + " ] ms.");
        }
        return metrics;
        
    }
    
    /**
     * This method will return a list of all 
     * <code>mil.nga.aero.upg.model.Metrics</code> objects currently 
     * persisted in the back-end data store that fall between the input 
     * start and end time.
     * 
     * @param startTime The "from" parameter 
     * @param endTime The "to" parameter
     * @return All of the metrics records executed between the input start 
     * and end times.  
     */
    public List<Metrics> select(long start, long end) {
        
        Connection        conn       = null;
        List<Metrics>     metrics    = new ArrayList<Metrics>();
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;
        long              startTime  = System.currentTimeMillis();
        String            sql        = "select "
                + "EXECUTION_TIME, SOURCE_HOLDINGS, NUM_PRODUCTS_ADDED, "
                + "NUM_PRODUCTS_UPDATED, NUM_PRODUCTS_REMOVED, "
                + "NUM_FAILED_DOWNLOADS, LOCAL_HOLDINGS, ELAPSED_TIME, "
                + "HOST_NAME, SERVER_NAME from "
                + METRICS_TABLE
                + " where EXECUTION_TIME > ? and EXECUTION_TIME < ? "
                + "order by EXECUTION_TIME desc";
        
        // Ensure the startTime is earlier than the endTime before submitting
        // the query to the database.
        if (start > end) {
                LOGGER.warn("The caller supplied a start time that falls "
                        + "after the end time.  Swapping start and end "
                        + "times.");
                long temp = start;
                start = end;
                end = temp;
        }
        else if (start == end) {
            LOGGER.warn("The caller supplied the same time for both start "
                    + "and end time.  This method will likely yield a null "
                    + "job list.");
        }
        
        if (datasource != null) {
            try {
                
                conn = datasource.getConnection();
                stmt = conn.prepareStatement(sql);
                stmt.setTimestamp(1, new Timestamp(start));
                stmt.setTimestamp(2, new Timestamp(end));
                rs   = stmt.executeQuery();
                
                while (rs.next()) {
                    Metrics entry = new Metrics.MetricsBuilder()
                            .executionTime(rs.getTimestamp("EXECUTION_TIME"))
                            .sourceHoldings(rs.getInt("SOURCE_HOLDINGS"))
                            .added(rs.getLong("NUM_PRODUCTS_ADDED"))
                            .updated(rs.getLong("NUM_PRODUCTS_UPDATED"))
                            .removed(rs.getLong("NUM_PRODUCTS_REMOVED"))
                            .failedDownloads(rs.getLong("NUM_FAILED_DOWNLOADS"))
                            .localHoldings(rs.getLong("LOCAL_HOLDINGS"))
                            .elapsedTime(rs.getLong("ELAPSED_TIME"))
                            .hostName(rs.getString("HOST_NAME"))
                            .jvmName(rs.getString("SERVER_NAME"))
                            .build();
                     metrics.add(entry);
                }
            }
            catch (SQLException se) {
                LOGGER.error("An unexpected SQLException was raised while "
                        + "attempting to retrieve a list of Metrics from the "
                        + "target data source.  Error message [ "
                        + se.getMessage() 
                        + " ].");
            }
            finally {
                try { 
                    if (rs != null) { rs.close(); } 
                } catch (Exception e) {}
                try { 
                    if (stmt != null) { stmt.close(); } 
                } catch (Exception e) {}
                try { 
                    if (conn != null) { conn.close(); } 
                } catch (Exception e) {}
            }
        }
        else {
            LOGGER.warn("DataSource object not injected by the container.  "
                    + "An empty List will be returned to the caller.");
        }
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("[ "
                    + metrics.size() 
                    + " ] metrics records selected in [ "
                    + (System.currentTimeMillis() - startTime) 
                    + " ] ms.");
        }
        return metrics;
    }
}

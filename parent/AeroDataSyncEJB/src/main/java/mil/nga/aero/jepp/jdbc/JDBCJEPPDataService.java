package mil.nga.aero.jepp.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import mil.nga.aero.interfaces.AeroDataStoreI;
import mil.nga.aero.upg.model.UPGData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class JDBCUPGDataServices
 */
@Stateless
@LocalBean
public class JDBCJEPPDataService implements AeroDataStoreI {

    /**
     * Set up the logging system for use throughout the class
     */        
    private static final Logger LOGGER = LoggerFactory.getLogger(
            JDBCJEPPDataService.class);
    
    /**
     * Container-injected datasource object.
     */
    @Resource(mappedName="java:jboss/datasources/ACES")
    DataSource datasource;
    
    /**
     * Default constructor. 
     */
    public JDBCJEPPDataService() { }

    /**
     * Delete all records that match the input UUID.
     * 
     * @param uuid The UUID to be deleted.
     */
    public void deleteData(String uuid) {
        
        Connection        conn   = null;
        PreparedStatement stmt   = null;
        long              start  = System.currentTimeMillis();
        String            sql    = "delete from AEROBROWSER_JEPP where "
                + "UUID = ?";
        
        if (datasource != null) {
            if ((uuid != null) && (!uuid.isEmpty())) {
                
                try { 
                    conn = datasource.getConnection();
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, uuid);
                    stmt.executeUpdate();
                }
                catch (SQLException se) {
                    LOGGER.error("An unexpected SQLException was raised "
                            + "while attempting to delete AEROBROWSER_JEPP "
                            + "records associated with UUID [ "
                            + uuid
                            + " ].  Error message [ "
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
            else {
                LOGGER.warn("The input UUID is null or empty.  Unable to "
                        + "delete the target JEPP data record.");
            }
        }
        else {
            LOGGER.warn("DataSource object not injected by the container.  "
                    + "An empty List will be returned to the caller.");
        }
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("AEROBROWSER_JEPP records deleted in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }    
    }
    
    /**
     * Retrieve a complete list of unique ICAO fields from the data store.
     * @return A list of ICAOs
     */
    public List<String> getICAOList() {
    	
        Connection        conn   = null;
        List<String>      icaos  = new ArrayList<String>();
        PreparedStatement stmt   = null;
        ResultSet         rs     = null;
        long              start  = System.currentTimeMillis();
        String            sql    = "select distinct(ICAO) from "
        		+ "AEROBROWSER_JEPP order by ICAO";
        
        if (datasource != null) {
            
            try {
                conn = datasource.getConnection();
                stmt = conn.prepareStatement(sql);
                rs   = stmt.executeQuery();
                while (rs.next()) {
                    icaos.add(rs.getString("ICAO"));
                }
            }
            catch (SQLException se) {
                
                LOGGER.error("An unexpected SQLException was raised while "
                        + "attempting to retrieve a list of ICAOs from the "
                        + "target data source.  Error message => [ "
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
                    + icaos.size() 
                    + " ] ICAOs selected in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
        
        return icaos;
    }
    
    /**
     * Retrieve a complete list of unique TYPE fields from the data store.
     * @return A list of TYPEs
     */
    public List<String> getTypeList() {
    	
        Connection        conn   = null;
        List<String>      types  = new ArrayList<String>();
        PreparedStatement stmt   = null;
        ResultSet         rs     = null;
        long              start  = System.currentTimeMillis();
        String            sql    = "select distinct(TYPE) from "
        		+ "AEROBROWSER_JEPP order by TYPE";
        
        if (datasource != null) {
            
            try {
                conn = datasource.getConnection();
                stmt = conn.prepareStatement(sql);
                rs   = stmt.executeQuery();
                while (rs.next()) {
                    types.add(rs.getString("TYPE"));
                }
            }
            catch (SQLException se) {
                
                LOGGER.error("An unexpected SQLException was raised while "
                        + "attempting to retrieve a list of TYPEs from the "
                        + "target data source.  Error message => [ "
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
                    + types.size() 
                    + " ] TYPEs selected in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
        
        return types;
    	
    }
    
    /**
     * Retrieve a complete list of UUIDs from the data store.
     * @return A list of UUIDs
     */
    public List<String> getUUIDList() {
        
        Connection        conn   = null;
        List<String>      UUIDs  = new ArrayList<String>();
        PreparedStatement stmt   = null;
        ResultSet         rs     = null;
        long              start  = System.currentTimeMillis();
        String            sql    = "select UUID from AEROBROWSER_JEPP";
        
        if (datasource != null) {
            
            try {
                conn = datasource.getConnection();
                stmt = conn.prepareStatement(sql);
                rs   = stmt.executeQuery();
                while (rs.next()) {
                    UUIDs.add(rs.getString("UUID"));
                }
            }
            catch (SQLException se) {
                
                LOGGER.error("An unexpected SQLException was raised while "
                        + "attempting to retrieve a list of UUIDs from the "
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
                    + UUIDs.size() 
                    + " ] UUIDs selected in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
        
        return UUIDs;
    }
    
    /**
     * Select a UPGData record from the data store based on the input UUID.
     * 
     * @param uuid UUID to retrieve. 
     * @return The UPGData record.  Null if not found.
     */
    public UPGData getData(String uuid) {
        
        UPGData           upg      = null;
        Connection        conn     = null;
        PreparedStatement stmt     = null;
        ResultSet         rs       = null;
        long              start    = System.currentTimeMillis();
        String            sql      = "select UUID, ICAO, "
                + "DATE_LAST_MODIFIED, LINK, FILENAME, SUCCESS_DL, "
                + "PSEUDONAME, TYPE, LEIDOS_LINK, HASH from "
                + "AEROBROWSER_JEPP where UUID = ? ";
        
        if (datasource != null) {
            if ((uuid != null) && (!uuid.isEmpty())) {
                
                try { 
                    
                    conn = datasource.getConnection();
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, uuid);
                    rs   = stmt.executeQuery();
                    
                    if (rs.next()) {
                        
                        upg = new UPGData.UPGDataBuilder()
                                .uuid(rs.getString("UUID"))
                                .icao(rs.getString("ICAO"))
                                .dateLastModified(rs.getDate("DATE_LAST_MODIFIED"))
                                .link(rs.getString("LINK"))
                                .filename(rs.getString("FILENAME"))
                                .success(rs.getLong("SUCCESS_DL"))
                                .psuedoName(rs.getString("PSUEDONAME"))
                                .type(rs.getString("TYPE"))
                                .sourceLink(rs.getString("LEIDOS_LINK"))
                                .hash(rs.getString("HASH"))
                                .build();
    
                    }
                    else {
                        LOGGER.info("No matching AEROBROWSER_JEPP records were "
                                + "found for UUID [ "
                                + uuid
                                + " ].");
                    }
                }
                catch (SQLException se) {
                    LOGGER.error("An unexpected SQLException was raised while "
                            + "attempting to retrieve a AEROBROWSER_JEPP "
                            + "record for UUID [ "
                            + uuid
                            + " ] from the target data source.  Error "
                            + "message [ "
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
                LOGGER.warn("The input UUID is null or empty.  Unable to "
                        + "retrieve the list of individual archives.");
            }
        }
        else {
            LOGGER.warn("DataSource object not injected by the container.  "
                    + "An empty List will be returned to the caller.");
        }
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("AEROBROWSER_JEPP record for UUID [ " 
                    + uuid 
                    + " ] selected in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
        return upg;
    }
    
    /**
     * Select the number of failed downloads from the data store.
     * 
     * @return The number of failed downloads.
     */
    public int getNumFailedDownloads() {
        
        int               count    = 0;
        Connection        conn     = null;
        PreparedStatement stmt     = null;
        ResultSet         rs       = null;
        long              start    = System.currentTimeMillis();
        String            sql      = "select COUNT(*) "
                + "from AEROBROWSER_JEPP where SUCCESS_DL = ?";
        
        if (datasource != null) {
            try { 
                conn = datasource.getConnection();
                stmt = conn.prepareStatement(sql);
                stmt.setLong(1, 0);
                rs   = stmt.executeQuery();
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
            catch (SQLException se) {
                LOGGER.error("An unexpected SQLException was raised while "
                        + "attempting to retrieve the number of products "
                        + "that failed to download from the AEROBROWSER_JEPP "
                        + "table. Error message [ "
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
            LOGGER.debug("Count of records in the AEROBROWSER_JEPP table " 
                    + "obtained in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
        return count;
    }
    
    /**
     * Select the number of  UPGData records from the data store.
     * 
     * @return The number of rows in the data store.
     */
    public int getNumProducts() {
        
        int               count    = 0;
        Connection        conn     = null;
        PreparedStatement stmt     = null;
        ResultSet         rs       = null;
        long              start    = System.currentTimeMillis();
        String            sql      = "select COUNT(*) "
                + "from AEROBROWSER_JEPP";
        
        if (datasource != null) {
                
            try { 
                
                conn = datasource.getConnection();
                stmt = conn.prepareStatement(sql);
                rs   = stmt.executeQuery();
                
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
            catch (SQLException se) {
                LOGGER.error("An unexpected SQLException was raised while "
                        + "attempting to retrieve the number of records in "
                        + "the AEROBROWSER_JEPP table. Error message [ "
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
            LOGGER.debug("Count of records in the AEROBROWSER_JEPP table " 
                    + "obtained in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
        return count;
    }
    
    /**
     * Get a list of all UPGData records from the data store.
     * 
     * @return A Map object containing All JEPP data records in the 
     * back-end data store.  The key is the UUID and the value is the 
     * UPGData POJO.
     */
    public Map<String, UPGData> getData() {
        
        Map<String, UPGData> upgData  = new HashMap<String, UPGData>();
        Connection           conn     = null;
        PreparedStatement    stmt     = null;
        ResultSet            rs       = null;
        long                 start    = System.currentTimeMillis();
        String               uuid     = null;
        String               sql      = "select UUID, ICAO, "
                + "DATE_LAST_MODIFIED, LINK, FILENAME, SUCCESS_DL, "
                + "PSEUDONAME, TYPE, LEIDOS_LINK, HASH from AEROBROWSER_JEPP "
                + "order by DATE_LAST_MODIFIED desc";
        
        if (datasource != null) {
                
            try { 
                
                conn = datasource.getConnection();
                stmt = conn.prepareStatement(sql);
                rs   = stmt.executeQuery();
                
                while (rs.next()) {
                    uuid = rs.getString("UUID");
                    UPGData upg = new UPGData.UPGDataBuilder()
                            .uuid(uuid)
                            .icao(rs.getString("ICAO"))
                            .dateLastModified(rs.getDate("DATE_LAST_MODIFIED"))
                            .link(rs.getString("LINK"))
                            .filename(rs.getString("FILENAME"))
                            .success(rs.getLong("SUCCESS_DL"))
                            .psuedoName(rs.getString("PSUEDONAME"))
                            .type(rs.getString("TYPE"))
                            .sourceLink(rs.getString("LEIDOS_LINK"))
                            .hash(rs.getString("HASH"))
                            .build();
                    upgData.put(uuid, upg);
                }
            }
            catch (SQLException se) {
                LOGGER.error("An unexpected SQLException was raised while "
                        + "attempting to retrieve all AEROBROWSER_JEPP "
                        + "records from the target data source.  Error "
                        + "message [ "
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
            LOGGER.error("DataSource object not injected by the container.  "
                    + "An empty List will be returned to the caller.");
        }
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("[ " 
                    + upgData.size()
                    + " ] AEROBROWSER_JEPP records selected in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
        return upgData;
    }
    
    /**
     * Persist (insert) the information associated with the input 
     * <code>AEROBROWSER_JEPP</code> object.
     * 
     * @param file <code>AEROBROWSER_JEPP</code> object containing updated state 
     * information.
     */
    public void insertData(UPGData data) {
        
        Connection        conn   = null;
        PreparedStatement stmt   = null;
        long              start  = System.currentTimeMillis();
        String            sql    = "insert into AEROBROWSER_JEPP ("
                + "UUID, ICAO, DATE_LAST_MODIFIED, LINK, FILENAME, "
                + "SUCCESS_DL, PSEUDONAME, TYPE, LEIDOS_LINK, HASH) values "
                + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        if (datasource != null) {
            if (data != null) {
                    
                try { 
                    
                    conn = datasource.getConnection();
                    stmt = conn.prepareStatement(sql);
                    
                    stmt.setString( 1, data.getUUID());
                    stmt.setString( 2, data.getICAO());
                    stmt.setDate(   3, data.getDateLastModified());
                    stmt.setString( 4, data.getLink());
                    stmt.setString( 5, data.getFilename());
                    stmt.setLong(   6, data.getDownloadSuccess());
                    stmt.setString( 7, data.getPsuedoName());
                    stmt.setString( 8, data.getType());
                    stmt.setString( 9, data.getSourceLink());
                    stmt.setString(10, data.getHash());
                    stmt.executeUpdate();
                    
                }
                catch (SQLException se) {
                    LOGGER.error("An unexpected SQLException was raised while "
                            + "attempting to insert a new AEROBROWSER_JEPP object.  "
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
            LOGGER.debug("Insert of AEROBROWSER_JEPP for UUID [ "
                    + data.getUUID()
                    + " ] completed in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
    }
    
    /**
     * Persist (update) the information associated with the input 
     * <code>AEROBROWSER_JEPP</code> object.
     * 
     * @param file <code>AEROBROWSER_JEPP</code> object containing updated state 
     * information.
     */
    public int updateData(UPGData data) {
    	
        int               numRecords = 0;
        Connection        conn       = null;
        PreparedStatement stmt       = null;
        long              start      = System.currentTimeMillis();
        String            sql        = "update AEROBROWSER_JEPP set "
                + "ICAO = ?, DATE_LAST_MODIFIED = ?, LINK = ?, "
                + "FILENAME = ? , SUCCESS_DL = ?, PSEUDONAME = ?, "
                + "TYPE = ?, LEIDOS_LINK = ?, HASH = ? where UUID = ?";
        
        if (datasource != null) {
            if (data != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Updating database record [ "
                            + data.toString()
                            + " ].");
                }
                try { 
                    conn = datasource.getConnection();
                    stmt = conn.prepareStatement(sql);
                    stmt.setString( 1, data.getICAO());
                    stmt.setDate(   2, data.getDateLastModified());
                    stmt.setString( 3, data.getLink());
                    stmt.setString( 4, data.getFilename());
                    stmt.setLong(   5, data.getDownloadSuccess());
                    stmt.setString( 6, data.getPsuedoName());
                    stmt.setString( 7, data.getType());
                    stmt.setString( 8, data.getSourceLink());
                    stmt.setString( 9, data.getHash());
                    stmt.setString(10, data.getUUID());
                    numRecords = stmt.executeUpdate();
                }
                catch (SQLException se) {
                    LOGGER.error("An unexpected SQLException was raised while "
                            + "attempting to update AEROBROWSER_JEPP object with "
                            + "UUID [ "
                            + data.getUUID()
                            + " ].  Error message [ "
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
            LOGGER.debug("Update of AEROBROWSER_JEPP record with UUID [ "
                    + data.getUUID()
                    + " ] completed in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
        return numRecords;
    }
}

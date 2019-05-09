package mil.nga.aero;

import java.lang.management.ManagementFactory;
import java.util.Properties;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import mil.nga.aero.interfaces.AeroDataMetricsStoreI;
import mil.nga.aero.interfaces.AeroDataServiceI;
import mil.nga.aero.interfaces.AeroDataStoreI;
import mil.nga.aero.interfaces.AeroDataUpdateServiceI;
import mil.nga.aero.jepp.JEPPDataService;
import mil.nga.aero.jepp.JEPPDataUpdateService;
import mil.nga.aero.jepp.jdbc.JDBCJEPPDataService;
import mil.nga.aero.jepp.jdbc.JDBCJEPPMetricsService;

import mil.nga.aero.upg.UPGDataService;
import mil.nga.aero.upg.UPGDataUpdateService;
import mil.nga.aero.upg.jdbc.JDBCUPGDataService;
import mil.nga.aero.upg.jdbc.JDBCUPGMetricsService;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience class used by the Web tier to look up EJB references within
 * the container.  This class is specific to the JBoss/Wildfly application
 * containers.  This was initially developed because JBoss EAP 6.x does not 
 * support EJB injection into the Web tier.
 * 
 * @author L. Craig Carpenter
 */
public class EJBClientUtilities {

    /**
     * Set up the LogBack system for use throughout the class
     */        
    static final Logger LOGGER = LoggerFactory.getLogger(
            EJBClientUtilities.class);
    
    /**
     * Handle to the container JNDI Context.
     */
    private static Context initialContext;
    
    /**
     * The specific JNDI interface to look up.
     */
    private static final String PKG_INTERFACES = 
    		"org.jboss.naming.remote.client.InitialContextFactory";
    
    /**
     * The server MBean name used for obtaining information about the running 
     * server. 
     */
    private static final String SERVER_MBEAN_OBJECT_NAME = 
            "jboss.as:management-root=server";

    /**
     * MBean attribute that contains the JVM server name.
     */
    private static final String SERVER_NAME_ATTRIBUTE = "name";
    
    /**
     * The name of the EAR file in which the EJBs are packaged.
     */
    private static final String EAR_APPLICATION_NAME = "AeroDataSync";
    
    /**
     * The name of the module (i.e. JAR) containing the EJBs
     */
    private static final String EJB_MODULE_NAME = "AeroDataSyncEJB";
    
    private boolean typeCorrect(String returnedType, String expectedType) {
        boolean correct = false;
        if ((returnedType != null) && (!returnedType.isEmpty())) {
            String temp;
            if (returnedType.contains("$$$")) {
                temp = returnedType.substring(0, returnedType.indexOf("$$$"));
            }
            else {
                temp = returnedType;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Comparing return type [ "
                        + temp
                        + " ] with expected type [ "
                        + expectedType
                        + " ].");
            }
            correct = temp.equalsIgnoreCase(expectedType);
        }
        return correct;
    }
    
    /**
     * Construct the JBoss appropriate JNDI lookup name for the input Class
     * object.
     * 
     * @param clazz EJB class reference we want to look up.
     * @return The JBoss appropriate JNDI lookup name.
     */
    private String getJNDIName(Class<?> clazz) {
        
        String appName = EAR_APPLICATION_NAME;
        String moduleName = EJB_MODULE_NAME;
        
        // String distinctName = "";
        String beanName = clazz.getSimpleName();
        String interfaceName = clazz.getName();
        
        // The following lookup is when using a local/remote interface
        // view.
        // String name = "ejb:" 
        //        + appName + "/" 
        //        + moduleName + "/" 
        //        + distinctName + "/" 
        //        + beanName + "!" + interfaceName;

        // When using a no-interface view for the beans, the following is the
        // lookup.
        String name = "java:global/" 
                + appName + "/"
                + moduleName + "/"
                + beanName + "!" + interfaceName;
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Looking up [ "
                    + name
                    + " ].");
        }
        return name;
    }
    
    /**
     * Construct the JBoss appropriate JNDI lookup name for the input Class
     * object.
     * 
     * @param clazz EJB class reference we want to look up.
     * @return The JBoss appropriate JNDI lookup name.
     */
    private String getJNDIName(Class<?> clazz, Class<?> interfaceClazz) {
        
        String appName = EAR_APPLICATION_NAME;
        String moduleName = EJB_MODULE_NAME;
        
        // String distinctName = "";
        String beanName = clazz.getSimpleName();
        String interfaceName = interfaceClazz.getName();
        
        // The following lookup is when using a local/remote interface
        // view.
        // String name = "ejb:" 
        //        + appName + "/" 
        //        + moduleName + "/" 
        //        + distinctName + "/" 
        //        + beanName + "!" + interfaceName;

        // When using a no-interface view for the beans, the following is the
        // lookup.
        String name = "java:global/" 
                + appName + "/"
                + moduleName + "/"
                + beanName + "!" + interfaceName;
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Looking up [ "
                    + name
                    + " ].");
        }
        return name;
    }
    
    /**
     * Return the raw reference to the target EJB.
     * 
     * @param clazz The Class reference to look up.
     * @return The superclass (Object) reference to the target EJB. 
     */
    private Object getEJB(Class<?> clazz, Class<?> interfaceClazz) {
        
        Object ejb  = null;
        String name = getJNDIName(clazz, interfaceClazz);
        
        try {
            Context ctx = getInitialContext();
            if (ctx != null) {
                ejb =  ctx.lookup(name);
            }
            else {
                LOGGER.error("Unable to look up the InitialContext.  See "
                        + "previous errors for more information.");
            }
        }
        catch (NamingException ne) {
            LOGGER.error("Unexpected NamingException attempting to "
                    + "look up EJB [ "
                    + name
                    + " ].  Error encountered [ "
                    + ne.getMessage()
                    + " ].");
        }
        return ejb;
    }
    
    /**
     * Return the raw reference to the target EJB.
     * 
     * @param clazz The Class reference to look up.
     * @return The superclass (Object) reference to the target EJB. 
     */
    private Object getEJB(Class<?> clazz) {
        
        Object ejb  = null;
        String name = getJNDIName(clazz);
        
        try {
            Context ctx = getInitialContext();
            if (ctx != null) {
                ejb =  ctx.lookup(name);
            }
            else {
                LOGGER.error("Unable to look up the InitialContext.  See "
                        + "previous errors for more information.");
            }
        }
        catch (NamingException ne) {
            LOGGER.error("Unexpected NamingException attempting to "
                    + "look up EJB [ "
                    + name
                    + " ].  Error encountered [ "
                    + ne.getMessage()
                    + " ].");
        }
        return ejb;
    }
    
    /**
     * Simple method used to get the initial context used by nearly all
     * of the methods in this class.
     * 
     * @return Reference to the InitialContext.
     * @throws NamingException Thrown if there are problems encountered while
     * obtaining the InitialContext.
     */
    private Context getInitialContext() throws NamingException {
        
        if (initialContext == null) {
            Properties properties = new Properties();
            properties.put(Context.URL_PKG_PREFIXES, PKG_INTERFACES);
            properties.put("jboss.naming.client.ejb.context", true);
            initialContext = new InitialContext(properties);
        }
        return initialContext;
    }
    
    /**
     * Accessor method for the singleton instance of the ClientUtility class.
     * 
     * @return The singleton instance of the ClientUtility class.
     */
    public static EJBClientUtilities getInstance() {
        return EJBClientUtilitiesHolder.getSingleton();
    }    
    
    /**
     * Utility method used to look up the JDBCJEPPDataServices interface.  
     * This method is only called by the web tier.
     * 
     * @return The JDBCJEPPDataServices interface, or null if we couldn't 
     * look it up.
     */
    public AeroDataStoreI getJDBCJEPPDataService() {
        
        AeroDataStoreI service = null;
        Object         ejb     = getEJB(
                                    JDBCJEPPDataService.class, 
                                    AeroDataStoreI.class);
        
        if (ejb != null) {
            if (ejb instanceof mil.nga.aero.interfaces.AeroDataStoreI) {
                service = (AeroDataStoreI)ejb;
            }
            else {
                LOGGER.error("Unable to look up EJB [ "
                        + getJNDIName(
                                JDBCJEPPDataService.class, 
                                AeroDataStoreI.class)
                        + " ] returned reference was the wrong type.  "
                        + "Type returned [ "
                        + ejb.getClass().getCanonicalName()
                        + " ].");
            }
        }
        else {
            LOGGER.error("Unable to look up EJB [ "
                    + getJNDIName(
                            JDBCJEPPDataService.class, 
                            AeroDataStoreI.class)
                    + " ] returned reference was null.");
        }
        return service;
    }
    
    /**
     * Utility method used to look up the JDBCUPGDataServices interface.  
     * This method is only called by the web tier.
     * 
     * @return The JDBCUPGDataServices interface, or null if we couldn't 
     * look it up.
     */
    public AeroDataStoreI getJDBCUPGDataService() {
        
        AeroDataStoreI service = null;
        Object         ejb     = getEJB(
                                    JDBCUPGDataService.class, 
                                    AeroDataStoreI.class);
        
        if (ejb != null) {
            if (ejb instanceof mil.nga.aero.interfaces.AeroDataStoreI) {
                service = (AeroDataStoreI)ejb;
            }
            else {
                LOGGER.error("Unable to look up EJB [ "
                        + getJNDIName(
                                JDBCUPGDataService.class, 
                                AeroDataStoreI.class)
                        + " ] returned reference was the wrong type.  "
                        + "Type returned [ "
                        + ejb.getClass().getCanonicalName()
                        + " ].");
            }
        }
        else {
            LOGGER.error("Unable to look up EJB [ "
                    + getJNDIName(
                            JDBCUPGDataService.class, 
                            AeroDataStoreI.class)
                    + " ] returned reference was null.");
        }
        return service;
    }
    
    /**
     * Utility method used to look up the HashGeneratorService interface.  
     * This method is only called by the web tier.
     * 
     * @return The HashGeneratorService interface, or null if we couldn't 
     * look it up.
     */
    public DataService getDataService() {
        
        DataService service = null;
        Object         ejb     = getEJB(DataService.class);
        
        if (ejb != null) {
            if (ejb instanceof mil.nga.aero.DataService) {
                service = (DataService)ejb;
            }
            else {
                LOGGER.error("Unable to look up EJB [ "
                        + getJNDIName(DataService.class)
                        + " ] returned reference was the wrong type.  "
                        + "Type returned [ "
                        + ejb.getClass().getCanonicalName()
                        + " ].");
            }
        }
        else {
            LOGGER.error("Unable to look up EJB [ "
                    + getJNDIName(DataService.class)
                    + " ] returned reference was null.");
        }
        return service;
    }
    
    /**
     * Utility method used to look up the UPGDataService interface.  
     * This method is only called by the web tier.
     * 
     * @return The UPGDataService interface, or null if we couldn't 
     * look it up.
     */
    public AeroDataServiceI getUPGDataService() {
        
        AeroDataServiceI service = null;
        Object           ejb     = getEJB(
                                    UPGDataService.class, 
                                    AeroDataServiceI.class);
        
        if (ejb != null) {
            if (ejb instanceof mil.nga.aero.interfaces.AeroDataServiceI) {
                service = (AeroDataServiceI)ejb;
            }
            else {
                LOGGER.error("Unable to look up EJB [ "
                        + getJNDIName(
                                UPGDataService.class, 
                                AeroDataServiceI.class)
                        + " ] returned reference was the wrong type.  "
                        + "Type returned [ "
                        + ejb.getClass().getCanonicalName()
                        + " ].");
            }
        }
        else {
            LOGGER.error("Unable to look up EJB [ "
                    + getJNDIName(
                            UPGDataService.class, 
                            AeroDataServiceI.class)
                    + " ] returned reference was null.");
        }
        return service;
    }
    
    /**
     * Utility method used to look up the UPGDataUpdateService interface.  
     * This method is only called by the web tier.
     * 
     * @return The UPGDataUpdateService interface, or null if we couldn't 
     * look it up.
     */
    public AeroDataUpdateServiceI getUPGDataUpdateService() {
        
        AeroDataUpdateServiceI service = null;
        Object                 ejb     = getEJB(
                                            UPGDataUpdateService.class, 
                                            AeroDataUpdateServiceI.class);
        
        if (ejb != null) {
            if (ejb instanceof mil.nga.aero.interfaces.AeroDataUpdateServiceI) {
                service = (AeroDataUpdateServiceI)ejb;
            }
            else {
                LOGGER.error("Unable to look up EJB [ "
                        + getJNDIName(
                                UPGDataUpdateService.class, 
                                AeroDataUpdateServiceI.class)
                        + " ] returned reference was the wrong type.  "
                        + "Type returned [ "
                        + ejb.getClass().getCanonicalName()
                        + " ].");
            }
        }
        else {
            LOGGER.error("Unable to look up EJB [ "
                    + getJNDIName(
                            UPGDataUpdateService.class, 
                            AeroDataUpdateServiceI.class)
                    + " ] returned reference was null.");
        }
        return service;
    }
    
    /**
     * Utility method used to look up the JEPPDataService interface.  
     * This method is only called by the web tier.
     * 
     * @return The JEPPDataService interface, or null if we couldn't 
     * look it up.
     */
    public AeroDataServiceI getJEPPDataService() {
        
        AeroDataServiceI service = null;
        Object           ejb     = getEJB(
                                    JEPPDataService.class, 
                                    AeroDataServiceI.class);
        
        if (ejb != null) {
            if (ejb instanceof mil.nga.aero.interfaces.AeroDataServiceI) {
                service = (AeroDataServiceI)ejb;
            }
            else {
                LOGGER.error("Unable to look up EJB [ "
                        + getJNDIName(
                                JEPPDataService.class, 
                                AeroDataServiceI.class)
                        + " ] returned reference was the wrong type.  "
                        + "Type returned [ "
                        + ejb.getClass().getCanonicalName()
                        + " ].");
            }
        }
        else {
            LOGGER.error("Unable to look up EJB [ "
                    + getJNDIName(
                            JEPPDataService.class, 
                            AeroDataServiceI.class)
                    + " ] returned reference was null.");
        }
        return service;
    }
    
    /**
     * Utility method used to look up the JEPPDataUpdateService interface.  
     * This method is only called by the web tier.
     * 
     * @return The JEPPDataUpdateService interface, or null if we couldn't 
     * look it up.
     */
    public AeroDataUpdateServiceI getJEPPDataUpdateService() {
        
        AeroDataUpdateServiceI service = null;
        Object                 ejb     = getEJB(
                JEPPDataUpdateService.class, 
                AeroDataUpdateServiceI.class);
        
        if (ejb != null) {
            if (ejb instanceof mil.nga.aero.interfaces.AeroDataUpdateServiceI) {
                service = (AeroDataUpdateServiceI)ejb;
            }
            else {
                LOGGER.error("Unable to look up EJB [ "
                        + getJNDIName(
                                JEPPDataUpdateService.class, 
                                AeroDataUpdateServiceI.class)
                        + " ] returned reference was the wrong type.  "
                        + "Type returned [ "
                        + ejb.getClass().getCanonicalName()
                        + " ].");
            }
        }
        else {
            LOGGER.error("Unable to look up EJB [ "
                    + getJNDIName(
                            JEPPDataUpdateService.class, 
                            AeroDataUpdateServiceI.class)
                    + " ] returned reference was null.");
        }
        return service;
    }
    
    /**
     * Utility method used to look up the JDBCJEPPMetricsService interface.  
     * This method is only called by the web tier.
     * 
     * @return The JDBCJEPPMetricsService interface, or null if we couldn't 
     * look it up.
     */
    public AeroDataMetricsStoreI getJDBCJEPPMetricsService() {
        
        AeroDataMetricsStoreI service = null;
        Object         ejb     = getEJB(
                                    JDBCJEPPMetricsService.class, 
                                    AeroDataMetricsStoreI.class);
        
        if (ejb != null) {
            if (ejb instanceof mil.nga.aero.interfaces.AeroDataMetricsStoreI) {
                service = (AeroDataMetricsStoreI)ejb;
            }
            else {
                LOGGER.error("Unable to look up EJB [ "
                        + getJNDIName(
                                JDBCJEPPMetricsService.class, 
                                AeroDataMetricsStoreI.class)
                        + " ] returned reference was the wrong type.  "
                        + "Type returned [ "
                        + ejb.getClass().getCanonicalName()
                        + " ].");
            }
        }
        else {
            LOGGER.error("Unable to look up EJB [ "
                    + getJNDIName(
                            JDBCJEPPMetricsService.class, 
                            AeroDataMetricsStoreI.class)
                    + " ] returned reference was null.");
        }
        return service;
    }
    
    /**
     * Utility method used to look up the JDBCUPGMetricsService interface.  
     * This method is only called by the web tier.
     * 
     * @return The JDBCUPGMetricsService interface, or null if we couldn't 
     * look it up.
     */
    public AeroDataMetricsStoreI getJDBCUPGMetricsService() {
        
        AeroDataMetricsStoreI service = null;
        Object                ejb     = getEJB(
                                          JDBCUPGMetricsService.class, 
                                          AeroDataMetricsStoreI.class);
        
        if (ejb != null) {
            if (ejb instanceof mil.nga.aero.interfaces.AeroDataMetricsStoreI) {
                service = (AeroDataMetricsStoreI)ejb;
            }
            else {
                LOGGER.error("Unable to look up EJB [ "
                        + getJNDIName(
                                JDBCUPGMetricsService.class, 
                                AeroDataMetricsStoreI.class)
                        + " ] returned reference was the wrong type.  "
                        + "Type returned [ "
                        + ejb.getClass().getCanonicalName()
                        + " ].");
            }
        }
        else {
            LOGGER.error("Unable to look up EJB [ "
                    + getJNDIName(
                            JDBCUPGMetricsService.class,
                            AeroDataMetricsStoreI.class)
                    + " ] returned reference was null.");
        }
        return service;
    }
    
    /**
     * Utility method used to look up the UPGDataSyncService interface.  
     * This method is only called by the web tier.
     * 
     * @return The HashGeneratorService interface, or null if we couldn't 
     * look it up.
     */
    public DataSyncService getDataSyncService() {
        
        DataSyncService service = null;
        Object             ejb     = getEJB(DataSyncService.class);
        
        if (ejb != null) {
            if (ejb instanceof mil.nga.aero.DataSyncService) {
                service = (DataSyncService)ejb;
            }
            else {
                LOGGER.error("Unable to look up EJB [ "
                        + getJNDIName(DataSyncService.class)
                        + " ] returned reference was the wrong type.  "
                        + "Type returned [ "
                        + ejb.getClass().getCanonicalName()
                        + " ].");
            }
        }
        else {
            LOGGER.error("Unable to look up EJB [ "
                    + getJNDIName(DataSyncService.class)
                    + " ] returned reference was null.");
        }
        return service;
    }
    
    /**
     * Method using the JMX MBean interface to retrieve the name of the current
     * JVM (i.e. server name).
     * 
     * @return The name of the container server instance.
     */
    public String getServerName() {
        
        String serverName = "";
        
        try {
            
            ObjectName serverMBeanName = new ObjectName(
                    SERVER_MBEAN_OBJECT_NAME);
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            serverName = (String)server.getAttribute(
                    serverMBeanName, 
                    SERVER_NAME_ATTRIBUTE);
        
        }
        catch (AttributeNotFoundException anfe) {
            LOGGER.error("Unexpected AttributeNotFoundException while "
                    + "attempting to obtain the server name from the "
                    + "application container.  Error message => "
                    + anfe.getMessage());
        }
        catch (MBeanException mbe) {
            LOGGER.error("Unexpected MBeanException while "
                    + "attempting to obtain the server name from the "
                    + "application container.  Error message => "
                    + mbe.getMessage());
        }
        catch (MalformedObjectNameException mone) {
            LOGGER.error("Unexpected MalformedObjectNameException while "
                    + "attempting to obtain the server name from the "
                    + "application container.  Error message => "
                    + mone.getMessage());
        }
        catch (InstanceNotFoundException infe) {
            LOGGER.error("Unexpected AttributeNotFoundException while "
                    + "attempting to obtain the server name from the "
                    + "application container.  Error message => "
                    + infe.getMessage());
        }
        catch (ReflectionException re) {
            LOGGER.error("Unexpected ReflectionException while "
                    + "attempting to obtain the server name from the "
                    + "application container.  Error message => "
                    + re.getMessage());
        }
        return serverName;
    }
    
    /**
     * Static inner class used to construct the Singleton object.  This class
     * exploits the fact that classes are not loaded until they are referenced
     * therefore enforcing thread safety without the performance hit imposed
     * by the <code>synchronized</code> keyword.
     * 
     * @author L. Craig Carpenter
     */
    public static class EJBClientUtilitiesHolder {
        
        /**
         * Reference to the Singleton instance of the ClientUtility
         */
        private static EJBClientUtilities _instance = new EJBClientUtilities();
    
        /**
         * Accessor method for the singleton instance of the ClientUtility.
         * @return The Singleton instance of the client utility.
         */
        public static EJBClientUtilities getSingleton() {
            return _instance;
        }
        
    }
}

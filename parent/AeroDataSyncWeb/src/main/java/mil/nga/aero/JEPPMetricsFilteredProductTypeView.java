package mil.nga.aero;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.aero.upg.model.Metrics;
import mil.nga.types.AeroDataType;

@ManagedBean
@SessionScoped
public class JEPPMetricsFilteredProductTypeView 
		extends AeroEJBClientUtilities 
		implements Serializable {

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = 8245319496306671433L;

    /**
     * Static logger for use throughout the class.
     */
    static final Logger LOGGER = 
            LoggerFactory.getLogger(JEPPMetricsFilteredProductTypeView.class);
    
	/**
	 * Total number of synchronization events.
	 */
	private int totalNumSyncEvents; 
	
    /**
     * List containing all of currently available products.  This list will 
     * not change throughout the life of the current bean.
     */
    private List<Metrics> syncEvents;
    
    /**
     * List containing the metrics data "filtered" by the user using the tools 
     * available on the JSF page.
     */
    private List<Metrics> filteredSyncEvents;
    
    
    /**
     * The product that is currently selected in DataTable.
     */
    private Metrics selectedSyncEvent;
    
    /** 
     * Constructor setting the superclass type to UPG.
     */
    public JEPPMetricsFilteredProductTypeView() {
    	super(AeroDataType.JEPP);
    }
	
    /**
     * This method serves as the constructor which will create and populate 
     * the internal lists that are displayed in the target web page.
     */
    @PostConstruct
    public void initialize() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Constructor called.");
        }
        syncEvents = super.loadMetrics();
        if (syncEvents != null) {
        	totalNumSyncEvents = syncEvents.size();
        }
        else {
        	totalNumSyncEvents = 0;
        }
    }
    
    
    /**
     * Setter method for the list of sync events that have had a "filter" 
     * applied.
     * @return list The filtered list of sync events.
     */
    public List<Metrics> getFilteredSyncEvents( ) {
        return filteredSyncEvents;
    }
    
    /**
     * Getter method for the list of available sync events.
     * @return The list of all available products.
     */
    public List<Metrics> getSyncEvents() {
        return syncEvents;
    }
    
    /**
     * Getter method for the product currently selected in the product list.
     * @return The currently selected product.
     */
    public Metrics getSelectedSyncEvent() {
        return selectedSyncEvent;
    }
    
    /**
     * Getter method for the the total number of synchronization events.
     * @return The total number of synchronization events.
     */
    public int getTotalNumSyncEvents() {
        return totalNumSyncEvents;
    }
    
    /**
     * Setter method for the list of metrics that have had a "filter" 
     * applied.
     * @param list The filtered list of metrics data.
     */
    public void setFilteredSyncEvents(List<Metrics> list) {
        filteredSyncEvents = list;
    }
    
    /**
     * Setter method for the synchronization event currently selected in 
     * the list.
     * 
     * @param value The currently selected Product.
     */
    public void setSelectedSyncEvent(Metrics value) {
        if (value != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Product selected [ "
                        + value.getExecutionTimeString()
                        + " ].");
            }
        }
        else {
            LOGGER.info("Selected product is null.");
        }
        selectedSyncEvent = value;
    }
}

package mil.nga.aero;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import mil.nga.aero.upg.model.UPGData;
import mil.nga.types.AeroDataType;

@ManagedBean
@SessionScoped
public class FilteredProductTypeView 
		extends AeroEJBClientUtilities 
		implements Serializable {

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = 1461288732255178446L;

	/**
	 * Total number of UPG products available
	 */
	private int totalNumProducts; 
	
    /**
     * List containing all of currently available products.  This list will 
     * not change throughout the life of the current bean.
     */
    private List<UPGData> products;
    
    /**
     * List containing the products "filtered" by the user using the tools 
     * available on the JSF page.
     */
    private List<UPGData> filteredProducts;
    
    /**
     * List containing the distinct product types available in the back-end data
     * source.
     */
    private List<String> availableTypes;
    
    /**
     * List containing the distinct ICAOs available in the back-end data
     * source.
     */
    private List<String> availableICAOs;
    
    /**
     * The product that is currently selected in DataTable.
     */
    private UPGData selectedProduct;
    
    /** 
     * Constructor setting the superclass type to UPG.
     */
    public FilteredProductTypeView() {
    	super(AeroDataType.UPG);
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
        products       = super.loadAllProducts();
        availableTypes = super.loadTypes();
        availableICAOs = super.loadICAOs();
        if ((products != null) && (products.size() > 0)) {
        	totalNumProducts = products.size();
        }
        else {
        	totalNumProducts = 0;
        }
    }
    
    /**
     * Getter method for the list of available ICAO Codes
     * @return The list of ICAO codes available in the backing data store.
     */
    public List<String> getAvailableICAOs() {
        return this.availableICAOs;
    }
    
    /**
     * Getter method for the list of available TYPE Codes
     * @return The list of TYPE codes available in the backing data store.
     */
    public List<String> getAvailableTypes() {
        return this.availableTypes;
    }
    
    /**
     * Setter method for the list of products that have had a "filter" 
     * applied.
     * @return list The filtered list of products.
     */
    public List<UPGData> getFilteredProducts( ) {
        return filteredProducts;
    }
    
    /**
     * Getter method for the list of all available products.
     * @return The list of all available products.
     */
    public List<UPGData> getProducts() {
        return products;
    }
    
    /**
     * Getter method for the product currently selected in the product list.
     * @return The currently selected product.
     */
    public UPGData getSelectedProduct() {
        return selectedProduct;
    }
    
    /**
     * Getter method for the the total number of products.
     * @return The total number of products.
     */
    public int getTotalNumProducts() {
        return totalNumProducts;
    }
    /**
     * Setter method for the list of products that have had a "filter" 
     * applied.
     * @param list The filtered list of products.
     */
    public void setFilteredProducts(List<UPGData> list) {
        filteredProducts = list;
    }
    
    /**
     * Setter method for the Product currently selected in the product list.
     * 
     * @param value The currently selected Product.
     */
    public void setSelectedProduct(UPGData value) {
        if (value != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Product selected [ "
                        + value.getUUID()
                        + " ].");
            }
        }
        else {
            LOGGER.info("Selected product is null.");
        }
        selectedProduct = value;
    }
}

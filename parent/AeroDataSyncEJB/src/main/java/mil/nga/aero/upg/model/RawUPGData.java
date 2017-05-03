package mil.nga.aero.upg.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Simple data structure used by JAX-B to deserialize the JSON based UPG 
 * data into a simple POJO for further processing.  The resulting POJO 
 * will contain the raw UPG data downloaded from the target data source.
 * 
 * @author L. Craig Carpenter
 */
public class RawUPGData implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -163775455189925249L;

    /**
     * List holding the JSON array that contains the column names.
     */
    @JsonProperty(value="columns")
    private List<String> columns = new ArrayList<String>();
    
    /**
     * List of lists that holds the actual UPG data.
     */
    @JsonProperty(value="data")
    private List<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
    
    /**
     * Default constructor required for JAX-B
     */
    public RawUPGData() {}
    
    /**
     * Getter method for the list containing the column names associated with
     * the UPG data. 
     * @return The columns contained by the UPG data.
     */
    public List<String> getColumns() {
        return columns;
    }
    
    /**
     * Getter method for the list containing the UPG data rows.
     * @return The actual UPG data.
     */
    public List<ArrayList<String>> getData() {
        return data;
    }
    
    /**
     * Setter method for the list containing the column names associated with
     * the UPG data. 
     * @param data The columns contained by the UPG data.
     */
    public void setColumns(List<String> data) {
        columns = data;
    }
    
    /**
     * Setter method for the list containing the UPG data rows.
     * @param The actual UPG data.
     */
    public void setData(List<ArrayList<String>> data) {
        this.data = data;
    }
    
}

package mil.nga.aero.interfaces;

import java.util.List;

import javax.ejb.Remote;

import mil.nga.aero.upg.model.Metrics;

@Remote
public interface AeroDataMetricsStoreI {

    /**
     * Persist (insert) the information associated with the input 
     * Metrics object.
     * 
     * @param metrics Object containing updated metrics information.
     */
    public void insert(Metrics metrics);
    
    /**
     * Select a list containing all of the elements from the target table.
     * @return A list of all metrics records from the target table.
     */
    public List<Metrics> select();
    
    /**
     * This method will return a list of all 
     * <code>mil.nga.aero.upg.model.Metrics</code> objects currently 
     * persisted in the back-end data store that fall between the input 
     * start and end time.
     * 
     * @param start The "from" parameter 
     * @param end The "to" parameter
     * @return All of the metrics records executed between the input start 
     * and end times.  
     */
    public List<Metrics> select(long start, long end);
    
}

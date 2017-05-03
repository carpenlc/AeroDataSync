package mil.nga.aero.interfaces;

import mil.nga.aero.upg.model.Metrics;

public interface AeroDataMetricsStoreI {

    /**
     * Persist (insert) the information associated with the input 
     * Metrics object.
     * 
     * @param metrics Object containing updated metrics information.
     */
    public void insert(Metrics metrics);
}

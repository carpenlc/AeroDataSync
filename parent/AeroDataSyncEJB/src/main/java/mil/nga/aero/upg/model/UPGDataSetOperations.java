package mil.nga.aero.upg.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UPGDataSetOperations {

	
	/**
	 * Subtract elements in Set B from Set A.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public List<String> subtract(Set<String> a, Set<String> b) {
		List<String> notPresent = new ArrayList<String>(a);
		notPresent.removeAll(b);
		return notPresent;
	}
	
	/**
	 * Subtract elements in list B from list A.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public List<String> subtract(List<String> a, List<String> b) {
		List<String> notPresent = new ArrayList<String>(a);
		notPresent.removeAll(b);
		return notPresent;
	}

	/**
	 * Calculate the intersection of lists A and B (i.e all elements that 
	 * exist in both lists).
	 * 
	 * @param a
	 * @param b
	 * @return The intersection of the two Sets.
	 */
	public List<String> intersection(Set<String> a, Set<String> b) {
		List<String> intersection = new ArrayList<String>();
		for (String id : a) {
			if (b.contains(id)) {
				intersection.add(id);
			}
		}
		return intersection;
	}
	
	/**
	 * Calculate the intersection of lists A and B (i.e all elements that 
	 * exist in both lists).
	 * 
	 * @param a
	 * @param b
	 * @return The intersection of the two Lists
	 */
	public List<String> intersection(List<String> a, List<String> b) {
		List<String> intersection = new ArrayList<String>();
		for (String id : a) {
			if (b.contains(id)) {
				intersection.add(id);
			}
		}
		return intersection;
	}
	
	
	/**
	 * Accessor method for the singleton instance of the AeroDataFactory.
	 * @return Handle to the singleton instance of the AeroDataFactory.
	 */
	public static UPGDataSetOperations getInstance() {
		return UPGDataSetOperationsHolder.getFactorySingleton();
	}
	
	/** 
	 * Static inner class used to construct the factory singleton.  This
	 * class exploits that fact that inner classes are not loaded until they 
	 * referenced therefore enforcing thread safety without the performance 
	 * hit imposed by the use of the "synchronized" keyword.
	 * 
	 * @author L. Craig Carpenter
	 */
	public static class UPGDataSetOperationsHolder {
		
		/**
		 * Reference to the Singleton instance of the factory
		 */
		private static UPGDataSetOperations _factory = new UPGDataSetOperations();
		
		/**
		 * Accessor method for the singleton instance of the factory object.
		 * @return The singleton instance of the factory.
		 */
		public static UPGDataSetOperations getFactorySingleton() {
			return _factory;
		}
	}
}

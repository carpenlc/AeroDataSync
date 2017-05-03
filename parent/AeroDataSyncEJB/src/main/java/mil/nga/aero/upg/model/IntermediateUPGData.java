package mil.nga.aero.upg.model;

import java.io.Serializable;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Simple POJO holding the data associated with a single array element 
 * retrieved from the UPG data source.  This will hold the data associated 
 * with a single UPG data product in it's "intermediate" form (i.e. before
 * it is translated into it's final form for storage in the Data source.)
 * These objects are only built from the raw data retrieved from the UPG
 * data source.  The raw data is obtained via JAX-B marshalling of JSON data.
 * @see mil.nga.aero.upg.model.RawUPGData
 * 
 * @author L. Craig Carpenter
 *
 */
public class IntermediateUPGData implements Serializable {

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = -1056315747425191280L;

	/** 
	 * Format associated with dates incoming from the target UPG data source.
	 */
	private static final String DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
			
	/**
	 * DateFormat object used to convert the String based last-update date 
	 * retrieved from the target data store.
	 */
	private static final SimpleDateFormat formatter = 
				new SimpleDateFormat(DATE_FORMAT_STRING);
	
	private final String uuid;
	private final String icao;
	private final Date   dateLastModified;
	private final String link;
	private final String hash;
	private final String psuedoName;
	private final String type;
	
	/**
	 * Constructor used to set all of the required internal members.
	 * 
	 * @param builder Populated builder object.
	 */
	private IntermediateUPGData(IntermediateUPGDataBuilder builder) {
		this.uuid             = builder.uuid;
		this.icao             = builder.icao;
		this.dateLastModified = builder.dateLastModified;
		this.link             = builder.link;
		this.hash             = builder.hash;
		this.psuedoName       = builder.psuedoName;
		this.type             = builder.type;
	}
	
	/**
	 * Getter method for the DLM (date last modified) attribute.
	 * @return The DLM attribute.
	 */
	public Date getDateLastModified() {
		return dateLastModified;
	}
	
	/**
	 * Getter method for the DLM (date last modified) attribute.
	 * @return The DLM attribute.
	 */
	public String getDateLastModifiedString() {
		return formatter.format(dateLastModified);
	}
	
	/**
	 * Getter method for the HASH attribute.
	 * @return The HASH attribute.
	 */
	public String getHash() {
		return hash;
	}
	
	/**
	 * Getter method for the ICAO attribute.
	 * @return The ICAO attribute.
	 */
	public String getICAO() {
		return icao;
	}
	
	/**
	 * Getter method for the NAME attribute.
	 * @return The NAME attribute.
	 */
	public String getPsuedoName() {
		return psuedoName;
	}
	
	/**
	 * Getter method for the URL attribute.
	 * @return The URL attribute.
	 */
	public String getLink() {
		return link;
	}
	
	/**
	 * Getter method for the TYPE attribute.
	 * @return The TYPE attribute.
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Getter method for the UUID attribute.
	 * @return The UUID attribute.
	 */
	public String getUUID() {
		return uuid;
	}
	
	/**
	 * Convert to human-readable format.
	 */
	public String toString() {
		
		StringBuilder sb      = new StringBuilder();
		String        newLine = System.getProperty("line.separator");
		
		sb.append("UUID => [ ");
		sb.append(getUUID());
		sb.append(" ], ICAO => [ ");
		sb.append(getICAO());
		sb.append(" ], DATE_LAST_MODIFIED => [ ");
		sb.append(getDateLastModifiedString());
		sb.append(" ], LINK => [ ");
		sb.append(getLink());
		sb.append(" ], FILE_HASH => [ ");
		sb.append(getHash());
		sb.append(" ], PSUEDONAME => [ ");
		sb.append(getPsuedoName());
		sb.append(" ], TYPE => [ ");
		sb.append(getType());
		sb.append(newLine);
		
		return sb.toString();
	}
	/**
	 * Class implementing the Builder creation pattern for new 
	 * IntermediateUPGData objects.
	 * 
	 * @author L. Craig Carpenter
	 */
	public static class IntermediateUPGDataBuilder {
		
		private String uuid;
		private String icao;
		private Date   dateLastModified;
		private String link;
		private String hash;
		private String psuedoName;
		private String type;
		
		/**
		 * Default constructor
		 */
		public IntermediateUPGDataBuilder() { }
		
		/**
		 * Private internal method used to convert a String representation of
		 * a date 
		 * 
		 * @param date String representation of the date.
		 * @return The formatted date.
		 * @throws ParseException Thrown if the input date string cannot be
		 * converted into a Date object.
		 */
		private Date getDate(String date) throws ParseException {
			java.util.Date parsed = formatter.parse(date);
			return new java.sql.Date(parsed.getTime());
		}
		
		/**
		 * Construct an IntermediateUPGDataBuilder object from the raw list
		 * of attributes that are received from the UPG data source.
		 * 
		 * @param value A list of attributes.
		 * @return A builder object.
		 * @throws IllegalStateException Thrown if there are problems with the
		 * input array.
		 * @throws ParseException Thrown if there are problems with the 
		 * DATE_LAST_MODIFIED attribute.
		 */
		public IntermediateUPGDataBuilder attributes(List<String> value) 
				throws IllegalStateException, ParseException {
			if ((value != null) && (value.size() > 0)) {
				if (value.size() == 7) {
					return uuid(value.get(0))
							.icao(value.get(1))
							.dateLastModified(value.get(2))
							.link(value.get(3))
							.hash(value.get(4))
							.psuedoName(value.get(5))
							.type(value.get(6));
				}
				else {
					throw new IllegalStateException("Not enough elements in the "
							+ "input array to create a new IntermediateUPGData "
							+ "object.");
				}
			}
			else {
				throw new IllegalStateException("The input array is null or "
						+ "empty.");
			}
		}
		
		/**
		 * Method used to actually construct the IntermediateUPGData object.
		 * @return A constructed and validated IntermediateUPGData object.
		 */
		public IntermediateUPGData build() {
			IntermediateUPGData object = new IntermediateUPGData(this);
			validateIntermediateUPGDataObject(object);
			return object;
		}
		
		/**
		 * Setter method for the DATE_LAST_MODIFIED attribute.
		 * @param value The DATE_LAST_MODIFIED attribute.
		 */
		public IntermediateUPGDataBuilder dateLastModified(String value) 
				throws IllegalStateException, ParseException {
			if ((value == null) || (value.isEmpty())) {
				throw new IllegalStateException("Attempted to build UPGData "
						+ "object but the value for DATE_LAST_MODIFIED was null.");
			}
			dateLastModified = getDate(value.trim());
			return this;
		}
		
		/**
		 * Setter method for the Hash attribute.  This attribute is provided by the 
		 * target, but it is not persisted in the back-end database.
		 * @param value The HASH attribute.
		 */
		public IntermediateUPGDataBuilder hash(String value) {
			hash = value;
			return this;
		}
		
		/**
		 * Setter method for the ICAO attribute.
		 * @param value The ICAO attribute.
		 */
		public IntermediateUPGDataBuilder icao(String value) {
			icao = value;
			return this;
		}
		
		/**
		 * Setter method for the PSUEDONAME attribute.
		 * @param value The PSUEDONAME attribute.
		 */
		public IntermediateUPGDataBuilder psuedoName(String value) {
			psuedoName = value;
			return this;
		}
		
		/**
		 * Setter method for the TYPE attribute.
		 * @param value The TYPE attribute.
		 */
		public IntermediateUPGDataBuilder type(String value) {
			type = value;
			return this;
		}
		
		/**
		 * Setter method for the link attribute.
		 * @param value The link attribute.
		 */
		public IntermediateUPGDataBuilder link(String value) {
			link = value;
			return this;

		}
		
		/**
		 * Getter method for the UUID attribute.
		 * @param value The UUID attribute.
		 */
		public IntermediateUPGDataBuilder uuid(String value) {
			uuid = value;
			return this;
		}
		
		/**
		 * Validate that all fields are populated.
		 * 
		 * @param object The IntermediateUPGData object to validate.
		 * @throws IllegalStateException Thrown if any of the required fields 
		 * are not populated.
		 */
		private void validateIntermediateUPGDataObject(
				IntermediateUPGData object) throws IllegalStateException {
			
			if ((hash == null) || (hash.isEmpty())) {
				throw new IllegalStateException("Attempted to build "
						+ "IntermediateUPGData object but the value for HASH "
						+ "was null.");
			}
			hash = hash.trim();
			
			if ((icao == null) || (icao.isEmpty())) {
				throw new IllegalStateException("Attempted to build "
						+ "IntermediateUPGData object but the value for ICAO "
						+ "was null.");
			}
			icao = icao.trim();
			
			if ((link == null) || (link.isEmpty())) {
				throw new IllegalStateException("Attempted to build "
						+ "IntermediateUPGData object but the value for URL "
						+ "was null.");
			}
			link = link.trim();
			
			if ((psuedoName == null) || (psuedoName.isEmpty())) {
				throw new IllegalStateException("Attempted to build "
						+ "IntermediateUPGData object but the value for "
						+ "PSUEDONAME was null.");
			}
			psuedoName = psuedoName.trim();
			
			if ((type == null) || (type.isEmpty())) {
				throw new IllegalStateException("Attempted to build "
						+ "IntermediateUPGData object but the value for TYPE "
						+ "was null.");
			}
			type = type.trim();
			
			if ((uuid == null) || (uuid.isEmpty())) {
				throw new IllegalStateException("Attempted to build "
						+ "IntermediateUPGData object but the value for UUID "
						+ "was null.");
			}
			uuid = uuid.trim();
		}
	}
}

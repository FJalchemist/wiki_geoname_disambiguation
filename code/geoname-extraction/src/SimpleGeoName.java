import java.util.List;


/**
 * This is a simplified version of GeoName class, containing only four members.
geonameid int PRIMARY KEY,
name varchar(200),
asciiname varchar(200),
alternatenames varchar(4000), 
 * @author Yibin Lin
 *
 */
public class SimpleGeoName {
	int geoNameId;
	String name;
	String asciiName;
	List<String> alternateNames;
	public int getGeoNameId() {
		return geoNameId;
	}
	public void setGeoNameId(int geoNameId) {
		this.geoNameId = geoNameId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAsciiName() {
		return asciiName;
	}
	public void setAsciiName(String asciiName) {
		this.asciiName = asciiName;
	}
	public List<String> getAlternateNames() {
		return alternateNames;
	}
	public void setAlternateNames(List<String> alternateNames) {
		this.alternateNames = alternateNames;
	}
	public SimpleGeoName(int geoNameId, String name, String asciiName,
			List<String> alternateNames) {
		super();
		this.geoNameId = geoNameId;
		this.name = name;
		this.asciiName = asciiName;
		this.alternateNames = alternateNames;
	}
	
	public SimpleGeoName()
	{
		super();
	}
	
	

}

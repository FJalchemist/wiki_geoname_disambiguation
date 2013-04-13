import java.util.List;


/**
 * Object representing an entry in the Geoname table in the mysql database.
 * 
CREATE TABLE geoname (
geonameid int PRIMARY KEY,
name varchar(200),
asciiname varchar(200),
alternatenames varchar(4000),
latitude decimal(10,7),
longitude decimal(10,7),
fclass char(1),
fcode varchar(10),
country varchar(2),
cc2 varchar(60),
admin1 varchar(20),
admin2 varchar(80),
admin3 varchar(20),
admin4 varchar(20),
population int,
elevation int,
gtopo30 int,
timezone varchar(40),
moddate date
) CHARACTER SET utf8; 
 * 
 * explanation:
geonameid         : integer id of record in geonames database
name              : name of geographical point (utf8) varchar(200)
asciiname         : name of geographical point in plain ascii characters, varchar(200)
alternatenames    : alternatenames, comma separated varchar(5000)
latitude          : latitude in decimal degrees (wgs84)
longitude         : longitude in decimal degrees (wgs84)
feature class     : see http://www.geonames.org/export/codes.html, char(1)
feature code      : see http://www.geonames.org/export/codes.html, varchar(10)
country code      : ISO-3166 2-letter country code, 2 characters
cc2               : alternate country codes, comma separated, ISO-3166 2-letter country code, 60 characters
admin1 code       : fipscode (subject to change to iso code), see exceptions below, see file admin1Codes.txt for display names of this code; varchar(20)
admin2 code       : code for the second administrative division, a county in the US, see file admin2Codes.txt; varchar(80) 
admin3 code       : code for third level administrative division, varchar(20)
admin4 code       : code for fourth level administrative division, varchar(20)
population        : bigint (8 byte int) 
elevation         : in meters, integer
dem               : digital elevation model, srtm3 or gtopo30, average elevation of 3''x3'' (ca 90mx90m) or 30''x30'' (ca 900mx900m) area in meters, integer. srtm processed by cgiar/ciat.
timezone          : the timezone id (see file timeZone.txt) varchar(40)
modification date : date of last modification in yyyy-MM-dd format
 * @author Yibin Lin
 *
 */
public class GeoName {

	public GeoName(int geoNameId, String name, String asciiName,
			List<String> alternateNames, double latitude, double longitude,
			FeatureClass fclass, String fcode, String country, String cc2,
			String admin1, String admin2, String admin3, String admin4,
			long population, long elevation, long gtopo30, String timeZone,
			String modDate) {
		super();
		this.geoNameId = geoNameId;
		this.name = name;
		this.asciiName = asciiName;
		this.alternateNames = alternateNames;
		this.latitude = latitude;
		this.longitude = longitude;
		this.fclass = fclass;
		this.fcode = fcode;
		this.country = country;
		this.cc2 = cc2;
		this.admin1 = admin1;
		this.admin2 = admin2;
		this.admin3 = admin3;
		this.admin4 = admin4;
		this.population = population;
		this.elevation = elevation;
		this.gtopo30 = gtopo30;
		this.timeZone = timeZone;
		this.modDate = modDate;
	}
	
	public GeoName() {
		super();
	}

	int geoNameId;
	String name;
	String asciiName;
	List<String> alternateNames;
	double latitude;
	double longitude;
	FeatureClass fclass;
	String fcode; //reference: http://www.geonames.org/export/codes.html
	String country; 
	String cc2;
	String admin1;
	String admin2;
	String admin3;
	String admin4;
	long population;
	long elevation;
	long gtopo30;
	String timeZone;
	String modDate;
	
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
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public FeatureClass getFclass() {
		return fclass;
	}
	public void setFclass(FeatureClass fclass) {
		this.fclass = fclass;
	}
	public String getFcode() {
		return fcode;
	}
	public void setFcode(String fcode) {
		this.fcode = fcode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCc2() {
		return cc2;
	}
	public void setCc2(String cc2) {
		this.cc2 = cc2;
	}
	public String getAdmin1() {
		return admin1;
	}
	public void setAdmin1(String admin1) {
		this.admin1 = admin1;
	}
	public String getAdmin2() {
		return admin2;
	}
	public void setAdmin2(String admin2) {
		this.admin2 = admin2;
	}
	public String getAdmin3() {
		return admin3;
	}
	public void setAdmin3(String admin3) {
		this.admin3 = admin3;
	}
	public String getAdmin4() {
		return admin4;
	}
	public void setAdmin4(String admin4) {
		this.admin4 = admin4;
	}
	public long getPopulation() {
		return population;
	}
	public void setPopulation(long population) {
		this.population = population;
	}
	public long getElevation() {
		return elevation;
	}
	public void setElevation(long elevation) {
		this.elevation = elevation;
	}
	public long getGtopo30() {
		return gtopo30;
	}
	public void setGtopo30(long gtopo30) {
		this.gtopo30 = gtopo30;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public String getModDate() {
		return modDate;
	}
	public void setModDate(String modDate) {
		this.modDate = modDate;
	}
	
	
	
}

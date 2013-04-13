import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;


/**
 * extract geoname from text file like cities1000.txt
 * 
 * @author Yibin Lin
 *
 */
public class GetCityInfoFromTxt {
	
	/**
	 * We only deal with cities first..
	 * @param filename
	 * @return mapping between geonameId and GeoName instances
	 */
	public HashMap<Integer, GeoName> getCityById(String filename)
	{
		HashMap<Integer, GeoName> map = new HashMap<Integer, GeoName>();
		try {
			File f = new File(filename);
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));
			
			String str;
			int cnt = 0;
			
			while ((str = in.readLine()) != null) {
				cnt++;
				
				str = str.replaceAll("(\\r|\\n)", "");
				
				if(cnt < 100)
				{
					System.out.println(str);
				}
			    
			    
			}
	 
			in.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return map;
	}
	
	/**
	 * 
	 * 
	 * @param line a line of txt in utf-8 format..
	 * @return a Geoname instance
	 */
	private GeoName getGeoNameFromTxt(String line)
	{
		String[] columns = line.split("\t");
		return null;
	}

}

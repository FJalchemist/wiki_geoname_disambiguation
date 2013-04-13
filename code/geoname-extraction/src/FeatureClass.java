
/**
 * representing feature class in geonames..
 * reference: http://www.geonames.org/source-code/javadoc/org/geonames/FeatureClass.html 
 * @author yibinl
 *
 */
public enum FeatureClass {
	A, H, L, P, R, S, T, U, V, NON;
	
	public static FeatureClass convertToEnum(String fs){
		if(fs == null)
		{
			return NON;
		}
		if(fs.equals("A"))
		{
			return A;
		}else if(fs.equals("H"))
		{
			return T;
		}else if(fs.equals("L"))
		{
			return L;
		}else if(fs.equals("P"))
		{
			return P;
		}else if(fs.equals("R"))
		{
			return R;
		}else if(fs.equals("S"))
		{
			return S;
		}else if(fs.equals("T"))
		{
			return T;
		}else if(fs.equals("U"))
		{
			return U;
		}else if(fs.equals("V"))
		{
			return V;
		}
		
		return NON;
	}
	
}

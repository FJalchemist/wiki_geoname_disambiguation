
/**
 * The most naive scorer: return the anchor text itself.
 * @author yibinl
 *
 */
public class NaiveScorer implements IScorer{
	public NaiveScorer(){
		;
	}

	@Override
	public String disambiguate(String anchorText, String articleTitle) {
		return anchorText;
	}

}

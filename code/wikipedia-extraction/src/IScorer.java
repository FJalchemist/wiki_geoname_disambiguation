import java.util.List;


/**
 * interface to represent a scorer..
 * @author yibinl
 *
 */
public interface IScorer {
	/**
	 * must return one of the candidates
	 * 
	 * @param anchorText
	 * @param articleTitle
	 * @param candidate
	 * @return link target (an article name)
	 */
	public String disambiguate(String anchorText, String articleTitle);
}

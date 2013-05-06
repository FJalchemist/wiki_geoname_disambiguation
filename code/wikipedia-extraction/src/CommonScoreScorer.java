import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Use only common score to do disambiguation
 * @author yibinl
 *
 */
public class CommonScoreScorer implements IScorer {
	/**
	 * key: anchor text
	 * value: map of article title vs. count
	 */
	HashMap<String, HashMap<String, Long>> commonScores; 
	double majority_prob;

	/**
	 * 
	 * @param filename
	 */
	public CommonScoreScorer(String filename)
	{
		commonScores = new HashMap<String, HashMap<String, Long>>();
		
		BufferedReader br = null;
		long major_cnt = 0;
		long total_cnt = 0;
		 
		try {
 
			String anchorEntry;
 
			br = new BufferedReader(new FileReader(filename));
 
			while ((anchorEntry = br.readLine()) != null) {
				
					
				String[] parts = anchorEntry.split("\t");
				if (parts.length <= 1) {
					continue;
				}
				String anchorText = parts[0];
				//System.out.println(anchorEntry);
				HashMap<String, Long> articles = null;
				long maxCnt = 0;
				long wholeCnt = 0;
				if(commonScores.containsKey(anchorText))
				{
					articles = commonScores.get(anchorText);
				}
				else{
					articles = new HashMap<String, Long>();
					commonScores.put(anchorText, articles);
				}
				Pattern pattern = Pattern.compile("\\[(.+?)\\]");
				Matcher matcher = pattern.matcher(anchorEntry);

				while (matcher.find()) {
					String link = matcher.group(1);
					String[] linkParts = link.split("\\|");
					if (linkParts.length == 1) {
						articles.put(linkParts[0], (long)1);
						wholeCnt += 1;
						if(maxCnt < 1)
						{
							maxCnt = 1;
						}
					}
					else if(linkParts.length == 2)
					{
						long cnt = Long.parseLong(linkParts[1]);
						articles.put(linkParts[0], cnt);
						wholeCnt += cnt;
						if (cnt > maxCnt)
						{
							maxCnt = cnt;
						}
					}
					else
					{
						System.err.println("parse warning: wrong format in common score file.\n" + anchorEntry);
					}
				}
				major_cnt += maxCnt;
				total_cnt += wholeCnt;
			}
			majority_prob = ((double)major_cnt / (double) total_cnt);
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@Override
	public String disambiguate(String anchorText, String articleTitle) {
		if(commonScores.containsKey(anchorText))
		{
			HashMap<String, Long> articles = commonScores.get(anchorText);
			long maxCount = 0;
			String maxArt = "";
			Set<String> artKeys = articles.keySet();
			for(String key : artKeys)
			{
				long count = articles.get(key);
				if(maxCount < count)
				{
					maxCount = count;
					maxArt = key;
				}
			}
			if(!maxArt.equals(""))
			{
				return maxArt;
			}
		}
		return anchorText; //default
	}

}

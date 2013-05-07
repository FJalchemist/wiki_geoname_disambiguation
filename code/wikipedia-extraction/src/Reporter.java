import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * calculate precision and recall 
 * @author Yibin Lin
 *
 */
public class Reporter {

	/**
	 * 
	 * @param args
	 * 0: gold standard file
	 * 1: common score file, or ...
	 */
	public static void main(String[] args)
	{
		System.err.println(args[0] + ", " + args[1]);
		String gold = args[0];
		
		String common = args[1];
		IScorer css = new CommonScoreScorer(common);
		//IScorer css = new NaiveScorer();
		//System.out.format("target probability: %f. \n", css.majority_prob);
		
		BufferedReader br = null;
		
		long correct = 0;
		long total = 0;
		 
		try {
 
			String article;
 
			br = new BufferedReader(new FileReader(gold));
			long lineCnt = 0;
			
			while ((article = br.readLine()) != null) {
				lineCnt ++;
				if(lineCnt % 10000== 0){
					System.err.print(".");
					if(Math.random() < 0.05)
					{
						System.err.print("\n");
					}
				}
				String[] parts = article.split("##,,##,,##");
				if(parts.length == 3)
				{
					String anchorText = parts[0];
					String title = parts[1].replace("[", "").replace("]", "");
					String golds = parts[2].replace("[", "").replace("]", "");
					//System.err.format("%s, %s, %s. \n", anchorText, title, golds);
					
					String res = css.disambiguate(anchorText, title);
					//System.err.format("%s, %s. \n", res, golds);
					if(res.equals(golds))
					{
						correct += 1;
					}
					total += 1;
				}
				else
				{
					System.err.format("article format warning: %s.\n", article);
				}
				
			}
			double prob = ((double)correct/ (double) total);
			System.err.print("\n");
			System.out.println("actual probability: " + prob);
 
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
}

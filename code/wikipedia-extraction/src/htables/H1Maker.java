package htables;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class H1Maker {
	/**
	 * key: link target (article name) value: list of anchor texts of linked to
	 * this link target
	 */
	private HashMap<String, LinkedList<String>> lnkTargetMap;

	public H1Maker(String targets, String inlinks, String output) {
		lnkTargetMap = new HashMap<String, LinkedList<String>>(30000000);
		readCitiesH1(targets);
		scanInlinks(inlinks, output);
	}

	public void scanInlinks(String inlinks, String output) {
		BufferedReader br = null;
		BufferedWriter out = null;

		try {

			String article;

			br = new BufferedReader(new FileReader(inlinks));

			FileWriter fstream = new FileWriter(output);
			out = new BufferedWriter(fstream);
			long cnt = 0;

			while ((article = br.readLine()) != null) {
				cnt++;
				if(cnt % 10000== 0){
					System.err.print(".");
					if(Math.random() < 0.05)
					{
						System.err.print("\n");
					}
				}
				
				// System.out.println(article);
				String[] parts = article.split("##,,##,,##");
				if (parts.length != 3) {
					System.err
							.println("Warning: error in reading inlinks file. "
									+ article);
					continue;
				}
				String title = parts[0].replace("\t", "");
				long lnkCnt = Long.parseLong(parts[1]);
				String lnkIdstr = parts[2];
				//System.out.println(title + "sss");
				if (lnkTargetMap.containsKey(title)) {
					//System.out.println("entered here!");
					LinkedList<String> anchors = lnkTargetMap.get(title);
					StringBuffer sb = new StringBuffer();
					for (String anchorText : anchors) {
						sb.append(String.format("%s##,,##,,##%s##,,##,,##%d##,,##,,##%s\n", anchorText, title, lnkCnt, lnkIdstr));
					}
					out.write(sb.toString());
					//System.out.println(sb.toString());
				}
			}
			System.err.print("Done\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (out != null)
					out.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void readCitiesH1(String filename) {
		BufferedReader br = null;

		try {

			String atLnkTarget;

			br = new BufferedReader(new FileReader(filename));
			long cnt = 0;
			
			while ((atLnkTarget = br.readLine()) != null) {
				cnt++;
				if(cnt % 10000== 0){
					System.err.print(".");
					if(Math.random() < 0.05)
					{
						System.err.print("\n");
					}
				}
				// System.out.println(atLnkTarget);
				String[] parts = atLnkTarget.split("##,,##,,##");
				if (parts.length != 2) {
					System.err
							.println("Warning: error in reading target file. "
									+ atLnkTarget);
					continue;
				}
				String anchorText = parts[0];
				String lnkTarget = parts[1].replace("[", "").replace("]", "");
				//System.out.println(lnkTarget);

				LinkedList<String> anchors = null;

				if (lnkTargetMap.containsKey(lnkTarget)) {
					anchors = lnkTargetMap.get(lnkTarget);
				} else {
					anchors = new LinkedList<String>();
					lnkTargetMap.put(lnkTarget, anchors);
				}
				anchors.add(anchorText);
			}
			System.err.println(lnkTargetMap.size());

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param args
	 *            0: target 1: inlink file, or... 2: output file
	 */
	public static void main(String[] args) {
		new H1Maker(args[0], args[1], args[2]);
	}

}

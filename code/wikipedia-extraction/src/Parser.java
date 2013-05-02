import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import edu.jhu.nlp.wikipedia.*;
        
  
public class Parser {
	public static void main(String[] args) throws Exception {
		File path = new File(args[0]);
		System.out.println(path.getAbsolutePath());
		for (File f : path.listFiles()) {
			if (f.isFile() == true) {
				Parser.parse(f.getAbsolutePath());
			}
		}
		
	}
	

		
	public static void parse(String xml) throws Exception {
		//String chuckName = "/home/yu/workspace/10605/projects/data/wikichuncks/chunk-0412.xml.bz2";
		//String chuckName = "/home/yu/workspace/10605/projects/data/wikichuncks/test";
		WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser(xml);
		try {
			wxsp.setPageCallback(new PageCallbackHandler() {
				public void  process(WikiPage page) {
					String title = page.getTitle().replace("\n", "").trim();
					String id = page.getID();
					Vector<String> pagelinks = page.getLinksWithText().get("pageLinks");
					Vector<String> pagelinkTexts = page.getLinksWithText().get("pageLinkTexts");
					if (pagelinks.size() != pagelinkTexts.size()) {
						System.err.print("link and anchor text number not the same");
					}
					if (pagelinks.size() > 0) {
						System.out.print(id + "\t" + title + "\t" + pagelinks.size());
						Iterator<String> it_pl = pagelinks.iterator();
						Iterator<String> it_plText = pagelinkTexts.iterator();
						while (it_pl.hasNext() && it_plText.hasNext()) {
							String link = it_pl.next();
							String anchor = it_plText.next();
							if (link.isEmpty() == true || link.contains("\\[") || link.contains("\\[")
									|| anchor.contains("\\[") || anchor.contains("\\]") || anchor.isEmpty()) {
								System.err.println("confusing link  " + link + " " + anchor);
								continue;
							}
							
							link = link.trim();
							anchor = anchor.trim();
							if (link.equals(anchor)) {
								System.out.print("\t[" + link +"]");
							} else {
								System.out.print("\t[" + link + "|"  + anchor + "]");
							}
						}
						System.out.print("\n");
					} else {
						System.out.println(id + "\t" + title + "\t" + pagelinks.size());
					}
				}	
					
			});
			 wxsp.parse();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}

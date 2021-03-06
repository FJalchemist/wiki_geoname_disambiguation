import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * extract geoname from text file like cities1000.txt
 * 
 * @author Yibin Lin
 * 
 */
public class GetCityInfoFromTxt {
	private static final int column_cnt = 19;

	/**
	 * We only deal with cities first..
	 * 
	 * @param filename
	 * @return mapping between geonameId and GeoName instances
	 */
	public static HashMap<Integer, GeoName> getCityByIdMapping(String filename) {
		HashMap<Integer, GeoName> map = new HashMap<Integer, GeoName>();
		try {
			File f = new File(filename);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(f), "UTF8"));

			String str;
			int cnt = 0;

			while ((str = in.readLine()) != null) {
				cnt++;

				str = str.replaceAll("(\\r|\\n)", "");

				// if (cnt < 100) {
				// System.out.println(str);
				// }

				GeoName gn = getGeoNameFromTxt(str);
				if (gn != null) {
					map.put(gn.geoNameId, gn);
				}
			}

			in.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * We only deal with cities first..
	 * 
	 * @param filename
	 * @return mapping between one alternative name and GeoName instances
	 */
	public static HashMap<String, LinkedList<SimpleGeoName>> getCityByAlternativeNameMapping(
			String filename) {
		HashMap<String, LinkedList<SimpleGeoName>> map = new HashMap<String, LinkedList<SimpleGeoName>>();
		try {
			File f = new File(filename);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(f), "UTF8"));

			String str;
			int cnt = 0;

			while ((str = in.readLine()) != null) {
				cnt++;

				str = str.replaceAll("(\\r|\\n)", "");

				// if (cnt < 100) {
				// System.out.println(str);
				// }

				SimpleGeoName gn = getSimpleGeoNameFromTxt(str);

				appendMapList(map, gn.name, gn);
				appendMapList(map, gn.asciiName, gn);
				for (String alter : gn.alternateNames) {
					appendMapList(map, alter, gn);
				}
			}

			in.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return map;
	}

	private static void appendMapList(
			HashMap<String, LinkedList<SimpleGeoName>> map, String name,
			SimpleGeoName gn) {
		if (map.containsKey(name)) {
			LinkedList<SimpleGeoName> lst = map.get(name);
			if (!lst.contains(gn)) // cannot contain duplicates..
			{
				lst.add(gn);
			}
		} else {
			LinkedList<SimpleGeoName> lst = new LinkedList<SimpleGeoName>();
			lst.add(gn);
			map.put(name, lst);
		}

	}

	/**
	 * total: 19 items
	 * 
	 * @param line
	 *            a line of txt in utf-8 format..
	 * @return a Geoname instance
	 */
	private static GeoName getGeoNameFromTxt(String line) {
		String[] columns = line.split("\t");
		// DEBUG
		// System.err.format("column length: %d\n", columns.length);
		if (columns.length == column_cnt) {
			try {
				int geonameid = -1;
				String name = "";
				String ansiname = "";
				List<String> alternatenames = new ArrayList<String>();
				double latitude = 200;
				double longitude = 200;
				FeatureClass feature_class = null;
				String feature_code = "";
				String country_code = "";
				String cc2 = "";
				String admin1_code = "";
				String admin2_code = "";
				String admin3_code = "";
				String admin4_code = "";
				long population = -1;
				int elevation = -300;
				int gtopo30 = -100;
				String timezone = "";
				String modification_date = "";

				geonameid = Integer.parseInt(columns[0]);
				name = columns[1];
				ansiname = columns[2];
				alternatenames = new ArrayList<String>(Arrays.asList(columns[3]
						.split(",")));
				latitude = Double.parseDouble(columns[4]);
				longitude = Double.parseDouble(columns[5]);
				feature_class = FeatureClass.convertToEnum(columns[6]);
				feature_code = columns[7];
				country_code = columns[8];
				cc2 = columns[9];
				admin1_code = columns[10];
				admin2_code = columns[11];
				admin3_code = columns[12];
				admin4_code = columns[13];
				population = Long.parseLong(columns[14]);
				elevation = Integer.parseInt(columns[15]);
				gtopo30 = Integer.parseInt(columns[16]);
				timezone = columns[17];
				modification_date = columns[18];

				return new GeoName(geonameid, name, ansiname, alternatenames,
						latitude, longitude, feature_class, feature_code,
						country_code, cc2, admin1_code, admin2_code,
						admin3_code, admin4_code, population, elevation,
						gtopo30, timezone, modification_date);
			} catch (NullPointerException npe) {
				;
			} catch (NumberFormatException nfe) {
				;
			}
		}

		return null;
	}

	/**
	 * total: 19 items
	 * 
	 * @param line
	 *            a line of txt in utf-8 format..
	 * @return a SimpleGeoname instance
	 */
	private static SimpleGeoName getSimpleGeoNameFromTxt(String line) {
		String[] columns = line.split("\t");
		// DEBUG
		// System.err.format("column length: %d\n", columns.length);
		if (columns.length == column_cnt) {
			int geonameid = Integer.parseInt(columns[0]);
			String name = columns[1];
			String ansiname = columns[2];
			List<String> alternatenames = new ArrayList<String>(
					Arrays.asList(columns[3].split(",")));

			return new SimpleGeoName(geonameid, name, ansiname, alternatenames);
		}

		return null;
	}

	/**
	 * Estimate degree distribution of the aliases - expecting a zipf's
	 * distribution..
	 * 
	 * @param aliasGn
	 * @return number of cities with the same name vs. count
	 */
	private static HashMap<Integer, Integer> calculateDistributionOfCities(
			HashMap<String, LinkedList<SimpleGeoName>> aliasGn) {
		HashMap<Integer, Integer> dist = new HashMap<Integer, Integer>();
		for (String alter : aliasGn.keySet()) {
			int ambiguousCnt = aliasGn.get(alter).size();
			if (ambiguousCnt > 3000) {
				System.err.println("entered: " + alter);
			}
			if (dist.containsKey(ambiguousCnt)) {
				dist.put(ambiguousCnt, (dist.get(ambiguousCnt) + 1));
			} else {
				dist.put(ambiguousCnt, 1);
			}
		}
		return dist;
	}
	
	/**
	 * filter the common score table (H3 by our definition.
	 * @param filename
	 * @param aliasGn
	 * @param output
	 */
	private static void filterCommonScoreH3(String filename,
			HashMap<String, LinkedList<SimpleGeoName>> aliasGn, String output) {
		BufferedReader br = null;
		BufferedWriter out = null;

		try {

			String anchorEntry;

			br = new BufferedReader(new FileReader(filename));
			out = new BufferedWriter(new FileWriter(output));

			while ((anchorEntry = br.readLine()) != null) {
				String[] parts = anchorEntry.split("\t");
				if (parts.length <= 1) {
					continue;
				}
				String anchorText = parts[0];
				if (aliasGn.containsKey(anchorText)
						&& aliasGn.get(anchorText).size() > 0) {

					Pattern pattern = Pattern.compile("\\[(.+?)\\]");
					Matcher matcher = pattern.matcher(anchorEntry);
					int cnt = 0;
					StringBuffer ambiguousText = new StringBuffer();

					while (matcher.find()) {
						cnt++;
					}
					if (cnt > 1) {
						out.write(anchorEntry + "\n");
					}
				}
			}

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


	private static void filterCityMentionByLinkTarget(String filename,
			HashMap<String, LinkedList<SimpleGeoName>> aliasGn, String output) {
		BufferedReader br = null;
		BufferedWriter out = null;

		try {

			String article;

			br = new BufferedReader(new FileReader(filename));
			out = new BufferedWriter(new FileWriter(output));
			String fStr = "##;%s###%d#;#"; // format string..

			while ((article = br.readLine()) != null) {
				String[] parts = article.split("\t");
				if (parts.length <= 1) {
					continue;
				}
				String anchorText = parts[0];
				if (aliasGn.containsKey(anchorText)
						&& aliasGn.get(anchorText).size() > 0) {

					Pattern pattern = Pattern.compile("\\[(.+?)\\]");
					Matcher matcher = pattern.matcher(article);
					int cnt = 0;
					StringBuffer ambiguousText = new StringBuffer();

					while (matcher.find()) {
						cnt++;
						String link = matcher.group(1);
						String[] linkParts = link.split("\\|");
						if (linkParts.length == 1 || linkParts.length == 2) {
							ambiguousText.append(String.format(
									"%s##,,##,,##%s\n", anchorText, ("["
											+ linkParts[0] + "]")));
						} else {
							System.err.format(
									"parse error: line %d. Line info: %s.\n",
									cnt, article);
						}
					}
					if (cnt > 1) {
						out.write(ambiguousText.toString());
					}
				}
			}

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

	private static void filterCityMentionByArticle(String filename,
			HashMap<String, LinkedList<SimpleGeoName>> aliasGn, String output) {
		BufferedReader br = null;
		BufferedWriter out = null;

		try {

			String article;

			br = new BufferedReader(new FileReader(filename));
			out = new BufferedWriter(new FileWriter(output));

			while ((article = br.readLine()) != null) {
				String[] parts = article.split("\t");
				StringBuffer titleBuffer = new StringBuffer();
				LinkedList<String> anchorTexts = new LinkedList<String>();
				
				if (parts.length <= 1) {
					continue;
				}

				if (parts.length < 3) {
					System.out.print("Unrecognized article: " + article);
				} else {

					Long id = Long.parseLong(parts[0]);
					String title = parts[1];
					Integer linkNum = Integer.parseInt(parts[2]);
					String anchorText = "";

					Pattern pattern = Pattern.compile("\\[(.+?)\\]");
					Matcher matcher = pattern.matcher(article);
					while (matcher.find()) {
						String link = matcher.group(1);
						String[] linkParts = link.split("\\|");
						if (linkParts.length == 1) {

							anchorText = linkParts[0];
						} else if (linkParts.length == 2) {

							anchorText = linkParts[1];
						} else {
							System.out.print("?? linkParts: "
									+ linkParts.length + "  " + link);
						}
						if (!anchorTexts.contains(anchorText) && aliasGn.containsKey(anchorText)
								&& aliasGn.get(anchorText).size() > 0) {
							anchorTexts.add(anchorText);
							titleBuffer.append(String.format(
									"%s##,,##,,##%s\n", anchorText, ("["
											+ title + "]") ));

						}

					}
					out.write(titleBuffer.toString());
				}
			}

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
	
	private static void getGoldStandard(String filename,
			HashMap<String, LinkedList<SimpleGeoName>> aliasGn, String output) {
		BufferedReader br = null;
		BufferedWriter out = null;

		try {

			String article;

			br = new BufferedReader(new FileReader(filename));
			out = new BufferedWriter(new FileWriter(output));

			while ((article = br.readLine()) != null) {
				String[] parts = article.split("\t");
				StringBuffer titleBuffer = new StringBuffer();
				LinkedList<String> anchorTexts = new LinkedList<String>();
				
				if (parts.length <= 1) {
					continue;
				}

				if (parts.length < 3) {
					System.out.print("Unrecognized article: " + article);
				} else {

					Long id = Long.parseLong(parts[0]);
					String title = parts[1];
					Integer linkNum = Integer.parseInt(parts[2]);
					String anchorText = "";
					String linkTarget = "";

					Pattern pattern = Pattern.compile("\\[(.+?)\\]");
					Matcher matcher = pattern.matcher(article);
					while (matcher.find()) {
						String link = matcher.group(1);
						String[] linkParts = link.split("\\|");
						if (linkParts.length == 1) {

							anchorText = linkParts[0];
							linkTarget = linkParts[0];
						} else if (linkParts.length == 2) {

							anchorText = linkParts[1];
							linkTarget = linkParts[0];
						} else {
							System.out.print("?? linkParts: "
									+ linkParts.length + "  " + link);
						}
						if (!anchorTexts.contains(anchorText) && aliasGn.containsKey(anchorText)
								&& aliasGn.get(anchorText).size() > 0) {
							anchorTexts.add(anchorText);
							titleBuffer.append(String.format(
									"%s##,,##,,##%s##,,##,,##%s\n", anchorText, ("["
											+ title + "]"), ("[" + linkTarget + "]") ));

						}

					}
					out.write(titleBuffer.toString());
				}
			}

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


	public static void main(String[] args) {
		String input = args[0];
		String output = args[1];
		System.out.println(input + ", " + output);
		HashMap<Integer, GeoName> idGn = getCityByIdMapping("input/cities1000.txt");
		HashMap<String, LinkedList<SimpleGeoName>> aliasGn = getCityByAlternativeNameMapping("input/cities1000.txt");
		//filterCityMentionByLinkTarget(input, aliasGn, output);
		//filterCityMentionByArticle(input, aliasGn, output);
		//filterCommonScoreH3(input, aliasGn, output);
		getGoldStandard(input, aliasGn, output);
	}

}

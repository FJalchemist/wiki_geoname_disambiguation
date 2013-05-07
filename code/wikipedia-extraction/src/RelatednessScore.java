import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * input should be two files, h1 and h2. H1 with separator1, H2 with separator2.
 * (defined here)
 * 
 * 
 * 
 * @author Yibin Lin
 * 
 */
public class RelatednessScore {
	static double totalLog = Math.log(4226723.0);
	static String separator1 = "##,,##,,##";
	static String separator2 = "##,#2#,##";

	public static class CSMapper extends Mapper<LongWritable, Text, Text, Text> {

		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {

			String anchorEntry = value.toString();
			if (anchorEntry.equals("")) {
				return;
			}

			String[] parts = anchorEntry.split("\t");
			if (parts.length != 2 && parts.length != 1) {
				System.out.println("Warning: Malformed input: " + anchorEntry);
				return;
			} else if (parts.length == 1) { // h1
				String[] contentParts = anchorEntry.split(separator1);
				if (contentParts.length != 4) {
					System.out.println("Malformed h1 input: " + anchorEntry);
					return;
				}
				String anchorText = contentParts[0];
				String linkTarget = contentParts[1];
				int inLnkCnt = Integer.parseInt(contentParts[2]);
				if(inLnkCnt < 10)
				{
					return;
				}
				String idStr = contentParts[3];

				context.write(
						new Text(anchorText),
						new Text(String.format("%s" + separator1 + "%s",
								linkTarget, idStr)));
			} else if (parts.length == 2) { // h2
				String anchorText = parts[0];
				String content = parts[1];
				String[] contentParts = content.split(separator1);
				if (contentParts.length != 3) {
					System.out.println("Malformed h2 input: " + anchorEntry);
					return;
				}
				String article = contentParts[0];
				int inLnkCnt = Integer.parseInt(contentParts[1]);
				if(inLnkCnt < 10)
				{
					return;
				}
				String idStr = contentParts[2];
				// System.out.println("!!! article: " + String.format("%s" +
				// separator2 + "%s", article, idStr));
				context.write(
						new Text(anchorText),
						new Text(String.format("%s" + separator2 + "%s",
								article, idStr)));
			}

		}

	} // Mapper

	public static class CSReducer extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			HashMap<String, HashSet<Long>> h1 = new HashMap<String, HashSet<Long>>();
			HashMap<String, HashSet<Long>> h2 = new HashMap<String, HashSet<Long>>();

			String anchorText = key.toString();

			for (Text value : values) {
				String valStr = value.toString();
				String[] sep1Parts = valStr.split(separator1);
				int sep1Len = sep1Parts.length;

				String[] sep2Parts = valStr.split(separator2);
				int sep2Len = sep2Parts.length;

				if (sep1Len == 2 && sep2Len == 1) // h1
				{
					String title = sep1Parts[0];
					HashSet<Long> inlinks = getInlinkSet(sep1Parts[1]);
					if(inlinks.size() < 10)
					{
						continue;
					}
					h1.put(title, inlinks);
				} else if (sep1Len == 1 && sep2Len == 2) {
					String title = sep2Parts[0];
					HashSet<Long> inlinks = getInlinkSet(sep2Parts[1]);
					if(inlinks.size() < 10)
					{
						continue;
					}
					h2.put(title, inlinks);
				} else {
					System.out
							.println("Warning: malformed output from mapper: "
									+ valStr);
					continue;
				}
			}
			// System.out.format("h1 size: %d, h2 size: %d.\n", h1.size(),
			// h2.size());

			for (String keyH2 : h2.keySet()) {
				StringBuffer res = new StringBuffer();
				res.append(keyH2 + separator1);
				boolean flag = false;
				HashSet<Long> setA = h2.get(keyH2);
				int sizeA = setA.size();
				for (String keyH1 : h1.keySet()) {
					HashSet<Long> setB = h2.get(keyH1);
					if (setB != null) {
						int sizeB = setB.size();
						if(sizeB == 0)
						{
							continue;
						}
						//DEBUG System.out.format("sizeA: %d, sizeB: %d\n", sizeA, sizeB);
						//System.out.println(setA);
						//System.out.println(setB);
						HashSet<Long> setC = new HashSet<Long>();
						setC.addAll(setB);
						setC.retainAll(setA);
						if(setC.size() == 0)
						{
							continue;
						}
						//System.out.format("max: %d, intersection: %d.\n", Math.max(sizeA, sizeB), setC.size());
						double para1 = Math.log(Math.max(sizeA, sizeB));
						double para2 = Math.log(setC.size());
						double para4 = Math.log(Math.min(sizeA, sizeB));
						double r_ness = (para1 - para2) / (totalLog - para4);
						if(!flag)
						{
							flag = true;
						}
						res.append(String.format("%s" + separator2 + "%f"
								+ separator1, keyH1, r_ness));
					}
				}
				if (flag)
				{
					context.write(key, new Text(res.toString()));
				}
			}

		}

		private HashSet<Long> getInlinkSet(String string) {
			HashSet<Long> res = new HashSet<Long>();
			String[] parts = string.split(",,");
			for (String id : parts) {
				String[] idCnt = id.split(":");
				if (idCnt.length != 2) {
					System.out.println("Inlink file format warning: " + string);
					continue;
				}
				long idl = Long.parseLong(idCnt[0]);
				res.add(idl);
			}
			return res;
		}
	}

	public static void main(String[] args) throws IOException,
			InterruptedException, ClassNotFoundException {

		// Path in_path = new
		// Path("/home/yu/workspace/10605/projects/data/normalized/");
		// Path in_path = new
		// Path("/home/yu/workspace/10605/projects/data/out/");
		// Path out_path = new
		// Path("/home/yu/workspace/10605/projects/data/out2");
		Path in_path = new Path(args[0]);
		Path out_path = new Path(args[1]);
		Configuration conf = new Configuration();
		Job job = new Job(conf);
		job.setJobName("Relatedness Score");

		job.setMapperClass(RelatednessScore.CSMapper.class);
		job.setReducerClass(RelatednessScore.CSReducer.class);
		job.setJarByClass(RelatednessScore.class);
		job.setNumReduceTasks(3);

		FileInputFormat.setInputPaths(job, in_path);
		FileOutputFormat.setOutputPath(job, out_path);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.waitForCompletion(true);
	}

}

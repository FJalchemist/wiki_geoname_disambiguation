package htables;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class H1MakerMR {

	public static class CSMapper extends Mapper<LongWritable, Text, Text, Text> {
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String article = value.toString();

			if (article.indexOf("#") == 0) {
				return;
			}

			String[] parts = article.split("##,,##,,##");

			if (parts.length != 2 && parts.length != 3) {
				System.out.println("format weird! " + article);
				return;
			} else if (parts.length == 2) {
				String anchorText = parts[0];
				String lnkTarget = parts[1].replace("[", "").replace("]", "");
				//System.out.println(lnkTarget + "****" + anchorText);
				context.write(new Text(lnkTarget), new Text(anchorText));
			} else { // length == 3.
				String title = parts[0].replace("\t", "");
				long lnkCnt = Long.parseLong(parts[1]);
				if(lnkCnt < 10)
				{
					return; //meaningless entry.
				}
				String lnkIdstr = parts[2];
				//System.out.println("*** mapper value: " + String.format("%s##,,##,,##%s", lnkCnt,
				//		lnkIdstr));
				context.write(
						new Text(title),
						new Text(String.format("%s##,,##,,##%s", lnkCnt,
								lnkIdstr)));
			}
		}

	} // Mapper

	public static class CSReducer extends Reducer<Text, Text, Text, Text> {

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			LinkedList<String> anchorTexts = new LinkedList<String>();
			LinkedList<String> articles = new LinkedList<String>();
			String title = key.toString();

			HashMap<String, Long> map = new HashMap<String, Long>();
			for (Text value : values) {
				String content = value.toString();
				//System.out.println(content);
				String[] parts = content.split("##,,##,,##");
				if (parts.length != 2 && parts.length != 1) {
					System.out.println("reducer parse warning: "
							+ value.toString());
					continue;
				} else if (parts.length == 1) {
					anchorTexts.add(content);
				} else { // length == 2
					articles.add(content);
				}

			}
			// System.out.println(anchorTexts.size());

			for (String atext : anchorTexts) {
				for (String article : articles) {
					String value = String.format("%s##,,##,,##%s", title,
							article);
					//System.out.println("!!!value: " + value + " anchorText: " + atext);
					context.write(new Text(atext), new Text(value));
				}

			}
			Text empty = new Text("");
			// context.write(empty, new Text(res.toString()));

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
		job.setJobName("H2");

		job.setMapperClass(H1MakerMR.CSMapper.class);
		job.setReducerClass(H1MakerMR.CSReducer.class);
		job.setJarByClass(H1MakerMR.class);
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

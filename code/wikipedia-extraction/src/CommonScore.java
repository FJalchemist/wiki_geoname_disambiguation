import java.io.IOException;
import java.util.HashMap;
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
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class CommonScore {
				
	
	public static class CSMapper extends 
		Mapper<LongWritable,Text,Text,Text> {
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String article = value.toString();
			
			if (article.indexOf("#") == 0) {
				return;
			}
			
			String [] parts = article.split("\t");
			
			if (parts.length < 3) {
				System.out.print("Unrecognized article: " + article);
			} else {
				
				Long  id = Long.parseLong(parts[0]);
				String title = parts[1];
				Integer linkNum = Integer.parseInt(parts[2]);
				
				
				Pattern pattern = Pattern.compile("\\[(.+?)\\]");
			    Matcher matcher = pattern.matcher(article);
			    while (matcher.find()) {
			    	String link = matcher.group(1);
			    	String [] linkParts = link.split("\\|");
			    	if (linkParts.length == 1) {
					
						context.write(new Text(linkParts[0]), new Text(linkParts[0] + "|,,,,,|" + 1));
					} else if (linkParts.length == 2 ) {
					
						context.write(new Text(linkParts[1]), new Text(linkParts[0] + "|,,,,,|" + 1));
					} else {
						System.out.print("?? linkParts: " + linkParts.length + "  " + link);
					}
			    
			    	linkNum--;
			        
			    }
			    if (linkNum != 0) {
			    	System.err.println(key.get() + ": article right is wrong ..." + linkNum + " " + article);
				}
			}
		}
		
	} // Mapper
	
	public static class CSCombine extends Reducer<Text, Text, Text, Text> { 
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			HashMap<String, Long> map = new HashMap<String, Long>();
			for (Text value: values) {
				String[] linkCnt = value.toString().split("\\|,,,,,\\|");
				if (linkCnt.length > 2)
				{
					System.out.println("Warning: linkCount length > 2");
					continue;
				}
				String linkTarget = linkCnt[0];
				long count = Long.parseLong(linkCnt[1]);
				Long orgCounts = map.get(linkTarget);
				if (orgCounts == null) {
					orgCounts = count;
				} else {
					orgCounts += count;
				}
				map.put(linkTarget, orgCounts);
			}
			
			for (Entry<String, Long> entry : map.entrySet()) {
				context.write(key, new Text(entry.getKey() + "|,,,,,|" + entry.getValue()));
				//String s = entry.getKey() + "|,,,,,|" + entry.getValue();
				//if (s.split("\\|,,,,,\\|").length > 2)
				//{
				//	System.out.println("!!!!: " + s);
				//}
			}
		 }
	}  // Combiner

	
	public static class CSReducer extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context)
			 throws IOException, InterruptedException {
			String anchorKey = key.toString();
			long totalNum = 0;
			HashMap<String, Long> map = new HashMap<String, Long>();
			for (Text value: values) {
				String [] p = value.toString().split("\\|,,,,,\\|");
				if (p.length != 2)
				{
					System.out.println(value.toString());
					continue;
				}
				// TODO: check p's lenght should be 2
				String linkTarget = p[0];
				Long newCounts = null;
				try {
					newCounts = Long.parseLong(p[1]);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				Long orgCounts = map.get(linkTarget);
				if (orgCounts == null) {
					orgCounts = newCounts;
				} else {
					orgCounts += newCounts;
				}
				map.put(linkTarget, orgCounts);
				totalNum += newCounts;			
			}
			//System.out.println(key.toString() + " " + totalNum);
			String separater = "##,,##,,##";
			StringBuffer sb = new StringBuffer();
			sb.append(separater + totalNum + separater + map.size());
			for (Entry<String, Long> entry : map.entrySet()) {
				sb.append(separater + "[" + entry.getKey()+"|" + entry.getValue() + "]");
			}
			context.write(key, new Text(sb.toString()));
		}
	}
	public static void main(String[] args) throws IOException,
	InterruptedException, ClassNotFoundException {
		
		
		//Path in_path = new Path("/home/yu/workspace/10605/projects/data/normalized/");
		//Path in_path = new Path("/home/yu/workspace/10605/projects/data/out/");
		//Path out_path = new Path("/home/yu/workspace/10605/projects/data/out2");
		Path in_path = new Path(args[0]);
		Path out_path = new Path(args[1]);
		Configuration conf = new Configuration();
		Job job = new Job(conf);
		job.setJobName("Common Score");
		
		job.setMapperClass(CommonScore.CSMapper.class);
		job.setCombinerClass(CommonScore.CSCombine.class);
		job.setReducerClass(CommonScore.CSReducer.class);
		job.setJarByClass(CommonScore.class);
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

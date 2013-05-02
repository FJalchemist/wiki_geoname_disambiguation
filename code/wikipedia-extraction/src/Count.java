import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
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

/**
 * @deprecated: already solved the degree distribution problem by one unix command, 
 * Hadoop is useless... 
 * 
 * @author yibinl
 *
 */
public class Count {
	public static class CountMapper extends 
			Mapper<LongWritable,Text,LongWritable,IntWritable> {
		private final int col = 2;
		private final static IntWritable one = new IntWritable(1);
		protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
			String article = value.toString();
			article.trim();
		
			String [] articleArray = article.split("\t");
			if (articleArray.length < 3) {
				System.err.println("array smaller 3: " + article);
			} else {
				try {
					Long i = Long.parseLong(articleArray[col]);
					context.write(new LongWritable(i), one);
				} catch (Exception e) {
					System.out.println("0: " + articleArray[0] + "   1:" + articleArray[1]);
					e.printStackTrace();
					return;
				}
			}
		}
	}
	
	public static class CountReducer extends Reducer<LongWritable, IntWritable, LongWritable, LongWritable> {
		public void reduce(LongWritable key, Iterable<IntWritable> values, Context context)
			 throws IOException, InterruptedException {
			long count = 0;
			for (IntWritable value: values) {
				count++;			
			}	
			context.write(key, new LongWritable(count));
		}
	}
	
	public static void main(String[] args) throws IOException,
	InterruptedException, ClassNotFoundException {
		
		
		//Path in_path = new Path("/home/yu/workspace/10605/projects/data/normalized/");
		Path in_path = new Path("/home/yu/workspace/10605/projects/res1");
		Path out_path = new Path("/home/yu/workspace/10605/projects/res2");
		//Path in_path = new Path(args[0]);
		//Path out_path = new Path(args[1]);
		
		Configuration conf = new Configuration();
		Job job = new Job(conf);
		job.setJobName("Count col 2");
		
		job.setMapperClass(Count.CountMapper.class);
		job.setReducerClass(Count.CountReducer.class);
		job.setJarByClass(Count.class);
		job.setNumReduceTasks(1);

		FileInputFormat.setInputPaths(job, in_path);
		FileOutputFormat.setOutputPath(job, out_path);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(LongWritable.class);
		job.waitForCompletion(true);
	}

}

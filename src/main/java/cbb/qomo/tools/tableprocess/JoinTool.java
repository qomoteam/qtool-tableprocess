package cbb.qomo.tools.tableprocess;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class JoinTool extends Configured implements Tool {

	public String file1;
	public String file2;
	public String output;

	public int column1;
	public int column2;

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		int res = ToolRunner.run(conf, new JoinTool(), args);
		System.exit(res);
	}

	public int run(String[] args) throws Exception {
		file1 = args[0];
		column1 = Integer.parseInt(args[1]);
		file2 = args[2];
		column2 = Integer.parseInt(args[3]);
		output = args[4];
		Job job = new Job(getConf());
		Configuration conf = job.getConfiguration();
		conf.set("file1", file1.toString());
		conf.set("file2", file2.toString());
		conf.set("column1", Integer.toString(column1));
		conf.set("column2", Integer.toString(column2));
		job.setJarByClass(Join.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setMapperClass(Join.TheMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Join.CompositeValue.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		job.setReducerClass(Join.TheReducer.class);
		FileInputFormat.addInputPath(job, new Path(file1));
		FileInputFormat.addInputPath(job, new Path(file2));
		FileOutputFormat.setOutputPath(job, new Path(output));
		return job.waitForCompletion(true) ? 0 : 1;
	}
}

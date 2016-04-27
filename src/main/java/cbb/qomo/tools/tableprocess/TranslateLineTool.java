package cbb.qomo.tools.tableprocess;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class TranslateLineTool extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
    String[] remainArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

		int res = ToolRunner.run(conf, new TranslateLineTool(), remainArgs);
		System.exit(res);
	}

	public int run(String[] args) throws Exception {
		String input = args[0];
		String output = args[1];
		String expr = args[2];
		expr = expr.trim();
		if (expr.charAt(0) == '"' && expr.charAt(expr.length() - 1) == '"') {
			expr = expr.substring(1, expr.length() - 1).trim();
		}
		System.out.println("Expr : " + expr);
		Job job = new Job(getConf());
		Configuration conf = job.getConfiguration();
		conf.set("expr", expr);

		job.setJarByClass(TranslateLine.class);
		job.setMapperClass(TranslateLine.TheMapper.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(0);
		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));
		if (job.waitForCompletion(true))
			return 0;
		else
			return 1;
	}

}

package cbb.qomo.tools.tableprocess;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class SortByColumnTool extends Configured implements Tool {
	public String input;
	public String output;
	public Integer column;
	public String type;
	public String order;

	private Path tmpDir;

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		int res = ToolRunner.run(conf, new SortByColumnTool(), args);
		System.exit(res);
	}

	public static class NumberComparator extends WritableComparator {
		protected NumberComparator() {
			super(Text.class, true);
		}

		@Override
		public int compare(WritableComparable a, WritableComparable b) {
			Double da = Double.valueOf(a.toString());
			Double db = Double.valueOf(b.toString());
			return da.compareTo(db);
		}
	}

	public static class ReverseNumberComparator extends NumberComparator {
		@Override
		public int compare(WritableComparable a, WritableComparable b) {
			return -super.compare(a, b);
		}
	}

	public static class ReverseTextComparator extends Text.Comparator {
		@Override
		public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
			return -super.compare(b1, s1, l1, b2, s2, l2);
		}
	}

	public Job preprocess(Path preproc, Class<? extends Writable> keyClass,
			Class<? extends Mapper> mapperClass) throws IOException {
		Job job = new Job(getConf());
		Configuration conf = job.getConfiguration();
		conf.setStrings("column", column.toString());
		job.setJarByClass(this.getClass());
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		job.setOutputKeyClass(keyClass);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(0);
		job.setMapperClass(mapperClass);
		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, preproc);
		return job;
	}

	public <K extends Writable> boolean setup(
			Class<? extends RawComparator> compClass) throws IOException,
			ClassNotFoundException, InterruptedException {
		Job job = new Job(getConf());
		Configuration conf = job.getConfiguration();
		conf.setStrings("column", column.toString());
		job.setJarByClass(this.getClass());
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		job.setMapperClass(SortByColumnAlphabetic.TheMapper.class);
		job.setReducerClass(SortByColumnAlphabetic.TheReducer.class);
		job.setNumReduceTasks(1);
		job.setSortComparatorClass(compClass);

		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));

		// job.setPartitionerClass(TotalOrderPartitioner.class);
		// TotalOrderPartitioner.setPartitionFile(job.getConfiguration(),
		// tmpDir.suffix("/part"));
		// InputSampler.Sampler<K, Text> sampler = new
		// InputSampler.RandomSampler<K, Text>(
		// 0.01, 100);
		// System.out.println(job.getNumReduceTasks());
		// InputSampler.writePartitionFile(job, sampler);

		return job.waitForCompletion(true);
	}

	public int run(String[] args) throws Exception {
		input = args[0];
		output = args[1];
		column = Integer.parseInt(args[2]);
		type = args[3];
		order = args[4];
		tmpDir = new Path(args[5]);
		System.out.println(type);
		boolean success = false;
		if (type.equals("alphabet")) {
			if (order.equals("des")) {
				success = setup(ReverseTextComparator.class);
			} else {
				success = setup(Text.Comparator.class);
			}
		} else {
			if (order.equals("des")) {
				success = setup(ReverseNumberComparator.class);
			} else {
				success = setup(NumberComparator.class);
			}
		}
		if (success)
			return 0;
		else
			return 1;
	}
}

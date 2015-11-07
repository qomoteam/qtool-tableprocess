package cbb.qomo.tools.tableprocess;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FilterLineRegex {
	public static class TheMapper extends
			Mapper<LongWritable, Text, NullWritable, Text> {
		private Pattern pattern;

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			Configuration conf = context.getConfiguration();
			pattern = Pattern.compile(conf.get("expr"));
		}

		@Override
		protected void map(LongWritable keyIn, Text valueIn, Context context)
				throws IOException, InterruptedException {
			NullWritable nil = NullWritable.get();
			if (pattern.matcher(valueIn.toString()).find()) {
				context.write(nil, valueIn);
			}
		}
	}
}

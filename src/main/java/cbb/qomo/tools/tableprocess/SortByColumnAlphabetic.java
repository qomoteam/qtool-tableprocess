package cbb.qomo.tools.tableprocess;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import qomo.common.CSVUtil;

public class SortByColumnAlphabetic {

	public static class TheMapper extends
			Mapper<LongWritable, Text, Text, Text> {

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			Configuration conf = context.getConfiguration();
			int idx = CSVUtil.parseColumn(conf.get("column"));
			String str = CSVUtil.split(value.toString())[idx];
			context.write(new Text(str), value);
		}
	}

	public static class TheReducer extends
			Reducer<Text, Text, NullWritable, Text> {
		public static NullWritable nil = NullWritable.get();

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			for (Text value : values) {
				context.write(nil, value);
			}
		}
	}
}

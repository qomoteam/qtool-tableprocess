package cbb.qomo.tools.tableprocess;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import qomo.common.CSVUtil;

public class Join {
	public static class CompositeValue implements
			WritableComparable<CompositeValue> {

		public IntWritable file;
		public Text text;

		public void readFields(DataInput in) throws IOException {
			file = new IntWritable();
			text = new Text();
			file.readFields(in);
			text.readFields(in);
		}

		public void write(DataOutput out) throws IOException {
			file.write(out);
			text.write(out);
		}

		public int compareTo(CompositeValue that) {
			int b = text.toString().compareTo(that.text.toString());
			if (b == 0) {
				return file.compareTo(that.file);
			}
			return b;
		}
	}

	public static class TheMapper extends
			Mapper<LongWritable, Text, Text, CompositeValue> {
		private String[] files;
		private int[] idx;

		private int getFileId(String file) {
			for (int i = 0; i < 2; i++) {
				if (file.startsWith(files[i]))
					return i;
			}
			return -1;
		}

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			Configuration conf = context.getConfiguration();
			int n = 2;
			files = new String[n];
			idx = new int[n];
			for (int i = 0; i < n; i++) {
				files[i] = conf.get("file" + (i + 1));
				idx[i] = CSVUtil.parseColumn(conf.get("column" + (i + 1)));
			}
		}

		@Override
		protected void map(LongWritable keyIn, Text valueIn, Context context)
				throws IOException, InterruptedException {
			CompositeValue valueOut = new CompositeValue();
			valueOut.text = valueIn;
			int id = getFileId(((FileSplit) context.getInputSplit()).getPath()
					.toString());
			valueOut.file = new IntWritable(id);
			String keyOut = CSVUtil.split(valueIn.toString())[idx[id]];
			context.write(new Text(keyOut), valueOut);
		}
	}

	public static class TheReducer extends
			Reducer<Text, CompositeValue, NullWritable, Text> {

		private final static NullWritable nil = NullWritable.get();

		public void reduce(Text key, Iterable<CompositeValue> values,
				Context context) throws IOException, InterruptedException {
			@SuppressWarnings("unchecked")
			ArrayList<Text>[] lines = new ArrayList[2];
			for (int i = 0; i < lines.length; i++) {
				lines[i] = new ArrayList<Text>();
			}
			for (CompositeValue value : values) {
				lines[value.file.get()].add(value.text);
			}
			for (Text line0 : lines[0]) {
				for (Text line1 : lines[1]) {
					context.write(
							nil,
							new Text(CSVUtil.join(line0.toString(),
									line1.toString())));
				}
			}
		}
	}
}

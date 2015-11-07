package cbb.qomo.tools.tableprocess;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import cbb.qomo.tools.tableprocess.expression.SemanticAnalyzer;
import cbb.qomo.tools.tableprocess.expression.SyntaxAnalyzer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import qomo.common.CSVUtil;

public class FilterLineBool {
	public static class TheMapper extends
			Mapper<LongWritable, Text, NullWritable, Text> {
		private SemanticAnalyzer sa;

		private Boolean satisfy(String[] fields) {
			HashMap<String, String> env = CSVUtil.buildEnv(fields);
			sa.setEnv(env);
			return (Boolean) sa.eval();
		}

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			Configuration conf = context.getConfiguration();
			String expr = conf.get("expr");
			this.sa = new SemanticAnalyzer(new SyntaxAnalyzer(new StringReader(
					expr)));
		}

		@Override
		protected void map(LongWritable keyIn, Text valueIn, Context context)
				throws IOException, InterruptedException {
			NullWritable nil = NullWritable.get();
			if (satisfy(CSVUtil.split(valueIn.toString()))) {
				context.write(nil, valueIn);
			}
		}
	}
}

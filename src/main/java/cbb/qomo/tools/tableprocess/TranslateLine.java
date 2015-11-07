package cbb.qomo.tools.tableprocess;

import cbb.qomo.tools.tableprocess.expression.SemanticAnalyzer;
import cbb.qomo.tools.tableprocess.expression.SyntaxAnalyzer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import qomo.common.CSVUtil;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

public class TranslateLine {
	public static class TheMapper extends
			Mapper<LongWritable, Text, NullWritable, Text> {
		private SemanticAnalyzer[] sa;

		private String[] eval(String[] fields) {
			HashMap<String, String> env = CSVUtil.buildEnv(fields);
			String[] result = new String[sa.length];
			for (int i = 0; i < sa.length; i++) {
				sa[i].setEnv(env);
				result[i] = sa[i].eval().toString();
			}
			return result;
		}

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			Configuration conf = context.getConfiguration();
			String[] expr = conf.get("expr").split("\\s+");
			sa = new SemanticAnalyzer[expr.length];
			for (int i = 0; i < sa.length; i++) {
				sa[i] = new SemanticAnalyzer(new SyntaxAnalyzer(
						new StringReader(expr[i])));
			}
		}

		@Override
		protected void map(LongWritable keyIn, Text valIn, Context context)
				throws IOException, InterruptedException {
			String[] result = eval(CSVUtil.split(valIn.toString()));
			if (result != null) {
				context.write(NullWritable.get(), new Text(CSVUtil.join(result)));
			}
		}
	}
}

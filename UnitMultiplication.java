import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UnitMultiplication {

    public static class TransitionMapper extends Mapper<Object, Text, Text, Text> {

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            //input format: fromPage\t toPage1,toPage2,toPage3
            //target: build transition matrix unit -> fromPage\t toPage=probability
	    String line = value.toString().trim();
	    String[] relations = line.split("\t");

	    if (relation.length() == 1 || relations[1].trim().equals("")) {
		    return;
	    }
	    String from = relation[0];
	    String[] destinations = relations[1].split(",");

	    for (String dest : destinations) {
		    context.write(new Text(from), new Text(dest + "=" + (double) 1 / destionations.length);
	    }
        }
    }

    public static class PRMapper extends Mapper<Object, Text, Text, Text> {

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            //input format: Page\t PageRank
            //target: write to reducer
	    String line = value.toString().trim();
	    String[] pagesWei = line.split("\t");

	    context.write(new Text(pagesWei[0]), new Text(pagesWei[1]));
        }
    }

    public static class MultiplicationReducer extends Reducer<Text, Text, Text, Text> {


        @Override
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            //input key = fromPage value=<toPage=probability..., pageRank>
            //target: get the unit multiplication
	    List<String> transitionUnit = new ArrayList<>();
	    double prUnit = 0;

	    for (Text value : values) {
		    if (value.toString.contains("=")) {
			    transitionUnit.add(value.toString());
		    } else {
			    prUnit = Double.parseDouble(value.toString());
		    }
	    }
	    for (String unit : transitionUnit) {
		    String toPage = unit.split("=")[0];
		    double output = Double.parseDouble(unit.split("=")[1]) * prUnit;
		    context.write(new Text(toPage), new Text(String.valueOf(output)));
	    }
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        job.setJarByClass(UnitMultiplication.class);

	// add two mappers into chain
	ChainMapper.addMapper(job, TransitionMapper.class, Object.class, Text.class, Text.class, Text.class, conf);
	ChainMapper.addMapper(job, PRMapper.class, Object.class, Text.class, Text.class, Text.class, conf);

        job.setReducerClass(MultiplicationReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, TransitionMapper.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, PRMapper.class);

        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        job.waitForCompletion(true);
    }

}
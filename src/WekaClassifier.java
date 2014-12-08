import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToNominal;

/* Using weka to build classifier
 * Format of the class is based on examples in weka.wikispaces.com
 */

public class WekaClassifier {
	protected String data_file = null;
	protected Instances raw_data = null;
	protected Instances filted_data = null;
	protected Filter filter = null;
	protected Classifier classifier = null;
	protected Evaluation evaluator = null;
	
	public WekaClassifier() {
		this(1);
	}
	
	public WekaClassifier(int i) {
		try {
			setFilter(2);
			setClassifier(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getRawData() throws Exception {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
							new FileReader(data_file));
			raw_data = new Instances(reader);
			raw_data.setClassIndex(raw_data.numAttributes()-1);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader!=null) reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setFilter(int i) throws Exception {
		String[] options = {"-R", "1, 2"};
		switch (i) {
		case 1:
			Remove remove = new Remove();
			remove.setOptions(options);
			filter = remove;
			break;
		case 2:
			StringToNominal STT = new StringToNominal();
			STT.setOptions(options);
			filter = STT;
			break;
		}
	}
	
	public void setClassifier(int i) throws Exception {
		// choose a base (algorithm)
		switch (i) {
		case 1:
			classifier = new J48();
			break;
		case 2:
			classifier = new NaiveBayesUpdateable();			
			break;
		case 3:
		default:
			classifier = new IBk();
			break;
		}
	}
	
	public void filtData() throws Exception {
		filter.setInputFormat(raw_data);
		filted_data = Filter.useFilter(raw_data, filter);
	}
	
	public void evaluate() throws Exception {
		// perform 10-fold cross-validiation
		evaluator = new Evaluation(filted_data);
		evaluator.crossValidateModel(classifier, filted_data, 10, new Random(1));
	}
	
	public void getSummaryResult() throws Exception {
		System.out.println(evaluator.toSummaryString());
	} 
	
	public void getDetailResult() throws Exception {
		System.out.println(evaluator.toClassDetailsString());
	}
	
	public void execute() throws Exception {
		try {
			// first, gain data from file
			// default data file:
			data_file = "data\\ppi.arff";
			getRawData();
			// filt data
			filtData();
			// run and get summary of result
			evaluate();
			getDetailResult();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

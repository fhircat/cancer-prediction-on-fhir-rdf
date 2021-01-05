package method;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;

import hr.irb.fastRandomForest.FastRandomForest;
import jamia.network.main.Scores;
import jamia.network.main.Step2_Classification;
import ml.dmlc.xgboost4j.java.Booster;
import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.XGBoost;
import mloss.roc.Curve;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibLINEAR;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.LibSVMSaver;
import weka.core.converters.ConverterUtils.DataSource;

public class Classification {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static String RandomForest = "random_forest";
	public static String SVM = "svm";
	public static String logisticRegress = "logistic_regression";
	public static String NaiveBayes = "naive_bayes";

	public static void classify(String classifier_string, String datafile,
			int nfolder) throws Exception {
		String inputdatafile;
		inputdatafile = datafile;

		DataSource source = new DataSource(inputdatafile);
		Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes() - 1);

		int seed = 1024; // the seed for randomizing the data

		Random rand = new Random(seed); // create seeded number generator
		Instances randData = new Instances(data); // create copy of original
													// data
		randData.randomize(rand); // randomize data with number generator
		randData.stratify(nfolder);

		for (int i = 0; i < nfolder; i++) {
			Instances train_local = randData.trainCV(nfolder, i, rand);
			Instances test_local = randData.testCV(nfolder, i);

			String xgb_input_train = "tmp.train_" + i + ".data";
			String xgb_input_test = "tmp.test_" + i + ".data";
			LibSVMSaver saver = new LibSVMSaver();
			saver.setInstances(train_local);

			saver.setFile(new File(xgb_input_train));
			saver.writeBatch();
			saver.setInstances(test_local);
			saver.setFile(new File(xgb_input_test));
			saver.writeBatch();

			if (classifier_string.equals(logisticRegress)) {
				LibLINEAR logistic = new LibLINEAR();
				String[] options = new String[2];
				options[0] = "-S";
				options[1] = "0";
				logistic.setOptions(options);

				// build classifier
				logistic.buildClassifier(train_local);
				Evaluation eval = new Evaluation(train_local);
				eval.evaluateModel(logistic, test_local);
			}

			if (classifier_string.equals(SVM)) {

				LibSVM libSVM = new LibSVM();
				libSVM.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_LINEAR, LibSVM.TAGS_KERNELTYPE));
				libSVM.setSVMType(new SelectedTag(LibSVM.SVMTYPE_C_SVC, LibSVM.TAGS_SVMTYPE));

				// build classifier
				libSVM.buildClassifier(train_local);
				Evaluation eval = new Evaluation(train_local);
				eval.evaluateModel(libSVM, test_local);
			}

			if (classifier_string.equals(RandomForest)) {

				FastRandomForest rf = new FastRandomForest();
				rf.setNumTrees(500);
				
				rf.buildClassifier(train_local);

				Evaluation eval = new Evaluation(train_local);
				eval.evaluateModel(rf, test_local);
			}

			if (classifier_string.equals(NaiveBayes)) {

				weka.classifiers.bayes.NaiveBayes nb = new weka.classifiers.bayes.NaiveBayes();

				// build classifier
				nb.buildClassifier(train_local);
				Evaluation eval = new Evaluation(train_local);
				eval.evaluateModel(nb, test_local);
			}
		
		}

	}

}

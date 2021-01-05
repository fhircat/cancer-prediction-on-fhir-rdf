package data.dataCollection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import org.eclipse.persistence.jpa.rs.util.list.LinkList;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.PrincipalComponents;
import weka.filters.unsupervised.attribute.Remove;

public class FeatureSelection {
	
	public static String pca="PCA";
	public static String information_gain="INFO";
	
	public static void main(String[] args) throws Exception{
	}
	
	public static void featureReduction(String arff_input, String filter_arff, String type, int topN) throws Exception{
		DataSource source = new DataSource(arff_input);
		Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes()-1);
		Instances filtered_data = null;
		if(type.equals(FeatureSelection.information_gain)){
			AttributeSelection selector=new AttributeSelection();
			InfoGainAttributeEval eval = new InfoGainAttributeEval();
			Ranker search = new Ranker();
//			search.setThreshold(0.01);
			search.setNumToSelect(topN);
			search.setOptions(new String[] { "-T", "0.01" });
			selector.setEvaluator(eval);
			selector.setFolds(10);
			selector.setRanking(true);
			selector.setSearch(search);
			selector.setSeed(1024);
//			selector.setXval(true);
			
//			selector.selectAttributesCVSplit(data);
//			String resutls=selector.CrossValidateAttributes();
			selector.SelectAttributes(data);	
			
			double[][] rank_results=selector.rankedAttributes();
			for (int i = 0; i < rank_results.length; i++) {
				for (int j = 0; j < rank_results[i].length; j++) {
//					System.out.print(rank_results[i][j]+" ");
				}
//				System.out.println();
			}
			System.out.println(selector.numberAttributesSelected());
			filtered_data =selector.reduceDimensionality(data);
			
		}else if(type.equals(FeatureSelection.pca)){
			PrincipalComponents pca = new PrincipalComponents();
			pca.setInputFormat(data);
			pca.setMaximumAttributes(200);
			filtered_data = Filter.useFilter(data, pca);
		}
		
		String outputFilename = filter_arff;
		DataSink.write(outputFilename, filtered_data);
	}
	
	
	public static void featureFiltering(String arff_input, String filter_arff, ArrayList<String> remainedFeatures) throws Exception{
		DataSource source = new DataSource(arff_input);
		Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes()-1);
		
		int[] attributes=new int[remainedFeatures.size()];
		int j=0;
		for (int i = 0; i < data.numAttributes()-1; i++) {
			if(!remainedFeatures.contains(data.attribute(i).name())){
				attributes[j]=i;
				j++;
			}
		
		}
		
		Remove r = new Remove();
		r.setAttributeIndicesArray(attributes);
		r.setInputFormat(data);
		Instances filtered_data = Filter.useFilter(data, r);

		String outputFilename = filter_arff;
		DataSink.write(outputFilename, filtered_data);
	}
	
	
	public static void featureFiltering_multiple(String arff_input_1,String arff_input_2, String filter_arff, ArrayList<String> remainedFeatures) throws Exception{
		
		Instances data_1 = new DataSource(arff_input_1).getDataSet();
		data_1.setClass(data_1.attribute(data_1.numAttributes()-1));
		Instances data_2_tmp = new DataSource(arff_input_2).getDataSet();
		data_2_tmp.setClass(data_2_tmp.attribute(data_2_tmp.numAttributes()-1));
		Instances data_2=removeClass(data_2_tmp);
		
		Instances data=Instances.mergeInstances(data_2, data_1);
		data.setClassIndex(data.numAttributes()-1);
		
		ArrayList<Integer> removed =new ArrayList<>();
		for (int i = 0; i < data.numAttributes()-1; i++) {
			if(!remainedFeatures.contains(data.attribute(i).name())){
				removed.add(i);
			}
		}
		
		int[] attributes=new int[removed.size()];
		for (int i = 0; i < attributes.length; i++) {
			attributes[i]=removed.get(i);
		}
		
		Remove r = new Remove();
		r.setAttributeIndicesArray(attributes);
		r.setInputFormat(data);
		Instances filtered_data = Filter.useFilter(data, r);

		String outputFilename = filter_arff;
		DataSink.write(outputFilename, filtered_data);
	}
	
	public static void featureReduction_multiple(String arff_input_1,String arff_input_2, String filter_arff, String type, int topN) throws Exception {
		Instances instance_1=featureReduction(arff_input_1,  type, topN);
		Instances instance_2_tmp=featureReduction(arff_input_2,  type, topN);
		Instances instance_2=removeClass(instance_2_tmp);
		
		Instances instances=Instances.mergeInstances(instance_2, instance_1);
		instances.setClassIndex(instances.numAttributes()-1);
		DataSink.write(filter_arff, instances);
	}
	
	public static void featureReduction_multiple(String arff_input_1,String arff_input_2, String arff_input_3,String filter_arff, String type, int topN) throws Exception {
		Instances instance_1=featureReduction(arff_input_1,  type, topN);
		Instances instance_2_tmp=featureReduction(arff_input_2,  type, topN);
		Instances instance_3_tmp=featureReduction(arff_input_3,  type, topN);
		Instances instance_2=removeClass(instance_2_tmp);
		Instances instance_3=removeClass(instance_3_tmp);
		
		Instances instances_23=Instances.mergeInstances(instance_2, instance_3);
		Instances instances=Instances.mergeInstances(instances_23, instance_1);
		instances.setClassIndex(instances.numAttributes()-1);
		DataSink.write(filter_arff, instances);
	}
	
	
	public static void featureReduction_multiple_seperatereduced(ArrayList<String> files,String filter_arff, String type, int topN) throws Exception {
		Instances instance_top=featureReduction(files.get(0),  type, topN);
		ArrayList<Instances> instances=new ArrayList<>();
		
		if(files.size()>1){
			for (int i = 1; i < files.size(); i++) {
				Instances instance_tmp=featureReduction(files.get(i),  type, topN);	
				Instances instance_sub=removeClass(instance_tmp);
				instances.add(instance_sub);
			}	
		}
		Instances instance_final=instance_top;
		if(instances.size()>0){
			Instances instances_merged=instances.get(0);
			for (int i = 1; i < instances.size(); i++) {
				instances_merged=Instances.mergeInstances(instances_merged, instances.get(i));
			}
			 instance_final=Instances.mergeInstances(instances_merged, instance_top);
		}
		
		instance_final.setClassIndex(instance_final.numAttributes()-1);
		DataSink.write(filter_arff, instance_final);
	}
	
	
	public static void featureReduction_multiple_allreduced(ArrayList<String> files,String sex_age_file, String filter_arff, String type, int topN) throws Exception {
		Instances instance_top=getInstance(files.get(0));
		ArrayList<Instances> instances=new ArrayList<>();
		
		if(files.size()>1){
			for (int i = 1; i < files.size(); i++) {
				Instances instance_tmp=getInstance(files.get(i));	
				Instances instance_sub=removeClass(instance_tmp);
				instances.add(instance_sub);
			}	
		}
		Instances instance_final=instance_top;
		if(instances.size()>0){
			Instances instances_merged=instances.get(0);
			for (int i = 1; i < instances.size(); i++) {
				instances_merged=Instances.mergeInstances(instances_merged, instances.get(i));
			}
			 instance_final=Instances.mergeInstances(instances_merged, instance_top);
		}
		
		instance_final.setClassIndex(instance_final.numAttributes()-1);
		Instances instance_return=featureReduction(instance_final,  type, topN);	
		
		Instances instance_tmp=getInstance(sex_age_file);	
		Instances instance_sexage=removeClass(instance_tmp);
		
		Instances instance_finalreturn=Instances.mergeInstances(instance_sexage, instance_return);
		
		DataSink.write(filter_arff, instance_finalreturn);
	}
	
	
	public static void featureReduction_multiple_allreduced(ArrayList<String> files, String filter_arff, String type, int topN) throws Exception {
		Instances instance_top=getInstance(files.get(0));
		ArrayList<Instances> instances=new ArrayList<>();
		
		if(files.size()>1){
			for (int i = 1; i < files.size(); i++) {
				Instances instance_tmp=getInstance(files.get(i));	
				Instances instance_sub=removeClass(instance_tmp);
				instances.add(instance_sub);
			}	
		}
		Instances instance_final=instance_top;
		if(instances.size()>0){
			Instances instances_merged=instances.get(0);
			for (int i = 1; i < instances.size(); i++) {
				instances_merged=Instances.mergeInstances(instances_merged, instances.get(i));
			}
			 instance_final=Instances.mergeInstances(instances_merged, instance_top);
		}
		
		instance_final.setClassIndex(instance_final.numAttributes()-1);
		Instances instance_return=featureReduction(instance_final,  type, topN);	
		
		
		DataSink.write(filter_arff, instance_return);
	}
	
	
	
	
	
	
	public static void featureReduction_multiple(String arff_input_1,String arff_input_2, String arff_input_3,String arff_input_4,
			String filter_arff, String type, int topN) throws Exception {
		Instances instance_1=featureReduction(arff_input_1,  type, topN);
		Instances instance_2_tmp=featureReduction(arff_input_2,  type, topN);
		Instances instance_3_tmp=featureReduction(arff_input_3,  type, topN);
		Instances instance_4_tmp=featureReduction(arff_input_4,  type, topN);
		
		Instances instance_2=removeClass(instance_2_tmp);
		Instances instance_3=removeClass(instance_3_tmp);
		Instances instance_4=removeClass(instance_4_tmp);
		
		Instances instances_23=Instances.mergeInstances(instance_2, instance_3);
		Instances instances_234=Instances.mergeInstances(instances_23, instance_4);
		Instances instances=Instances.mergeInstances(instances_234, instance_1);
		instances.setClassIndex(instances.numAttributes()-1);
		DataSink.write(filter_arff, instances);
	}
	
	
	public static void featureReduction_multiple(String arff_input_1,String arff_input_2, String arff_input_3,String arff_input_4,
			String arff_input_5,String filter_arff, String type, int topN) throws Exception {
		Instances instance_1=featureReduction(arff_input_1,  type, topN);
		Instances instance_2_tmp=featureReduction(arff_input_2,  type, topN);
		Instances instance_3_tmp=featureReduction(arff_input_3,  type, topN);
		Instances instance_4_tmp=featureReduction(arff_input_4,  type, topN);
		Instances instance_5_tmp=featureReduction(arff_input_5,  type, topN);
		
		Instances instance_2=removeClass(instance_2_tmp);
		Instances instance_3=removeClass(instance_3_tmp);
		Instances instance_4=removeClass(instance_4_tmp);
		Instances instance_5=removeClass(instance_5_tmp);
		
		Instances instances_23=Instances.mergeInstances(instance_2, instance_3);
		Instances instances_234=Instances.mergeInstances(instances_23, instance_4);
		Instances instances_2345=Instances.mergeInstances(instances_234, instance_5);
		Instances instances=Instances.mergeInstances(instances_2345, instance_1);
		instances.setClassIndex(instances.numAttributes()-1);
		DataSink.write(filter_arff, instances);
	}
	
	
	public static Instances featureReduction(String arff_input, String type, int topN) throws Exception{
		DataSource source = new DataSource(arff_input);
		Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes()-1);
		Instances filtered_data = null;
		if(type.equals(FeatureSelection.information_gain)){
			AttributeSelection selector=new AttributeSelection();
			InfoGainAttributeEval eval = new InfoGainAttributeEval();
			Ranker search = new Ranker();
//			search.setThreshold(0.01);
			search.setNumToSelect(topN);
			search.setOptions(new String[] { "-T", "0.01" });
			selector.setEvaluator(eval);
			selector.setFolds(10);
			selector.setRanking(true);
			selector.setSearch(search);
			selector.setSeed(1024);
//			selector.setXval(true);
			
//			selector.selectAttributesCVSplit(data);
//			String resutls=selector.CrossValidateAttributes();
			selector.SelectAttributes(data);	
			
			double[][] rank_results=selector.rankedAttributes();
			for (int i = 0; i < rank_results.length; i++) {
				for (int j = 0; j < rank_results[i].length; j++) {
//					System.out.print(rank_results[i][j]+" ");
				}
//				System.out.println();
			}
			System.out.println(selector.numberAttributesSelected());
			filtered_data =selector.reduceDimensionality(data);
			
		}else if(type.equals(FeatureSelection.pca)){
			PrincipalComponents pca = new PrincipalComponents();
			pca.setInputFormat(data);
			pca.setMaximumAttributes(200);
			filtered_data = Filter.useFilter(data, pca);
		}
		 return filtered_data;
	}
	
	
	
	public static Instances featureReduction(Instances data, String type, int topN) throws Exception{
		Instances filtered_data = null;
		if(type.equals(FeatureSelection.information_gain)){
			AttributeSelection selector=new AttributeSelection();
			InfoGainAttributeEval eval = new InfoGainAttributeEval();
			Ranker search = new Ranker();
//			search.setThreshold(0.01);
			search.setNumToSelect(topN);
			search.setOptions(new String[] { "-T", "0.01" });
			selector.setEvaluator(eval);
			selector.setFolds(10);
			selector.setRanking(true);
			selector.setSearch(search);
			selector.setSeed(1024);
//			selector.setXval(true);
			
//			selector.selectAttributesCVSplit(data);
//			String resutls=selector.CrossValidateAttributes();
			selector.SelectAttributes(data);	
			
			double[][] rank_results=selector.rankedAttributes();
			for (int i = 0; i < rank_results.length; i++) {
				for (int j = 0; j < rank_results[i].length; j++) {
//					System.out.print(rank_results[i][j]+" ");
				}
//				System.out.println();
			}
			System.out.println(selector.numberAttributesSelected());
			filtered_data =selector.reduceDimensionality(data);
			
		}else if(type.equals(FeatureSelection.pca)){
			PrincipalComponents pca = new PrincipalComponents();
			pca.setInputFormat(data);
			pca.setMaximumAttributes(200);
			filtered_data = Filter.useFilter(data, pca);
		}
		 return filtered_data;
	}
	
	public static Instances getInstance(String arff_input) throws Exception{
		DataSource source = new DataSource(arff_input);
		Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes()-1);
		return data;
	}
	
	 public static Instances removeClass(Instances inst) {
		  Remove af = new Remove();
		  Instances retI = null;

		  try {
		    if (inst.classIndex() < 0) {
		      retI = inst;
		    } else {
		      af.setAttributeIndices("" + (inst.classIndex() + 1));
		      af.setInvertSelection(false);
		      af.setInputFormat(inst);
		      retI = Filter.useFilter(inst, af);
		    }
		  } catch (Exception e) {
		    e.printStackTrace();
		  }
		  return retI;
		}
	
	public void featureSelection(String arfffile) throws Exception{
		
		DataSource source = new DataSource(arfffile);
		Instances data = source.getDataSet();

		AttributeSelection selector=new AttributeSelection();
		InfoGainAttributeEval eval = new InfoGainAttributeEval();
		Ranker search = new Ranker();
		search.setThreshold(0.01);
		selector.setEvaluator(eval);
		selector.setFolds(10);
		selector.setRanking(true);
		selector.setSearch(search);
		selector.setSeed(1024);
		selector.setXval(true);
		selector.selectAttributesCVSplit(data);
		String resutls=selector.CrossValidateAttributes();
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File("tmp.txt")));
		System.out.println(resutls);
		bw.write(resutls);
		bw.flush();
		bw.close();
	}	
	
	
}


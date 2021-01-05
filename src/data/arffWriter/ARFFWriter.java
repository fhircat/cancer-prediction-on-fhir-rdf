package data.arffWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import data.LabtestBean;
import data.Prediciton_PatientBean;
import data.dataCollection.FeatureSelection;
import data.parser.Parser_diagnosis;
import data.parser.Parser_geneReport;
import data.parser.Parser_historical;
import data.parser.Parser_labtest;
import data.parser.Parser_medication;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class ARFFWriter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void update_others(HashMap<BigInteger,HashSet<String>> map, ArrayList<String> value_list){
		value_list.add("null");
		for(Entry<BigInteger,HashSet<String>> entry:map.entrySet()){
			for(String string:entry.getValue()){
				if(!value_list.contains(string)){
					value_list.add(string);
				}
			}
		}
	}
	
	public static void update_lab(HashMap<BigInteger, HashMap<String, LabtestBean>> map, ArrayList<String> value_list){
		HashMap<String,HashSet<String>> control_map=new HashMap<>();
		for(Entry<BigInteger,HashMap<String, LabtestBean>> entry_1:map.entrySet()){
			for(Entry<String, LabtestBean> entry_2:entry_1.getValue().entrySet()){
				LabtestBean bean=entry_2.getValue();
				String value=bean.getCode()+"("+bean.getValue()+")";
				
				if(control_map.containsKey(bean.getCode())){
					control_map.get(bean.getCode()).add(value);
				}else{
					HashSet<String> set=new HashSet<>();
					set.add(value);
					control_map.put(bean.getCode(), set);
				}
			}
		}
		
		for(Entry<String,HashSet<String>> entry:control_map.entrySet()){
			for(String string:entry.getValue()){
				value_list.add(string);
			}
			String value=entry.getKey()+"(null)";
			if(!value_list.contains(value)){
				value_list.add(value);
			}
		}
		
	}
	
	/**
	 * featureSourceType 1= genetic only
	 * 
	 * featureSourceType 2= labtest only
	 * 
	 * featureSourceType 3= diagnosis only
	 * 
	 * featureSourceType 4= medication only
	 *
	 * featureSourceType 5= historical only
	 * 
	 * 
	 * @param data_map
	 * @param type
	 * @param arffTrain
	 * @throws Exception
	 */
	public static void writeARFF_all(ArrayList<BigInteger> patientList,
			HashMap<BigInteger, Prediciton_PatientBean> data_patient, 
			HashMap<BigInteger, String> data_sex,
			HashMap<BigInteger, String> data_age,
			HashMap<BigInteger, HashSet<String>> data_genes,
			HashMap<BigInteger, HashMap<String, LabtestBean>> data_labtests,
			HashMap<BigInteger, HashSet<String>> data_diagnosis,
			HashMap<BigInteger, HashSet<String>> data_medications,
			HashMap<BigInteger, HashSet<String>> data_historicals,
			int reducted_dimension) throws Exception {
		
		String tmp_0="demographic_tmp.arff";
		String tmp_1="genetic_tmp.arff";
		String tmp_2="diagnosis_tmp.arff";
		String tmp_3="medication_tmp.arff";
		String tmp_4="historical_tmp.arff";
		String tmp_5="labtest_tmp.arff";		
		
		BufferedWriter bw_0 = new BufferedWriter(new FileWriter(new File(tmp_0)));
		BufferedWriter bw_1 = new BufferedWriter(new FileWriter(new File(tmp_1)));
		BufferedWriter bw_2 = new BufferedWriter(new FileWriter(new File(tmp_2)));
		BufferedWriter bw_3 = new BufferedWriter(new FileWriter(new File(tmp_3)));
		BufferedWriter bw_4 = new BufferedWriter(new FileWriter(new File(tmp_4)));
		BufferedWriter bw_5 = new BufferedWriter(new FileWriter(new File(tmp_5)));
		
		ArrayList<String> all_genes = new ArrayList<>();
		ArrayList<String> all_labtests = new ArrayList<>();
		ArrayList<String> all_diagnosis = new ArrayList<>();
		ArrayList<String> all_medication = new ArrayList<>();
		ArrayList<String> all_history = new ArrayList<>();

		ArrayList<BigInteger> valide_patients = new ArrayList<>();

		ArrayList<String> all_tumor = new ArrayList<>();
		
		all_tumor.add("153.9"); //   Malignant neoplasm of colon, unspecified site
		all_tumor.add("155"); //    Malignant neoplasm of liver, primary
		all_tumor.add("162.9"); //    PMalignant neoplasm of bronchus and lung, unspecified
		all_tumor.add("157.9"); //    Malignant neoplasm of pancreas, part unspecified
		all_tumor.add("174.9"); //     Malignant neoplasm of breast (female), unspecified
		all_tumor.add("171.9"); //    Malignant neoplasm of connective and other soft tissue, site unspecified
		all_tumor.add("193"); //    Malignant neoplasm of thyroid gland
		all_tumor.add("183"); //    Malignant neoplasm of ovary
		all_tumor.add("185"); //    Malignant neoplasm of prostate
		
		List<String> text_2 = new ArrayList<String>(); // history
		
		HashMap<String, HashMap<BigInteger, String>> tmp_test = new HashMap<>();
		update_others(data_genes, all_genes);
		update_others(data_diagnosis, all_diagnosis);
		update_others(data_medications, all_medication);
		update_others(data_historicals, all_history);
		update_lab(data_labtests, all_labtests);
		
		bw_0.write("@relation 'associations'\n");
		bw_1.write("@relation 'associations'\n");
		bw_2.write("@relation 'associations'\n");
		bw_3.write("@relation 'associations'\n");
		bw_4.write("@relation 'associations'\n");
		bw_5.write("@relation 'associations'\n");
		
		bw_0.write("@attribute 'Sex' { 'female', 'male' } \n");
		bw_0.write("@attribute 'Age' { 'very_old', 'old' , 'middle_age' , 'senior' , 'junior' } \n");
		
		
		for (int i = 0; i < all_genes.size(); i++) {
			bw_1.write("@attribute 'Gene(" + all_genes.get(i) + ")' { 'TRUE', 'FALSE' } \n");
		}
		
		for (int i = 0; i < all_diagnosis.size(); i++) {
			bw_2.write("@attribute 'Diagnosis(" + all_diagnosis.get(i) + ")' { 'TRUE', 'FALSE' } \n");
		}
		
		for (int i = 0; i < all_medication.size(); i++) {
			bw_3.write("@attribute 'Medication(" + all_medication.get(i) + ")' { 'TRUE', 'FALSE' } \n");
		}
		for (int i = 0; i < all_history.size(); i++) {
			bw_4.write("@attribute 'History(" + all_history.get(i) + ")' { 'TRUE', 'FALSE' } \n");
		}
		for (int i = 0; i < all_labtests.size(); i++) {
			bw_5.write("@attribute 'Labtest_" + all_labtests.get(i) + "' { 'TRUE', 'FALSE' } \n");
		}


		StringBuffer sb_4 = new StringBuffer();
		for (int i = 0; i < all_tumor.size(); i++) {
			sb_4.append("'" + all_tumor.get(i) + "'").append(",");
		}
		String value=sb_4.toString().trim();
		value=value.substring(0,value.length()-1);
		
		bw_0.write("@attribute 'Class' { " + value + "} \n");
		bw_1.write("@attribute 'Class' { " + value + "} \n");
		bw_2.write("@attribute 'Class' { " + value + "} \n");
		bw_3.write("@attribute 'Class' { " + value + "} \n");
		bw_4.write("@attribute 'Class' { " + value + "} \n");
		bw_5.write("@attribute 'Class' { " + value + "} \n");

		bw_0.write("@data\n");
		bw_1.write("@data\n");
		bw_2.write("@data\n");
		bw_3.write("@data\n");
		bw_4.write("@data\n");
		bw_5.write("@data\n");
		
		for(BigInteger patient:patientList){
			
			StringBuffer sb =new StringBuffer();
			Prediciton_PatientBean bean=data_patient.get(patient);
			String label=bean.getSubmittedDiagnosis_general();
			
			if(!all_tumor.contains(label)){
				continue;
			}
			String sex= data_sex.get(patient);
			String age= data_age.get(patient);
			
			sb.append("'").append(sex).append("',");
			sb.append("'").append(age).append("',");
			sb.append("'" + label + "'");
			bw_0.write(sb.toString() + "\n");
		}
		bw_0.flush();
		bw_0.close();
		
		
		for(BigInteger patient:patientList){
			
			StringBuffer sb =new StringBuffer();
			Prediciton_PatientBean bean=data_patient.get(patient);
			String label=bean.getSubmittedDiagnosis_general();
			
			if(!all_tumor.contains(label)){
				continue;
			}
			
			HashSet<String> local_genes=new HashSet<>();
			local_genes.add("null");
			if(data_genes.containsKey(patient)){
				local_genes=data_genes.get(patient);
			}
			
			for (int i = 0; i < all_genes.size(); i++) {
				String gene = all_genes.get(i);
				if (local_genes.contains(gene)) {
					sb.append("'TRUE'").append(",");
				} else {
					sb.append("'FALSE'").append(",");
				}
			}
			sb.append("'" + label + "'");
			bw_1.write(sb.toString() + "\n");
		}
		bw_1.flush();
		bw_1.close();
		
		
		
		for(BigInteger patient:patientList){
			
			StringBuffer sb =new StringBuffer();
			Prediciton_PatientBean bean=data_patient.get(patient);
			String label=bean.getSubmittedDiagnosis_general();
			if(!all_tumor.contains(label)){
				continue;
			}
			
			HashSet<String> local_diagnosis=new HashSet<>();
			local_diagnosis.add("null");
			if(data_diagnosis.containsKey(patient)){
				local_diagnosis=data_diagnosis.get(patient);
			}
			
			for (int i = 0; i < all_diagnosis.size(); i++) {
				String diagnosis = all_diagnosis.get(i);
				if (local_diagnosis.contains(diagnosis)) {
					sb.append("'TRUE'").append(",");
				} else {
					sb.append("'FALSE'").append(",");
				}
			}
			sb.append("'" + label + "'");
			bw_2.write(sb.toString() + "\n");
		}
		bw_2.flush();
		bw_2.close();
		
		for(BigInteger patient:patientList){
			StringBuffer sb =new StringBuffer();
			Prediciton_PatientBean bean=data_patient.get(patient);
			String label=bean.getSubmittedDiagnosis_general();
			if(!all_tumor.contains(label)){
				continue;
			}
			
			HashSet<String> local_medication=new HashSet<>();
			local_medication.add("null");
			if(data_medications.containsKey(patient)){
				local_medication=data_medications.get(patient);
			}
			
			for (int i = 0; i < all_medication.size(); i++) {
				String medication = all_medication.get(i);
				if (local_medication.contains(medication)) {
					sb.append("'TRUE'").append(",");
				} else {
					sb.append("'FALSE'").append(",");
				}
			}
			sb.append("'" + label + "'");
			bw_3.write(sb.toString() + "\n");
		}
		bw_3.flush();
		bw_3.close();
		
		
		for(BigInteger patient:patientList){
			StringBuffer sb =new StringBuffer();
			Prediciton_PatientBean bean=data_patient.get(patient);
			String label=bean.getSubmittedDiagnosis_general();
			
			if(!all_tumor.contains(label)){
				continue;
			}
			
			HashSet<String> local_history=new HashSet<>();
			local_history.add("null");
			if(data_historicals.containsKey(patient)){
				local_history=data_historicals.get(patient);
			}
			
			for (int i = 0; i < all_history.size(); i++) {
				String history = all_history.get(i);
				if (local_history.contains(history)) {
					sb.append("'TRUE'").append(",");
				} else {
					sb.append("'FALSE'").append(",");
				}
			}
			sb.append("'" + label + "'");
			bw_4.write(sb.toString() + "\n");
		}
		bw_4.flush();
		bw_4.close();
		
		for(BigInteger patient:patientList){
			StringBuffer sb =new StringBuffer();
			Prediciton_PatientBean bean=data_patient.get(patient);
			String label=bean.getSubmittedDiagnosis_general();
			
			if(!all_tumor.contains(label)){
				continue;
			}
			
			HashMap<String, LabtestBean>orignial_labtest=new HashMap<>();
			if(data_labtests.containsKey(patient)){
				orignial_labtest=data_labtests.get(patient);
			}
			HashMap<String, LabtestBean>local_labtest=new HashMap<>();
			
			for (int i = 0; i < all_labtests.size(); i++) {
				String labtest = all_labtests.get(i);
				String lab_code=labtest.substring(0,labtest.lastIndexOf("("));
				String lab_value=labtest.substring(labtest.lastIndexOf("(")+1,labtest.lastIndexOf(")"));
				if(orignial_labtest.containsKey(lab_code)){
					local_labtest.put(lab_code, orignial_labtest.get(lab_code));
				}else{
					LabtestBean add_bean= new LabtestBean();
					add_bean.setCode(lab_code);
					add_bean.setValue("null");
					local_labtest.put(lab_code, add_bean);
				}
			}
			
			
			for (int i = 0; i < all_labtests.size(); i++) {
				String labtest = all_labtests.get(i);
				boolean ou=false;
				for(Entry<String,LabtestBean> entry_2:local_labtest.entrySet()){
					if(labtest.contains(entry_2.getKey())&&labtest.contains(entry_2.getValue().getValue())){
						ou=true;
					}
				}
				
				if (ou) {
					sb.append("'TRUE'").append(",");
				} else {
					sb.append("'FALSE'").append(",");
				}
			}
			sb.append("'" + label + "'");
			bw_5.write(sb.toString() + "\n");
		}
		bw_5.flush();
		bw_5.close();
		
		
		HashSet<String> files=new HashSet<>();
		files.add(tmp_1);
		files.add(tmp_2);
		files.add(tmp_3);
		files.add(tmp_4);
		files.add(tmp_5);
		
		ArrayList<String> sublist=new ArrayList<>();
		StringBuffer sb =new StringBuffer();
		for(String string:files){
			sublist.add(string);
			String name=new File(string).getName();
			name=name.replace("_tmp.arff", "");
			sb.append(name).append("_");
		}

		FeatureSelection.featureReduction_multiple_allreduced(sublist,tmp_0,
					sb.toString()+reducted_dimension+"_allreduced.arff",
					FeatureSelection.information_gain,reducted_dimension);
	}
	
	
	
		
	public static void writeFile(HashMap<String,Integer> network_idx,ArrayList<BigInteger> patient_idx, String file, String outfile) throws Exception{
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile)));
		DataSource source = new DataSource(file);
		Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes()-1);
		for (int i = 0; i < data.numInstances(); i++) {
			StringBuffer sb =new StringBuffer();
			Instance ins=data.get(i);
			for (int j = 0; j < data.numAttributes()-1; j++) {
				sb.append(ins.value(j)).append(" ");
			}
			String class_value=data.classAttribute().value((int)ins.value(ins.classIndex()));
			sb.append(class_value);
			String node="Patient<"+patient_idx.get(i)+">";
			if(network_idx.containsKey(node)){
				bw.write(network_idx.get(node)+" "+sb.toString().trim()+"\n");
			}else{
				System.err.println("network idex missing from arff making : "+patient_idx.get(i));
				System.exit(0);
			}
			
			
		}
		bw.flush();
		bw.close();
	}
	
	public static HashMap<BigInteger, String> readLabel(String label_file) throws IOException{
		BufferedReader br =new BufferedReader(new FileReader(new File(label_file)));
		String line=null;
		HashMap<BigInteger, String> map=new HashMap<>();
		
		while((line=br.readLine())!=null){
			String[] elements=line.split(" ");
			map.put(new BigInteger(elements[0]), elements[1]);
		}
		br.close();
		return map;
	}
	
	public static HashMap<BigInteger, String> readSex(String sex_file) throws IOException{
		BufferedReader br =new BufferedReader(new FileReader(new File(sex_file)));
		String line=null;
		HashMap<BigInteger, String> map=new HashMap<>();
		
		while((line=br.readLine())!=null){
			String[] elements=line.split(" ");
			map.put(new BigInteger(elements[0]), elements[1]);
		}
		br.close();
		return map;
	}
	
	public static HashMap<BigInteger, String> readAge(String age_file) throws IOException{
		BufferedReader br =new BufferedReader(new FileReader(new File(age_file)));
		String line=null;
		HashMap<BigInteger, String> map=new HashMap<>();
		
		while((line=br.readLine())!=null){
			String[] elements=line.split(" ");
			map.put(new BigInteger(elements[0]), elements[1]);
		}
		br.close();
		return map;
	}
	
	public static HashMap<String, BigInteger> readnode_patient(String node_idx_file) throws IOException{
		
		HashMap<String, BigInteger> map=new HashMap<>();
		BufferedReader br =new BufferedReader(new FileReader(new File(node_idx_file)));
		String line=null;
		
		while((line=br.readLine())!=null){
			String[] elements=line.split("\t");
//			System.out.println(line);
			if(line.contains("Patient")){
				String id=elements[1].substring(elements[1].indexOf("<")+1,elements[1].indexOf(">"));
				map.put(elements[0], new BigInteger(id));	
			}
			
		}
		br.close();
		return map;
	}
	
	
	public static HashMap<BigInteger, ArrayList<String>> readembedding(String embedding, HashMap<String,BigInteger> node_idex) throws IOException{
		System.out.println("read embedding: "+embedding);
		HashMap<BigInteger, ArrayList<String>> embeddings=new HashMap<>();
		HashMap<String, BigInteger> map=new HashMap<>();
		BufferedReader br =new BufferedReader(new FileReader(new File(embedding)));
		String line=null;
		int counter=0;
		while((line=br.readLine())!=null){
			counter++;
			if(counter==1){
				continue;
			}
				
			String[] elements=line.split(" ");
			ArrayList<String> list=new ArrayList<>();
			for (int j = 1; j < elements.length; j++) {
				list.add(elements[j]);
			}
			if(node_idex.containsKey(elements[0])){
				embeddings.put(node_idex.get(elements[0]), list);	
			}
		}
		br.close();
		return embeddings;
	}

	
	/**
	 * featureSourceType 1= genetic only
	 * 
	 * featureSourceType 2= labtest only
	 * 
	 * featureSourceType 3= diagnosis only
	 * 
	 * featureSourceType 4= medication only
	 *
	 * featureSourceType 5= historical only
	 * 
	 * 
	 * @param data_map
	 * @param type
	 * @param arffTrain
	 * @throws Exception
	 */
	
	
	public static ArrayList<String> getAverage(HashMap<BigInteger, ArrayList<String>> embeddings){
		ArrayList<Double> average=new ArrayList<>();
		ArrayList<String> average_return=new ArrayList<>();
		int i=0;
		for(Entry<BigInteger, ArrayList<String>> entry:embeddings.entrySet()){
			
			i++;
			if(i==1){
				for (int j = 0; j < entry.getValue().size(); j++) {
					average.add(j, Double.valueOf(entry.getValue().get(j)));
				}
			}else{
				for (int j = 0; j < entry.getValue().size(); j++) {
					Double tmp=average.get(j)+Double.valueOf(entry.getValue().get(j));
					average.set(j, tmp);
				}
			}
		}
		
		for (int j = 0; j < average.size(); j++) {
			average_return.add(j,String.valueOf(average.get(j)/embeddings.size()));
		}
		
		
		return average_return;
	}
	
	
	public static void writeARFF_embedding(String sex_file, String age_file, String label_file, String node_idx_file,String embedding_file,
			String outfile_arff) throws Exception {
		
		HashMap<BigInteger, String> sex=readSex(sex_file) ;
		HashMap<BigInteger, String> age=readAge(age_file) ;
		HashMap<BigInteger, String> lable=readLabel(label_file) ;
		HashMap<String, BigInteger> node_idx=readnode_patient(node_idx_file);
		HashMap<BigInteger, ArrayList<String>> embeddings=readembedding( embedding_file, node_idx);
		ArrayList<String> average_embedding=getAverage(embeddings);
		int embedding_size=0;
		for(Entry<BigInteger,ArrayList<String>> entry:embeddings.entrySet()){
			embedding_size=entry.getValue().size();
			break;
		}
		
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile_arff)));
		bw.write("@relation 'associations'\n");
		
	
		bw.write("@attribute 'Sex' { 'female', 'male' } \n");
		bw.write("@attribute 'Age' { 'very_old', 'old' , 'middle_age' , 'senior' , 'junior' } \n");
		
		for (int i = 0; i < embedding_size; i++) {
			bw.write("@attribute 'Attribute_" + i + "' numeric \n");
		}

		ArrayList<String> all_tumor = new ArrayList<>();
		
		all_tumor.add("153.9"); //   Malignant neoplasm of colon, unspecified site
		all_tumor.add("155"); //    Malignant neoplasm of liver, primary
		all_tumor.add("162.9"); //    PMalignant neoplasm of bronchus and lung, unspecified
		all_tumor.add("157.9"); //    Malignant neoplasm of pancreas, part unspecified
		all_tumor.add("174.9"); //     Malignant neoplasm of breast (female), unspecified
		all_tumor.add("171.9"); //    Malignant neoplasm of connective and other soft tissue, site unspecified
		all_tumor.add("193"); //    Malignant neoplasm of thyroid gland
		all_tumor.add("183"); //    Malignant neoplasm of ovary
		all_tumor.add("185"); //    Malignant neoplasm of prostate
		
		StringBuffer sb_4 = new StringBuffer();
		for (int i = 0; i < all_tumor.size(); i++) {
			sb_4.append("'" + all_tumor.get(i) + "'").append(",");
		}
		String value=sb_4.toString().trim();
		value=value.substring(0,value.length()-1);
		
		bw.write("@attribute 'Class' { " + value + "} \n");

		bw.write("@data\n");
		
		for(Entry<BigInteger, String> entry:lable.entrySet()){
			StringBuffer sb =new StringBuffer();
			
			String features=null;
					
			if(!all_tumor.contains(entry.getValue())){
				continue;
			}
					if(sex.containsKey(entry.getKey())&&age.containsKey(entry.getKey())){
						features=sex.get(entry.getKey())+" "+age.get(entry.getKey())+" ";
					}else{
						System.err.println("not contain id (sex & age): "+entry.getKey());
						System.exit(0);
					}
				
					if(embeddings.containsKey(entry.getKey())){
						ArrayList<String> list=embeddings.get(entry.getKey());
						
						StringBuffer sb_e = new StringBuffer();
						for (int i = 0; i < list.size(); i++) {
							sb_e.append(list.get(i)).append(" ");
						}
						features+=sb_e.toString().trim();
						
					}else{
						System.err.println("not contain id in embdding: "+entry.getKey());
						System.exit(0);
					}
			
					bw.write(features+" "+entry.getValue()+ "\n");
		}
		bw.flush();
		bw.close();
		
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
	 
	 public static Instances removeDemograph(Instances inst) throws Exception {
		  Remove af = new Remove();
		  Instances retI = null;
		      int[] ranges=new int[2];
		      ranges[0]= inst.attribute("Sex").index();
		      ranges[1]= inst.attribute("Age").index();
		      System.out.println("ranges[0]: "+ranges[0]);
		      System.out.println("ranges[1]: "+ranges[1]);
		      af.setAttributeIndicesArray(ranges);
		      af.setInvertSelection(false);
		      af.setInputFormat(inst);
		      retI = Filter.useFilter(inst, af);
		 
		  return retI;
		}
	 
	public static void merge(String file_1,String file_2, String outfile) throws Exception {
		Instances instance_1=getInstance(file_1);
		Instances instance_2_tmp=getInstance(file_2);
		Instances instance_2=removeClass(instance_2_tmp);
		Instances instance_3=removeDemograph(instance_2) ;
		Instances instance_final=Instances.mergeInstances(instance_3, instance_1);
		instance_final.setClassIndex(instance_final.numAttributes()-1);
		
		DataSink.write(outfile, instance_final);
	}
	
	
	
	public static void writeARFF_embedding_average(ArrayList<BigInteger> p_list, String sex_file, String age_file, String label_file, String node_idx_file,String embedding_file,
			String outfile_arff) throws Exception {
		
		HashMap<BigInteger, String> sex=readSex(sex_file) ;
		HashMap<BigInteger, String> age=readAge(age_file) ;
		HashMap<BigInteger, String> lable=readLabel(label_file) ;
		HashMap<String, BigInteger> node_idx=readnode_patient(node_idx_file);
		HashMap<BigInteger, ArrayList<String>> embeddings=readembedding( embedding_file, node_idx);
		ArrayList<String> average_embedding=getAverage(embeddings);
		int embedding_size=0;
		for(Entry<BigInteger,ArrayList<String>> entry:embeddings.entrySet()){
			embedding_size=entry.getValue().size();
			break;
		}
		
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile_arff)));
		bw.write("@relation 'associations'\n");
		
	
		bw.write("@attribute 'Sex' { 'female', 'male' } \n");
		bw.write("@attribute 'Age' { 'very_old', 'old' , 'middle_age' , 'senior' , 'junior' } \n");
		
		for (int i = 0; i < embedding_size; i++) {
			bw.write("@attribute 'Attribute_" + i + "' numeric \n");
		}

		ArrayList<String> all_tumor = new ArrayList<>();
		
		all_tumor.add("153.9"); //   Malignant neoplasm of colon, unspecified site
		all_tumor.add("155"); //    Malignant neoplasm of liver, primary
		all_tumor.add("162.9"); //    PMalignant neoplasm of bronchus and lung, unspecified
		all_tumor.add("157.9"); //    Malignant neoplasm of pancreas, part unspecified
		all_tumor.add("174.9"); //     Malignant neoplasm of breast (female), unspecified
		all_tumor.add("171.9"); //    Malignant neoplasm of connective and other soft tissue, site unspecified
		all_tumor.add("193"); //    Malignant neoplasm of thyroid gland
		all_tumor.add("183"); //    Malignant neoplasm of ovary
		all_tumor.add("185"); //    Malignant neoplasm of prostate
		
		StringBuffer sb_4 = new StringBuffer();
		for (int i = 0; i < all_tumor.size(); i++) {
			sb_4.append("'" + all_tumor.get(i) + "'").append(",");
		}
		String value=sb_4.toString().trim();
		value=value.substring(0,value.length()-1);
		
		bw.write("@attribute 'Class' { " + value + "} \n");

		bw.write("@data\n");
		
		for(BigInteger patient:p_list ){
			
			String label =lable.get(patient);
			
			if(!all_tumor.contains(label)){
				continue;
			}
			StringBuffer sb =new StringBuffer();
			
			String features=null;
			
					if(sex.containsKey(patient)&&age.containsKey(patient)){
						features=sex.get(patient)+" "+age.get(patient)+" ";
					}else{
						
					}
				
					if(embeddings.containsKey(patient)){
						ArrayList<String> list=embeddings.get(patient);
						
						StringBuffer sb_e = new StringBuffer();
						for (int i = 0; i < list.size(); i++) {
							sb_e.append(list.get(i)).append(" ");
						}
						features+=sb_e.toString().trim();
						
					}else{
						StringBuffer sb_t = new StringBuffer();
						for (int i = 0; i < average_embedding.size(); i++) {
							sb_t.append(average_embedding.get(i)).append(" ");
						}
						features+=sb_t.toString().trim();
					}
			
					bw.write(features+" "+label+ "\n");
		}
		bw.flush();
		bw.close();
		
	}
	
	
	
}
